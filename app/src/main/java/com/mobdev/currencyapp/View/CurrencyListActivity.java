package com.mobdev.currencyapp.View;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdev.currencyapp.Model.Coin;
import com.mobdev.currencyapp.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static android.os.Build.VERSION_CODES.N;
import static android.util.Log.i;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.mobdev.currencyapp.Model.Coin.getCoin;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;
import static com.mobdev.currencyapp.View.CoinListFragment.*;
import static com.mobdev.currencyapp.View.MyCoinListRecyclerViewAdapter.addCoinObj;
import static com.mobdev.currencyapp.View.MyCoinListRecyclerViewAdapter.getCoins;
import static com.mobdev.currencyapp.View.MyCoinListRecyclerViewAdapter.refreshCoinList;
import static java.lang.Thread.sleep;

public class CurrencyListActivity extends AppCompatActivity {
    private static Fragment coinListFragment;
    Handler handler = new Handler();
    final int loadCoins = 1;

    ThreadPoolExecutor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_list);
        findViewById(id.loadNext10Btn).setOnClickListener(v -> loadNext10());
        findViewById(id.refreshBtn).setOnClickListener(v -> refreshAndStartOver());

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        coinListFragment = getSupportFragmentManager().findFragmentById(id.fragment);
        System.out.println(coinListFragment.getActivity().findViewById(id.coinRecyclerView));
        loadNext10();
    }

    public void refreshAndStartOver() {
        refreshCoinList();
        loadNext10();
    }

    public void loadNext10() {
        Message message = new Message();
        message.what = loadCoins;
        message.arg1 = getCoins().size()+1; // start
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
                            addCoinObj(coin);
                            System.out.println("S"+getCoins().size());
                            runOnUiThread(() -> addProgress(finalId - start, num));
                            System.out.println("E"+ getCoins().size());
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