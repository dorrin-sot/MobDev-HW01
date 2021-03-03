package com.mobdev.currencyapp.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdev.currencyapp.Model.Coin;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;
import static android.view.LayoutInflater.from;
import static androidx.core.content.ContextCompat.getColor;
import static com.mobdev.currencyapp.R.*;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Coin}.
 */
public class MyCoinListRecyclerViewAdapter extends RecyclerView.Adapter<MyCoinListRecyclerViewAdapter.ViewHolder> {

    private static final LinkedList<Coin> coins = new LinkedList<>();
    static Thread coinListObserver;
    private Context context;

    public MyCoinListRecyclerViewAdapter() {
        coinListObserver = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("MyCoinListRecyclerViewAdapter.run.before wait");
                        synchronized (this) {
                            wait();
                            notifyItemInserted(coins.size() - 1);
                        }
                        System.out.println("MyCoinListRecyclerViewAdapter.run.after wait");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        coinListObserver.start();
    }

    public static void refreshCoinList() {
        synchronized (coinListObserver) {
            coins.clear();
            coinListObserver.notify();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = from(parent.getContext())
                .inflate(layout.coin, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @RequiresApi(api = M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.coin = coins.get(position);
        Coin coin = coins.get(position);
        holder.coinRank.setText(valueOf(coin.getRank()));
//        holder.coinIcon.setImageIcon(createWithContentUri(valueOf(new File(coin.getLogoURL()).toURI()))); // fixme
//        Glide.with(holder.mView)
//                .load(coin.getLogoURL())
//                .into(holder.coinIcon);
        holder.coinFullName.setText(coin.getName());
        holder.coinShortName.setText(coin.getSymbol());
        holder.coinPrice.setText(formatChange(coin.getCurrentPriceUSD(), 4));
        holder._1HChange.setText(formatChange(coin.getPercentChange1H(), 1));
        holder._1HChange.setTextColor(getColorBaseOnUpDown(coin.getPercentChange1H()));
        holder._1DChange.setText(formatChange(coin.getPercentChange1D(), 2));
        holder._1DChange.setTextColor(getColorBaseOnUpDown(coin.getPercentChange1D()));
        holder._1WChange.setText(formatChange(coin.getPercentChange1W(), 3));
        holder._1WChange.setTextColor(getColorBaseOnUpDown(coin.getPercentChange1W()));
    }

    /**
     * @param type 1:1H , 2:1D , 3:1W , 4:price
     * @return formatted like "1D: (up/down icon)number%"
     */
    @SuppressLint("DefaultLocale")
    private String formatChange(double val, int type) {
        if (type == 4)
            return format("$%s", new DecimalFormat("#,###.00").format(val));

        boolean upOrDown = val > 0;
        return format(" %s%.02f", upOrDown ? "▲️" : "▼️", Float.valueOf(valueOf(abs(val)))) + "%";
    }

    private int getColorBaseOnUpDown(double val) {
        boolean upOrDown = val > 0;
        return getColor(context, upOrDown ? color.up_green : color.down_red);
    }

    @Override
    public int getItemCount() {
        return coins.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView coinRank,
                coinFullName, coinShortName,
                coinPrice,
                _1HChange, _1DChange, _1WChange;
        public final ImageView coinIcon;
        public Coin coin;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            coinRank = view.findViewById(id.coinRank);
            coinFullName = view.findViewById(id.coinFullName);
            coinShortName = view.findViewById(id.coinShortName);
            coinIcon = view.findViewById(id.coinIcon);
            coinPrice = view.findViewById(id.coinPrice);
            _1HChange = view.findViewById(id._1HChange);
            _1DChange = view.findViewById(id._1DChange);
            _1WChange = view.findViewById(id._1WChange);
            ((TextView) view.findViewById(id._1HTitle)).setText(context.getString(string._1HTitleStr));
            ((TextView) view.findViewById(id._1DTitle)).setText(context.getString(string._1DTitleStr));
            ((TextView) view.findViewById(id._1WTitle)).setText(context.getString(string._1WTitleStr));
        }
    }

    public static void addCoinObj(Coin coin) {
        synchronized (coinListObserver) {
            System.out.println("MyCoinListRecyclerViewAdapter.addCoinObj");
            coins.addLast(coin);
            coinListObserver.notifyAll();
        }
    }

    public static LinkedList<Coin> getCoins() {
        return coins;
    }
}