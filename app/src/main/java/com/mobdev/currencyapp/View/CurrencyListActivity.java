package com.mobdev.currencyapp.View;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.mobdev.currencyapp.R;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.mobdev.currencyapp.R.*;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

public class CurrencyListActivity extends AppCompatActivity {
    Handler handler = new Handler();
    final int load10Coins = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_list);
        findViewById(id.loadNext10Btn).setOnClickListener(v -> loadNext10());
        findViewById(id.refreshBtn).setOnClickListener(v -> refreshAndStartOver());
    }

    public void refreshAndStartOver() {
        Message message = new Message();
        message.what = load10Coins;
        handler.sendMessage(message);
        System.out.println("CurrencyListActivity.refreshAndStartOver");
        Fragment frg = getSupportFragmentManager().findFragmentById(id.fragment);
        getSupportFragmentManager().beginTransaction()
                .detach(frg).attach(frg).commit();
    }

    public void loadNext10() {
        // TODO: 3/2/21
    }

    @Override
    protected void onResume() {
        super.onResume();
        ExecutorService executor = newFixedThreadPool(5);
        handler = new Handler(Looper.getMainLooper(), msg -> {
            switch (msg.what) {
                case load10Coins:
                    System.out.println("CurrencyListActivity.refreshAndStartOver");
                    Fragment frg = getSupportFragmentManager().findFragmentById(id.fragment);
                    getSupportFragmentManager().beginTransaction()
                            .detach(frg).attach(frg).commit();
                    break;
            }
            return true;
        });
    }
}