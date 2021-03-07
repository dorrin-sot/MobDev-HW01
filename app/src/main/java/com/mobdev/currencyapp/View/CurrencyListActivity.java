package com.mobdev.currencyapp.View;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.CandleEntry;
import com.mobdev.currencyapp.Controller.DatabaseHandler;
import com.mobdev.currencyapp.Model.Coin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static android.os.Build.VERSION_CODES.N;
import static android.os.Build.VERSION_CODES.O;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.mobdev.currencyapp.Model.Coin.getCoin;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;
import static com.mobdev.currencyapp.View.MyCoinListRecyclerViewAdapter.getCoins;
import static com.mobdev.currencyapp.View.OhlcDialogFragment.newInstance;
import static java.time.LocalDate.now;

public class CurrencyListActivity extends AppCompatActivity {
    static Handler handler = new Handler();
    public static final int loadCoins = 1, openOhlcPage = 2;
    public static DatabaseHandler dataBaseHandler ;
    static ThreadPoolExecutor executer;

    RecyclerView recyclerView;
    MyCoinListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_list);
        findViewById(id.loadNext10Btn).setOnClickListener(v -> loadNext10());
        findViewById(id.refreshBtn).setOnClickListener(v -> refreshAndStartOver());
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
        message.arg1 = getCoins().size()+1; // start
        message.arg2 = 10; // number of coins to load
        handler.sendMessage(message);
    }

    @RequiresApi(api = O)
    @Override
    protected void onResume() {
        super.onResume();
        AtomicInteger numberOfDialogsOpened = new AtomicInteger();
        handler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case loadCoins: {
                    int start = msg.arg1, num = msg.arg2, end = start + num;
                    runOnUiThread(() -> {
                        ProgressBar progressBar = findViewById(id.loadingProgressBar);
                        progressBar.setProgress(0, true);
                        progressBar.setMax(num);
                        progressBar.setVisibility(VISIBLE);
                    });
                    for (int rank = start; rank < end; rank++) {
                        int finalRank = rank;
                        executer.execute(() -> {
                            Coin coin = getCoin(finalRank);
                            runOnUiThread(() -> {
                                adapter.addCoinObj(coin);
                                addProgress();
                            });
                        });
                    }
                }
                break;
                case openOhlcPage: {
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
        System.out.println(progressBar.getProgress() + ", " + progressBar.getMax());

        progressBar.setProgress(progressBar.getProgress() + 1, true);

        if (progressBar.getProgress() == progressBar.getMax() - 1) {
            progressBar.setVisibility(INVISIBLE);
            progressBar.setProgress(0, false);
            System.out.println(progressBar.getProgress() + ", " + progressBar.getMax());
        }
    }

    public static Handler getHandler() {
        return handler;
    }
    public static ThreadPoolExecutor getExecuter(){
        return executer;
    }

}