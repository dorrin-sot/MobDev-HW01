package com.mobdev.currencyapp.Model;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.CandleEntry;
import com.mobdev.currencyapp.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import static android.os.Build.VERSION_CODES.O;
import static java.lang.Math.random;
import static java.lang.Math.toIntExact;
import static java.lang.String.format;
import static java.util.Arrays.sort;

public class Coin {
    static LinkedList<Coin> coinList = new LinkedList<>();
    private final String name, symbol;
    private final int id, rank;
    private final String logoURL;
    private final double currentPriceUSD,
            percentChange1H,
            percentChange1D,
            percentChange1W;

    public Coin(String name,
                String symbol,
                int id,
                int rank,
                String logoURL,
                double currentPriceUSD,
                double percentChange1H,
                double percentChange1D,
                double percentChange1W) {
        this.name = name;
        this.symbol = symbol;
        this.id = id;
        this.rank = rank;
        this.logoURL = logoURL;
        this.currentPriceUSD = currentPriceUSD;
        this.percentChange1H = percentChange1H;
        this.percentChange1D = percentChange1D;
        this.percentChange1W = percentChange1W;
    }

    public static synchronized Coin getCoin(int id) {
        // fixme do from server now just test
        coinList.addLast(new Coin("Bitcoin", "BTC", id, coinList.size() + 1, "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png",
                50000000, 0.025, -0.255, 0.664
        ));
        return coinList.getLast();
    }

    public static synchronized void clearCoinList() {
        coinList = new LinkedList<>();
    }

    // todo only for testing
    @RequiresApi(api = O)
    @SuppressLint("DefaultLocale")
    public synchronized ArrayList<CandleEntry> generateRandomOHLCData(LocalDate start, LocalDate end) {

        ArrayList<CandleEntry> values = new ArrayList<>();

//        values.add(new CandleEntry(values.size(), 4.62f, 2.02f, 2.70f, 4.13f));
//        values.add(new CandleEntry(values.size(), 5.50f, 2.70f, 3.35f, 4.96f));
//        values.add(new CandleEntry(values.size(), 5.25f, 3.02f, 3.50f, 4.50f));
//        values.add(new CandleEntry(values.size(), 6f, 3.25f, 4.40f, 5.0f));
//        values.add(new CandleEntry(values.size(), 5.57f, 2f, 2.80f, 4.5f));
//        values.add(new CandleEntry(values.size(), 4.62f, 2.02f, 2.70f, 4.13f));

        for (int i = toIntExact(end.toEpochDay() - 1); i >= toIntExact(start.toEpochDay()); i--) {
            float multi = (100 + 1);
            float val = (float) (random() * 40) + multi;

            float high = (float) (random() * 9) + 8f;
            float low = (float) (random() * 9) + 8f;

            float open = (float) (random() * 6) + 1f;
            float close = (float) (random() * 6) + 1f;

            boolean even = i % 2 == 0;

            values.add(new CandleEntry(
                    toIntExact(start.toEpochDay()) - i,
                    val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close
            ));
        }
        return values;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public double getCurrentPriceUSD() {
        return currentPriceUSD;
    }

    public double getPercentChange1H() {
        return percentChange1H;
    }

    public double getPercentChange1D() {
        return percentChange1D;
    }

    public double getPercentChange1W() {
        return percentChange1W;
    }
}
