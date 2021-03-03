package com.mobdev.currencyapp.View;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.Style.FILL;
import static androidx.core.content.ContextCompat.getColor;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTH_SIDED;
import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;
import static com.mobdev.currencyapp.R.*;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.time.LocalDate.now;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OhlcDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OhlcDialog extends DialogFragment {
    private static HashMap<LocalDate, String> ohlcData;
    private static final String ohlcDataArg = "ohlcData",
            coinIDArg = "coinID";
    private static int coinID;

    public OhlcDialog() {
    }

    /**
     * @param ohlcData
     * @return A new instance of fragment OhlcDialog.
     */
    public static OhlcDialog newInstance(int coinID, HashMap<LocalDate, String> ohlcData) {
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
            ohlcData = (HashMap<LocalDate, String>) getArguments().getSerializable(ohlcDataArg);
            coinID = getArguments().getInt(coinIDArg);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_ohlc_dialog, container, false);

        CandleStickChart chart = view.findViewById(id.ohlcChart);
        chart.setMaxVisibleValueCount(30);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(WHITE);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(BOTH_SIDED);
        xAxis.setDrawAxisLine(true);
//        xAxis.setDrawGridLinesBehindData(true);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(true);
        yAxis.setAxisMaximum(60_000);
        yAxis.setAxisMinimum(0);
        yAxis.setEnabled(true);
//        yAxis.setDrawGridLinesBehindData(true);

        yAxis = chart.getAxisRight();
        yAxis.setDrawAxisLine(true);
        yAxis.setAxisMaximum(60_000);
        yAxis.setAxisMinimum(0);
        yAxis.setEnabled(true);
//        yAxis.setDrawGridLinesBehindData(true);

        CandleDataSet candleDataSet = getCandleDataSet(ohlcData);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static CandleDataSet getCandleDataSet(HashMap<LocalDate, String> ohlcData) {
        return new CandleDataSet(new LinkedList<CandleEntry>() {{
            for (Map.Entry<LocalDate, String> pieceOfData : ohlcData.entrySet()) {
                int open = parseInt(pieceOfData.getValue().split(",")[0]),
                        high = parseInt(pieceOfData.getValue().split(",")[1]),
                        low = parseInt(pieceOfData.getValue().split(",")[2]),
                        close = parseInt(pieceOfData.getValue().split(",")[3]);

                add(new CandleEntry(
                        now().toEpochDay() - pieceOfData.getKey().toEpochDay(), high, low, open, close
                ));
            }
        }}, "Data set");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static int getCoinID() {
        return coinID;
    }
}