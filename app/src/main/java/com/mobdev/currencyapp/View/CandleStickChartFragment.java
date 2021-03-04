package com.mobdev.currencyapp.View;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.mobdev.currencyapp.R;

import java.util.ArrayList;
import java.util.LinkedList;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.Style.FILL;
import static androidx.core.content.ContextCompat.getColor;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTH_SIDED;
import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;
import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CandleStickChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CandleStickChartFragment extends Fragment {
    private static final String ohlcDataArg = "ohlcData";
    private static ArrayList<CandleEntry> ohlcData;


    public CandleStickChartFragment() {
        // Required empty public constructor
    }

    public static CandleStickChartFragment newInstance(ArrayList<CandleEntry> ohlcData) {
        CandleStickChartFragment fragment = new CandleStickChartFragment();
        Bundle args = new Bundle();
        args.putSerializable(ohlcDataArg, ohlcData);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setOhlcData(ArrayList<CandleEntry> ohlcData) {
        CandleStickChartFragment.ohlcData = ohlcData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ohlcData = (ArrayList<CandleEntry>) getArguments().getSerializable(ohlcDataArg);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candle_stick_chart, container, false);
        if (ohlcData == null) return view;

        CandleStickChart chart = view.findViewById(R.id.ohlcChart);

        chart.setMaxVisibleValueCount(32);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(WHITE);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(BOTH_SIDED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLinesBehindData(true);

        LinkedList<String> xAxisLabel = new LinkedList<>();
        for (int i = 0; i < ohlcData.size(); i++)
            xAxisLabel.addLast(ofPattern("M/d").format(now().minusDays(i).minusDays(1)));

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(true);
        yAxis.setEnabled(true);
        yAxis.setDrawGridLinesBehindData(true);

        yAxis = chart.getAxisRight();
        yAxis.setDrawAxisLine(true);
        yAxis.setEnabled(true);
        yAxis.setDrawGridLinesBehindData(true);

        chart.resetTracking();

        CandleDataSet candleDataSet = new CandleDataSet(ohlcData, "DataSet");

        candleDataSet.setDrawIcons(false);
        candleDataSet.setAxisDependency(LEFT);
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setShadowWidth(0.7f);
        candleDataSet.setDecreasingColor(getColor(getContext(), R.color.down_red));
        candleDataSet.setDecreasingPaintStyle(FILL);
        candleDataSet.setIncreasingColor(getColor(getContext(), R.color.up_green));
        candleDataSet.setIncreasingPaintStyle(FILL);
        candleDataSet.setNeutralColor(getColor(getContext(), R.color.neutral_blue));

        chart.setData(new CandleData(candleDataSet));
        chart.invalidate();

        chart.getLegend().setEnabled(false);

        return view;
    }
}