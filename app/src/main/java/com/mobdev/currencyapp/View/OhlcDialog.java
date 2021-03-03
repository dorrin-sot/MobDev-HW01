package com.mobdev.currencyapp.View;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.google.android.material.tabs.TabItem;

import java.util.ArrayList;
import java.util.HashMap;

import static android.graphics.Color.WHITE;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTH_SIDED;
import static com.mobdev.currencyapp.R.*;
import static com.mobdev.currencyapp.View.CurrencyListActivity.getHandler;
import static com.mobdev.currencyapp.View.CurrencyListActivity.openOhlcPage;
import static java.lang.String.format;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OhlcDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OhlcDialog extends DialogFragment {
    private static HashMap<Integer, String> ohlcData;
    private static final String ohlcDataArg = "ohlcData",
            coinIDArg = "coinID";
    private static int coinID;

    public OhlcDialog() {
    }

    /**
     * @param ohlcData
     * @return A new instance of fragment OhlcDialog.
     */
    public static OhlcDialog newInstance(int coinID, HashMap<Integer, String> ohlcData) {
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
            ohlcData = (HashMap<Integer, String>) getArguments().getSerializable(ohlcDataArg);
            coinID = getArguments().getInt(coinIDArg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_ohlc_dialog, container, false);

        CandleStickChart chart = view.findViewById(id.ohlcChart);
        chart.setBackgroundColor(WHITE);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(BOTH_SIDED);
        xAxis.setDrawAxisLine(true);
//        xAxis.setDrawGridLinesBehindData(true);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(true);
        yAxis.setEnabled(true);
//        yAxis.setDrawGridLinesBehindData(true);

        yAxis = chart.getAxisRight();
        yAxis.setDrawAxisLine(true);
        yAxis.setEnabled(true);
//        yAxis.setDrawGridLinesBehindData(true);



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
}