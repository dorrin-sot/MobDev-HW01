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
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.data.CandleEntry;
import com.mobdev.currencyapp.Controller.DatabaseHandler;
import com.mobdev.currencyapp.Model.Coin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static android.os.Build.VERSION_CODES.N;
import static android.os.Build.VERSION_CODES.O;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.mobdev.currencyapp.Model.Coin.getCoin;
import static com.mobdev.currencyapp.R.drawable;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;
import static com.mobdev.currencyapp.View.MyCoinListRecyclerViewAdapter.getCoins;
import static com.mobdev.currencyapp.View.OhlcDialogFragment.newInstance;
import static java.time.LocalDate.now;

public class CurrencyListActivity extends AppCompatActivity {
    static Handler handler = new Handler();
    public static final int loadCoins = 1, openOhlcPage = 2;
    public static DatabaseHandler dataBaseHandler;
    static ThreadPoolExecutor executer;

    RecyclerView recyclerView;
    MyCoinListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_list);
        findViewById(id.loadNext10Btn).setOnClickListener(v -> loadNext10());
        findViewById(id.refreshBtn).setOnClickListener(v -> refreshAndStartOver());

        ImageView noConnectionImgView = (ImageView) findViewById(id.noConnectionImg);
        Glide.with(this)
                .load("https://image.freepik.com/free-vector/no-internet-connection-sign_79145-136.jpg")
                .centerCrop()
                .override(1500)
                .placeholder(drawable.no_connection)
                .into(noConnectionImgView);
        noConnectionImgView.setVisibility(isConnectedToInternet() ? INVISIBLE : VISIBLE);

        System.out.println("isConnectedToInternet() = " + isConnectedToInternet());

        dataBaseHandler = new DatabaseHandler(this);

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

    @RequiresApi(api = O)
    @Override
    protected void onStart() {
        super.onStart();

        AtomicBoolean loadFromCache = new AtomicBoolean(false),
                buttonClicked = new AtomicBoolean(false);
        AtomicInteger numberOfDialogsOpened = new AtomicInteger();

        handler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case loadCoins: {
                    if (!buttonClicked.get()) {
                        buttonClicked.set(true);

                        int start = msg.arg1, num = msg.arg2, end = start + num;
                        runOnUiThread(() -> {
                            ProgressBar progressBar = findViewById(id.loadingProgressBar);
                            progressBar.setProgress(0, true);
                            progressBar.setMax(num);
                            progressBar.setVisibility(VISIBLE);
                            findViewById(id.noConnectionImg).setVisibility(isConnectedToInternet() ? INVISIBLE : VISIBLE);
                        });
                        loadFromCache.set(canLoadFromCache());
                        for (int rank = start; rank < end; rank++) {
                            int finalRank = rank;
                            executer.execute(() -> {
                                Coin coin;
                                /*
                                if "n" internet     "n" cache   show no connection image
                                if "n" internet     "y" cache   get from cache
                                if "y" internet                 get from api
                                 */
                                if (loadFromCache.get()) {
                                    coin = dataBaseHandler.getCoin(finalRank);

                                    Coin finalCoin = coin;
                                    runOnUiThread(() -> {
                                        adapter.addCoinObj(finalCoin);
                                        addProgress();
                                    });
                                }
                                if (isConnectedToInternet()) {
                                    coin = getCoin(finalRank);
//                                    if (dataBaseHandler.coinExists(coin))
//                                        dataBaseHandler.updateCoin(coin);
//                                    else
                                    dataBaseHandler.addCoin(coin);

                                    Coin finalCoin1 = coin;
                                    runOnUiThread(() -> {
                                        adapter.addCoinObj(finalCoin1);
                                        addProgress();
                                    });
                                }
                            });
                        }
                        buttonClicked.set(false);
                    }
                }
                break;
                case openOhlcPage: {
                    if (!buttonClicked.get()) {
                        buttonClicked.set(true);

                        runOnUiThread(() -> findViewById(id.noConnectionImg).setVisibility(isConnectedToInternet() ? INVISIBLE : VISIBLE));

                        Coin coin = (Coin) msg.obj;
                        executer.execute(() -> {
                            LocalDate date = now().minusDays(7)
                                    .minusDays(1); // to exclude today
                            ArrayList<CandleEntry> ohlcData1Week = coin.generateRandomOHLCData(date, now().minusDays(1));
                            date = now().minusMonths(1)
                                    .minusDays(1); // to exclude today
                            ArrayList<CandleEntry> ohlcData1Month = coin.generateRandomOHLCData(date, now().minusDays(1));

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
                        });
                        buttonClicked.set(false);
                    }
                }
                break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
            return true;
        });
    }

    @RequiresApi(api = N)
    synchronized void addProgress() {
        ProgressBar progressBar = findViewById(id.loadingProgressBar);
//        System.out.println(progressBar.getProgress() + ", " + progressBar.getMax());

        progressBar.setProgress(progressBar.getProgress() + 1, true);

        if (progressBar.getProgress() == progressBar.getMax() - 1) {
            progressBar.setVisibility(INVISIBLE);
            progressBar.setProgress(0, false);
//            System.out.println(progressBar.getProgress() + ", " + progressBar.getMax());
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
        return !isConnectedToInternet() || dataBaseHandler.getCoinCount() != 0;
    }

//    @RequiresApi(api = N)
//    public boolean coinHasBeenCachedBefore(Coin coin) {
//        return dataBaseHandler.coinExists(coin);
//    }

    public static Handler getHandler() {
        return handler;
    }

    public static ThreadPoolExecutor getExecuter() {
        return executer;
    }
}