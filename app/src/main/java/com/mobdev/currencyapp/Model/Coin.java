package com.mobdev.currencyapp.Model;

import java.util.LinkedList;
import java.util.List;

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

    public static LinkedList<Coin> getCoinList() {
        System.out.println("Coin.getCoinList");
        // fixme do from server now just test
        for (int i = 0; i < 5; i++)
            coinList.add(new Coin("Bitcoin", "BTC", 1, coinList.size()+1, "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png",
                    50000000, 0.025, -0.255, 0.664
            ));

        return coinList;
    }

    public static synchronized Coin getCoin(int id) {
        coinList.addLast(new Coin("Bitcoin", "BTC", id, coinList.size()+1, "https://s2.coinmarketcap.com/static/img/coins/64x64/1.png",
                50000000, 0.025, -0.255, 0.664
        ));
        return coinList.getLast();
    }

    public static synchronized void clearCoinList() {
        coinList = new LinkedList<>();
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
