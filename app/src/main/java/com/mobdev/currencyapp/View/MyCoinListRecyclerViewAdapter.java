package com.mobdev.currencyapp.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;

import com.bumptech.glide.Glide;
import com.mobdev.currencyapp.Model.Coin;

import java.text.DecimalFormat;
import java.util.HashMap;

import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;
import static android.view.LayoutInflater.from;
import static android.view.View.GONE;
import static androidx.core.content.ContextCompat.getColor;
import static com.mobdev.currencyapp.Model.Coin.clearCoinList;
import static com.mobdev.currencyapp.R.color;
import static com.mobdev.currencyapp.R.drawable;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;
import static com.mobdev.currencyapp.R.string;
import static com.mobdev.currencyapp.View.CurrencyListActivity.getHandler;
import static com.mobdev.currencyapp.View.CurrencyListActivity.openOhlcPage;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Coin}.
 */
public class MyCoinListRecyclerViewAdapter extends RecyclerView.Adapter<MyCoinListRecyclerViewAdapter.ViewHolder> {

    private static HashMap<Integer, Coin> coins = new HashMap<>();
    private Context context;

    public MyCoinListRecyclerViewAdapter() {
    }

    public synchronized void refreshCoinList() {
        clearCoinList();
        int size = coins.size();
        coins = new HashMap<>();
        for (int i = 0; i < size; i++) {
            notifyItemRemoved(0);
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
        if (coins.isEmpty()) return;
        position++;
        Coin coin = coins.get(position);
        if (coin == null) {
            holder.itemView.setVisibility(GONE);
            holder.itemView.setLayoutParams(new LayoutParams(0, 0));
            return;
        }
        holder.coin = coin;
//        holder.coinRank.setText(valueOf(coin.getRank()));
        setCoinIcon(coin, holder.coinIcon);
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

    private void setCoinIcon(Coin coin, ImageView coinIcon) {
        Glide.with(context)
                .load(coin.getLogoURL()) // Image URL
                .centerCrop() // Image scale type
                .override(100) // Resize image
                .placeholder(drawable.coin_icon) // Place holder image
                .into(coinIcon); // ImageView to display image
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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView
                coinFullName, coinShortName,
                coinPrice,
                _1HChange, _1DChange, _1WChange;
        public final ImageView coinIcon;
        public Coin coin;

        public ViewHolder(View view) {
            super(view);
            mView = view;
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
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Message msg = new Message();
            msg.what = openOhlcPage;
            msg.obj = coin;
            getHandler().sendMessage(msg);
        }
    }

    @RequiresApi(api = N)
    public synchronized void addCoinObj(int position, Coin coin) {
        coins.remove(0);
        coins.put(position, coin);
        notifyItemChanged(position - 1);
    }

    public static synchronized HashMap<Integer, Coin> getCoins() {
        return coins;
    }
}