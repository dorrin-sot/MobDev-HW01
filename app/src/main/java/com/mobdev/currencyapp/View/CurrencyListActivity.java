package com.mobdev.currencyapp.View;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdev.currencyapp.Model.Coin;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static android.os.Build.VERSION_CODES.N;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.mobdev.currencyapp.Model.Coin.getCoin;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;
import static com.mobdev.currencyapp.View.MyCoinListRecyclerViewAdapter.getCoins;

public class CurrencyListActivity extends AppCompatActivity {
    Handler handler = new Handler();
    final int loadCoins = 1;

    ThreadPoolExecutor executor;

    RecyclerView recyclerView;
    MyCoinListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_list);
        findViewById(id.loadNext10Btn).setOnClickListener(v -> loadNext10());
        findViewById(id.refreshBtn).setOnClickListener(v -> refreshAndStartOver());

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        recyclerView = findViewById(id.coinRecyclerView);
        recyclerView.setAdapter(new MyCoinListRecyclerViewAdapter());
        adapter = (MyCoinListRecyclerViewAdapter) recyclerView.getAdapter();

        refreshAndStartOver();
    }

    public void refreshAndStartOver() {
        adapter.refreshCoinList();
        loadNext10();
    }

    public void loadNext10() {
        Message message = new Message();
        message.what = loadCoins;
        message.arg1 = getCoins().size(); // start
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
                            System.out.println("S" + getCoins().size());
                            runOnUiThread(() -> {
                                adapter.addCoinObj(coin);
                                addProgress(finalId - start, num);
                            });
                            System.out.println("E" + getCoins().size());
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
}