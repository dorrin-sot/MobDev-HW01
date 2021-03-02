package com.mobdev.currencyapp.View;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mobdev.currencyapp.Model.Coin;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static android.os.Build.VERSION_CODES.N;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.mobdev.currencyapp.Model.Coin.getCoin;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;
import static java.lang.Thread.sleep;

public class CurrencyListActivity extends AppCompatActivity {
    private static CurrencyListActivity currencyListActivity;
    Handler handler = new Handler();
    final int loadCoins = 1;

    ThreadPoolExecutor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currencyListActivity = this;
        setContentView(layout.activity_currency_list);
        findViewById(id.loadNext10Btn).setOnClickListener(v -> loadNext10());
        findViewById(id.refreshBtn).setOnClickListener(v -> refreshAndStartOver());

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    public void refreshAndStartOver() {
        // todo clear recyclerview
        loadNext10();
    }

    public void loadNext10() {
        Message message = new Message();
        message.what = loadCoins;
        message.arg1 = 5; // start
        message.arg2 = 10; // number of coins to load
        handler.sendMessage(message);
    }

    @RequiresApi(api = N)
    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case loadCoins:
                    int start = msg.arg1, num = msg.arg2, end = start + num;
                    for (int id = start; id <= end; id++) {
                        int finalId = id;
                        executor.execute(() -> {
                            Coin coin = getCoin(finalId);
                            System.out.println("Start: " + coin.getName() + " " + coin.getId());
                            try {
                                sleep(finalId * 200);
                                runOnUiThread(() -> addProgress(finalId - start, num));
                                System.out.println("End: " + coin.getName() + " " + coin.getId());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    break;
            }
            return true;
        });
    }

    @RequiresApi(api = N)
    synchronized void addProgress(int doneCount, int allCount) {
        ProgressBar progressBar = findViewById(id.loadingProgressBar);
        if (progressBar.getMax() != allCount)
            progressBar.setMax(allCount);

        if (progressBar.getVisibility() == INVISIBLE)
            progressBar.setVisibility(VISIBLE);
        progressBar.setProgress(doneCount, true);
        if (doneCount + 1 > progressBar.getMax()) {
            progressBar.setVisibility(INVISIBLE);
            progressBar.setProgress(0, false);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public static CurrencyListActivity getInstance() {
        return currencyListActivity;
    }
}