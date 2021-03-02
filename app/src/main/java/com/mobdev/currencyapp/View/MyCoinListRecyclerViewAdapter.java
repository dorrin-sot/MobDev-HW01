package com.mobdev.currencyapp.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobdev.currencyapp.Model.Coin;
import com.mobdev.currencyapp.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import static android.graphics.drawable.Icon.createWithContentUri;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.lang.String.valueOf;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Coin}.
 */
public class MyCoinListRecyclerViewAdapter extends RecyclerView.Adapter<MyCoinListRecyclerViewAdapter.ViewHolder> {

    private final List<Coin> mValues;

    public MyCoinListRecyclerViewAdapter(List<Coin> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coin, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Coin coin = mValues.get(position);
        holder.coinRank.setText(valueOf(coin.getRank()));
//        holder.coinIcon.setImageIcon(createWithContentUri(valueOf(new File(coin.getLogoURL()).toURI()))); // fixme
//        Glide.with(holder.mView)
//                .load(coin.getLogoURL())
//                .into(holder.coinIcon);
        holder.coinFullName.setText(coin.getName());
        holder.coinShortName.setText(coin.getSymbol());
        holder.coinPrice.setText(formatChange(coin.getCurrentPriceUSD(), 4));
        holder._1HChange.setText(formatChange(coin.getPercentChange1H(), 1));
        holder._1DChange.setText(formatChange(coin.getPercentChange1D(), 2));
        holder._1WChange.setText(formatChange(coin.getPercentChange1W(), 3));
    }

    /**
     * @param type 1:1H , 2:1D , 3:1W , 4:price
     * @return formatted like "1D: (up/down icon)number%"
     */
    @SuppressLint("DefaultLocale")
    private String formatChange(double val, int type) {
        String title;
        switch (type) {
            case 1:
                title = "1H";
                break;
            case 2:
                title = "1D";
                break;
            case 3:
                title = "1W";
                break;
            case 4:
            {
                return format("$%s", new DecimalFormat("#,###.00").format(val));
            }
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        boolean upOrDown = val > 0;
        return format("%s: %s%.02f", title, upOrDown ? "⬆️" : "⬇️", Float.valueOf(valueOf(abs(val)))) + "%";
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView coinRank,
                coinFullName, coinShortName,
                coinPrice,
                _1HChange, _1DChange, _1WChange;
        public final ImageView coinIcon;
        public Coin mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            coinRank = (TextView) view.findViewById(R.id.coinRank);
            coinFullName = (TextView) view.findViewById(R.id.coinFullName);
            coinShortName = view.findViewById(R.id.coinShortName);
            coinIcon = view.findViewById(R.id.coinIcon);
            coinPrice = view.findViewById(R.id.coinPrice);
            _1HChange = view.findViewById(R.id._1HChange);
            _1DChange = view.findViewById(R.id._1DChange);
            _1WChange = view.findViewById(R.id._1WChange);
        }
    }
}