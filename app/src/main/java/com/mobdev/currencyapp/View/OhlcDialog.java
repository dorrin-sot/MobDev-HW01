package com.mobdev.currencyapp.View;

import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import com.mobdev.currencyapp.Model.Coin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.Style.FILL;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.content.ContextCompat.getColor;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTH_SIDED;
import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;
import static com.mobdev.currencyapp.Model.Coin.getCoin;
import static com.mobdev.currencyapp.R.*;
import static com.mobdev.currencyapp.View.CurrencyListActivity.getHandler;
import static com.mobdev.currencyapp.View.CurrencyListActivity.openOhlcPage;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.time.LocalDate.now;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OhlcDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OhlcDialog extends DialogFragment implements TabLayout.OnTabSelectedListener {
    private static ArrayList<CandleEntry> ohlcData;
    private static final String ohlcDataArg = "ohlcData",
            coinIDArg = "coinID";
    private static int coinID;

    private TabLayout tabLayout;

    public OhlcDialog() {
    }

    /**
     * @return A new instance of fragment OhlcDialog.
     */
    public static OhlcDialog newInstance(int coinID, ArrayList<CandleEntry> ohlcData) {
        OhlcDialog fragment = new OhlcDialog();
        Bundle args = new Bundle();
        args.putSerializable(ohlcDataArg, ohlcData);
        args.putInt(coinIDArg, coinID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ohlcData = (ArrayList<CandleEntry>) getArguments().getSerializable(ohlcDataArg);
            coinID = getArguments().getInt(coinIDArg);
        }
    }

    @RequiresApi(api = O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_ohlc_dialog, container, false);

        tabLayout = view.findViewById(id.tabLayout);

        tabLayout.addOnTabSelectedListener(this);

        CandleStickChart chart = view.findViewById(id.ohlcChart);
        chart.setMaxVisibleValueCount(32);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(WHITE);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(BOTH_SIDED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLinesBehindData(true);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(true);
//        yAxis.setAxisMaximum(60_000);
//        yAxis.setAxisMinimum(0);
        yAxis.setEnabled(true);
        yAxis.setDrawGridLinesBehindData(true);

        yAxis = chart.getAxisRight();
        yAxis.setDrawAxisLine(true);
//        yAxis.setAxisMaximum(60_000);
//        yAxis.setAxisMinimum(0);
        yAxis.setEnabled(true);
        yAxis.setDrawGridLinesBehindData(true);

        chart.resetTracking();

        tabLayout.getTabAt(ohlcData.size() == 7 ? 0 : 1)
                .select();

        CandleDataSet candleDataSet = new CandleDataSet(ohlcData, "DataSet");

        candleDataSet.setDrawIcons(false);
        candleDataSet.setAxisDependency(LEFT);
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setShadowWidth(0.7f);
        candleDataSet.setDecreasingColor(getColor(getContext(), color.down_red));
        candleDataSet.setDecreasingPaintStyle(FILL);
        candleDataSet.setIncreasingColor(getColor(getContext(), color.up_green));
        candleDataSet.setIncreasingPaintStyle(FILL);
        candleDataSet.setNeutralColor(getColor(getContext(), color.neutral_blue));

        chart.setData(new CandleData(candleDataSet));
        chart.invalidate();

        chart.getLegend().setEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static int getCoinID() {
        return coinID;
    }

    @Override
    public void onTabSelected(Tab tab) {
        Message message = new Message();
        message.what = openOhlcPage;
        message.arg1 = (tab.getId() == id.show1WTab ? 7 : 30);
        message.obj = getCoin(getCoinID());
        getHandler().sendMessage(message);
    }

    @Override
    public void onTabUnselected(Tab tab) {
    }

    @Override
    public void onTabReselected(Tab tab) {
    }
}