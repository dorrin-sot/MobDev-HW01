package com.mobdev.currencyapp.Model;

import android.os.Message;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.CandleEntry;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Build.VERSION_CODES.O;
import static com.mobdev.currencyapp.View.CurrencyListActivity.getHandler;
import static com.mobdev.currencyapp.View.CurrencyListActivity.getProceedProgressBar;
import static java.lang.String.valueOf;

public class Coin {
    static LinkedList<Coin> coinList = new LinkedList<>();
    private static final String apiKey = "a7f4c18e-942e-4ace-971b-10c34e92806d";
    private static final String OHLCKey = "C5D132B3-65F8-491E-B2F2-29A7397DFA17";
    private static final String OHLCtilte = "X-CoinAPI-Key";
    private static final String apiTtlte = "X-CMC_PRO_API_KEY";
    private static final String marketCapAPI = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest";
    private static final String coinIoAPI = "https://pro-api.coinmarketcap.com/v2/cryptocurrency/info";

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

    public static Coin getCoin(int id) {
        Coin coin = constructCoin(id);
        if (coin == null) return null;
        coinList.addLast(coin);
        return coin;
    }

    private static Coin getCoinn(int rank) {
        OkHttpClient coinClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest").newBuilder();
        urlBuilder.addQueryParameter("start", valueOf(rank));
        urlBuilder.addQueryParameter("limit", valueOf(1));
        Request request = new Request.Builder()
                .addHeader("X-CMC_PRO_API_KEY", "535516e0-3c6b-464b-ab71-d383cd6b85b6")
                .addHeader("Accept", "application/json")
                .url(urlBuilder.build().toString())
                .build();
        try {
            Response coinResponse = coinClient.newCall(request).execute();
            JSONObject coinData = new JSONObject(coinResponse.body().string())
                    .getJSONArray("data").getJSONObject(0);

            int id = coinData.getInt("id");

            System.out.println("coinData " + rank + " = " + coinData);
            return new Coin(
                    coinData.getString("name"),
                    coinData.getString("symbol"),
                    id,
                    rank,
                    "https://s2.coinmarketcap.com/static/img/coins/64x64/" + id + ".png",
                    coinData.getJSONObject("quote").getJSONObject("USD").getDouble("price"),
                    coinData.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_1h"),
                    coinData.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_24h"),
                    coinData.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_7d")
            );
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    private static Coin constructCoin(int id) {
        final String[] name = new String[1];
        final String[] symbol = new String[1];
        final String[] logoURL = {"https://s2.coinmarketcap.com/static/img/coins/64x64/" + id + ".png"};
        final int[] rank = new int[1];
        final double[] currentPriceUSD = new double[1];
        final double[] percentChange1H = new double[1];
        final double[] percentChange1D = new double[1];
        final double[] percentChange1W = new double[1];


        JSONObject data = GetJSON(marketCapAPI, id, apiTtlte, apiKey);
        if (data == null) return null;
        System.out.println("data=" + data);
        try {
            name[0] = data.getString("name");
            symbol[0] = data.getString("symbol");
            rank[0] = id;
            JSONObject USD_converted = data.getJSONObject("quote").getJSONObject("USD");
            currentPriceUSD[0] = USD_converted.getDouble("price");
            percentChange1H[0] = USD_converted.getDouble("percent_change_1h");
            percentChange1D[0] = USD_converted.getDouble("percent_change_24h");
            percentChange1W[0] = USD_converted.getDouble("percent_change_7d");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(
                "id:" + id +
                        "\nname:" + name[0] +
                        "\nsymbol:" + symbol[0]
                        + "\nrank:" + rank[0] + "\ncurrentprice:" + currentPriceUSD[0]
                        + "\npercentcahnge1w:" + percentChange1W[0]
                        + "\nlogourl: " + logoURL[0]);
        return new Coin(name[0], symbol[0], id, rank[0], logoURL[0], currentPriceUSD[0], percentChange1H[0], percentChange1D[0], percentChange1W[0]);
    }

    public static synchronized void clearCoinList() {
        coinList = new LinkedList<>();
    }

    // todo only for testing
    @RequiresApi(api = O)
    public synchronized ArrayList<CandleEntry> generateRandomOHLCData(int numOfDays) {
        //todo add limit to methode inputs
//        int myLimit =7;

        ArrayList<CandleEntry> values = new ArrayList<>();
        try {
            JSONArray jsonArray = getFromCoinIo(this.symbol, OHLCtilte, OHLCKey, numOfDays + 1);
            for (int i = 1; i <= jsonArray.length(); i++) {
                JSONObject candle = jsonArray.getJSONObject(i);

                // to proceed progress bar
                Message message = new Message();
                message.what = getProceedProgressBar();
                getHandler().sendMessage(message);

                float high = Float.parseFloat(candle.getString("price_high"));
                float low = Float.parseFloat(candle.getString("price_low"));
                float open = Float.parseFloat(candle.getString("price_open"));
                float close = Float.parseFloat(candle.getString("price_close"));
                //todo for dorin : check if args are correct
                values.add(new CandleEntry(i - 1, high, low, open, close));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return values;
    }

    private JSONArray getFromCoinIo(String symbol, String title, String key, int limit) {
        OkHttpClient client = new OkHttpClient();
        String rawUrl = String.format("https://rest.coinapi.io/v1/ohlcv/%s/USD/latest?period_id=1DAY", symbol);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(rawUrl).newBuilder();
        urlBuilder.addQueryParameter("limit", "" + limit + "");
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .header(title, key)
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String str = response.body().string();
            System.out.println(str);
            return new JSONArray(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject GetJSON(String rawURL, int id, String title, String key) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(rawURL).newBuilder();
        urlBuilder.addQueryParameter("id", "" + id + "");
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .header(title, key)
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String str = response.body().string();
            System.out.println(str);
            JSONObject data = new JSONObject(str);
            if (data.getJSONObject("status").getInt("error_code") == 400)
                return null;
            data = data.getJSONObject("data").getJSONObject("" + id + "");
//            int errorCode = new JSONObject(response.body().string()).getJSONObject("status").getInt("error_code");
//            if (errorCode != 0)
//                System.out.println("errorCode = " + errorCode);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
