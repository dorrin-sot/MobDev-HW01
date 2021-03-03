package com.mobdev.currencyapp.Model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

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

    public synchronized HashMap<Integer, String> getOHLCData(int numberOfDays) {
        HashMap<Integer, String> ohlcData = new HashMap<>();

        // fixme do from server now just test
        switch (numberOfDays) {
            case 7:
            case 30:
                for (int day = 1; day <= numberOfDays; day++)
                    ohlcData.put(day, generateRandomOHLCData());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + numberOfDays);
        }

        return ohlcData;
    }

    // todo only for testing
    @SuppressLint("DefaultLocale")
    private static String generateRandomOHLCData() {
        int[] numbers = new int[4];
        for (int i = 0; i < 4; i++)
            numbers[i] = new Random().nextInt(60_000);
        sort(numbers);
        int open = numbers[2],
                high = numbers[0],
                low = numbers[3],
                close = numbers[1];
        return format("%d,%d,%d,%d", open, high, low, close);
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
