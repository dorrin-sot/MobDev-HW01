package com.mobdev.currencyapp.View;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.CandleEntry;
import com.mobdev.currencyapp.Model.Coin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

import static android.os.Build.VERSION_CODES.N;
import static android.os.Build.VERSION_CODES.O;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.mobdev.currencyapp.Controller.DatabaseHandler.getInstance;
import static com.mobdev.currencyapp.Model.Coin.getCoin;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;
import static com.mobdev.currencyapp.View.MyCoinListRecyclerViewAdapter.getCoins;
import static com.mobdev.currencyapp.View.OhlcDialogFragment.newInstance;
import static java.lang.Math.toIntExact;
import static org.threeten.bp.LocalDate.now;

public class CurrencyListActivity extends AppCompatActivity {
    static Handler handler = new Handler();
    public static final int loadCoins = 1, openOhlcPage = 2, proceedProgressBar = 3;
    static ThreadPoolExecutor executer;

    public static LinkedList<Integer> availableIDs = new LinkedList<>();

    RecyclerView recyclerView;
    MyCoinListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_list);
        findViewById(id.loadNext10Btn).setOnClickListener(v -> loadNext10());
        findViewById(id.refreshBtn).setOnClickListener(v -> refreshAndStartOver());

//        ImageView noConnectionImgView = (ImageView) findViewById(id.noConnectionImg);
//        Glide.with(this)
//                .load("https://image.freepik.com/free-vector/no-internet-connection-sign_79145-136.jpg")
//                .thumbnail()
//                .centerCrop()
//                .override(1500)
//                .placeholder(drawable.no_connection)
//                .into(noConnectionImgView);
//        noConnectionImgView.setVisibility(shouldShowNoInternetPic() ? VISIBLE : INVISIBLE);
        if (shouldShowNoInternetPic())
            showCantConnectToInternetToast();

        System.out.println("isConnectedToInternet() = " + isConnectedToInternet());

        executer = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        recyclerView = findViewById(id.coinRecyclerView);
        recyclerView.setAdapter(new MyCoinListRecyclerViewAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), VERTICAL));
        adapter = (MyCoinListRecyclerViewAdapter) recyclerView.getAdapter();

        findViewById(id.refreshBtn).performClick();
    }

    public void refreshAndStartOver() {
        adapter.refreshCoinList();
        loadNext10();
    }

    public void loadNext10() {
        Message message = new Message();
        message.what = loadCoins;
        message.arg1 = getCoins().size() + 1; // start
        message.arg2 = 10; // number of coins to load
        handler.sendMessage(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getInstance(this).getReadableDatabase().close();
        getInstance(this).getWritableDatabase().close();
    }

    @RequiresApi(api = O)
    @Override
    protected void onStart() {
        super.onStart();

        executer.execute(() -> {
            if (isConnectedToInternet())
                try {
                    OkHttpClient coinClient = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://pro-api.coinmarketcap.com/v1/cryptocurrency/map").newBuilder();
                    urlBuilder.addQueryParameter("sort", "id");
                    Request request = new Builder()
                            .addHeader("X-CMC_PRO_API_KEY", "a7f4c18e-942e-4ace-971b-10c34e92806d")
                            .addHeader("Accept", "application/json")
                            .url(urlBuilder.build().toString())
                            .build();
                    Response coinResponse = coinClient.newCall(request).execute();
                    JSONArray idsFromApi = new JSONObject(coinResponse.body().string()).getJSONArray("data");
                    for (int i = 0; i < idsFromApi.length(); i++) {
                        availableIDs.addLast(idsFromApi.getJSONObject(i).getInt("id"));
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            else
                availableIDs = getInstance(this).getAvailableIDs();
            availableIDs.forEach(id -> System.out.print(id + "  "));
            System.out.println();
            runOnUiThread(() -> {
                findViewById(id.refreshBtn).setEnabled(true);
                findViewById(id.loadNext10Btn).setEnabled(true);
            });
        });

        AtomicBoolean loadFromCache = new AtomicBoolean(false), buttonClicked = new AtomicBoolean(false);
        AtomicInteger numberOfDialogsOpened = new AtomicInteger();

        handler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case loadCoins: {
//                    setAllButtonEnabled(false);
                    if (!buttonClicked.get()) {
                        buttonClicked.set(true);
                        if (shouldShowNoInternetPic()) {
                            showCantConnectToInternetToast();
                            buttonClicked.set(false);
                            break;
                        }

                        int start = msg.arg1, num = msg.arg2, end = start + num;
                        runOnUiThread(() -> {
                            ProgressBar progressBar = findViewById(id.loadingProgressBar);
                            progressBar.setProgress(0, true);
                            progressBar.setMax(num);
                            progressBar.setVisibility(VISIBLE);
//                            findViewById(id.noConnectionImg).setVisibility(shouldShowNoInternetPic() ? VISIBLE : INVISIBLE);
                        });

                        loadFromCache.set(canLoadFromCache());
                        for (int rank = start; rank < end; rank++) {
                            int finalRank = rank;
                            if (rank - 1 >= availableIDs.size()) {
                                runOnUiThread(() -> {
                                    for (int i = finalRank; i < end; i++)
                                        addProgress();
                                    Toast.makeText(this, "No more data in cache", LENGTH_LONG).show();
                                });
                                break;
                            }
                            int id = availableIDs.get(rank - 1);
                            executer.execute(() -> {
                                Coin coin;
                                /*
                                if "n" internet     "n" cache   show no connection image
                                if "n" internet     "y" cache   get from cache
                                if "y" internet                 get from api
                                 */
                                if (isConnectedToInternet()) {
                                    coin = getCoin(id, finalRank);
                                    if (getInstance(this).coinExists(id))
                                        getInstance(this).updateContact(coin);
                                    else
                                        getInstance(this).addCoin(coin);

                                    Coin finalCoin = coin;
                                    runOnUiThread(() -> {
                                        adapter.addCoinObj(finalCoin);
                                        addProgress();
                                    });
                                } else {
                                    if (getInstance(this).coinExists(id)) {
                                        coin = getInstance(this).getCoin(id, finalRank);
                                        Coin finalCoin = coin;
                                        System.out.println(coin.getName());
                                        runOnUiThread(() -> {
                                            adapter.addCoinObj(finalCoin);
                                            addProgress();
                                        });
                                    } else
                                        runOnUiThread(this::showCantConnectToInternetToast);
                                }
                                buttonClicked.set(false);
//                    setAllButtonEnabled(true);
                            });
                        }
                    }
                }
                break;
                case openOhlcPage: {
//                    setAllButtonEnabled(false);
                    if (!buttonClicked.get()) {
                        buttonClicked.set(true);
                        if (shouldShowNoInternetPic()) {
                            showCantConnectToInternetToast();
                            buttonClicked.set(false);
                            break;
                        }

                        Coin coin = (Coin) msg.obj;
                        int numberOfOhlcDataToLoad = 7 + toIntExact(now().toEpochDay() - now().minusMonths(1).toEpochDay());
                        runOnUiThread(() -> {
                            ProgressBar progressBar = findViewById(id.loadingProgressBar);
                            progressBar.setProgress(0, true);
                            progressBar.setMax(numberOfOhlcDataToLoad);
                            progressBar.setVisibility(VISIBLE);
//                            findViewById(id.noConnectionImg).setVisibility(shouldShowNoInternetPic() ? VISIBLE : INVISIBLE);
                        });
                        executer.execute(() -> {
                            ArrayList<CandleEntry> ohlcData1Month = coin.generateRandomOHLCData(
                                    numberOfOhlcDataToLoad - 7 // is number of days in last month
                            ), ohlcData1Week = coin.generateRandomOHLCData(7);

                            runOnUiThread(() -> {
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                Fragment prev = getFragmentManager().findFragmentById(id.ohlcDialogFragment);
                                if (prev != null)
                                    ft.remove(prev);
                                ft.addToBackStack(null);

                                DialogFragment ohlcDialog = newInstance(ohlcData1Week, ohlcData1Month, true, coin.getName());
                                String tag = "ohlc" + numberOfDialogsOpened.incrementAndGet();
                                System.out.println("tag = " + tag);
                                ohlcDialog.show(getSupportFragmentManager(), tag);
                            });
                            buttonClicked.set(false);
//                    setAllButtonEnabled(true);
                        });
                    }
                }
                break;
                case proceedProgressBar: {
                    addProgress();
                }
                break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    buttonClicked.set(false);
//                }
//            }, 500);
            return true;
        });
    }

    private void showCantConnectToInternetToast() {
        Toast.makeText(this, "Cannot Connect To The Internet", LENGTH_LONG).show();
    }

    private void setAllButtonEnabled(boolean enabled) {
        runOnUiThread(() -> {
            ((Button) findViewById(id.loadNext10Btn)).setEnabled(enabled);
            ((Button) findViewById(id.refreshBtn)).setEnabled(enabled);
        });
    }

    private boolean shouldShowNoInternetPic() {
        return !isConnectedToInternet() && getInstance(this).getCoinCount() == 0;
    }

    @RequiresApi(api = N)
    synchronized void addProgress() {
        ProgressBar progressBar = findViewById(id.loadingProgressBar);

        progressBar.setProgress(progressBar.getProgress() + 1, true);

        if (progressBar.getProgress() == progressBar.getMax() - 1) {
            progressBar.setVisibility(INVISIBLE);
            progressBar.setProgress(0, false);
        }
    }

    public boolean isConnectedToInternet() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean canLoadFromCache() {
        return !isConnectedToInternet() || getInstance(this).getCoinCount() != 0;
    }

//    @RequiresApi(api = N)
//    public boolean coinHasBeenCachedBefore(Coin coin) {
//        return dataBaseHandler.coinExists(coin);
//    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getProceedProgressBar() {
        return proceedProgressBar;
    }
}