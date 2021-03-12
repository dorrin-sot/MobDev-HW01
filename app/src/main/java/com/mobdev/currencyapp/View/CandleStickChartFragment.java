package com.mobdev.currencyapp.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.mobdev.currencyapp.R;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

import static android.graphics.Color.WHITE;
import static android.graphics.Paint.Style.FILL;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.content.ContextCompat.getColor;
import static com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM;
import static com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT;
import static org.threeten.bp.LocalDate.now;
import static org.threeten.bp.format.DateTimeFormatter.ofPattern;

//import java.time.LocalDate;


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ohlcData = (ArrayList<CandleEntry>) getArguments().getSerializable(ohlcDataArg);
        }
    }

    @RequiresApi(api = O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candle_stick_chart, container, false);
        if (ohlcData == null) return view;

        CandleStickChart chart = view.findViewById(R.id.ohlcChart);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (0.8 * displayMetrics.widthPixels);
        chart.setMinimumWidth(width);
        chart.setMinimumHeight(width);

        chart.setMaxVisibleValueCount(32);
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(WHITE);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLinesBehindData(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(width / 125f);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                LocalDate date = now().minusDays((long) value).minusDays(1);
                String pattern;
                if (date.getDayOfMonth()==1)
                    pattern = "MMM";
                else
                    pattern = "d";
                return ofPattern(pattern).format(date);
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(true);
        yAxis.setEnabled(true);
        yAxis.setDrawGridLinesBehindData(true);
        yAxis.setTextSize(width / 125f);

        yAxis = chart.getAxisRight();
        yAxis.setDrawAxisLine(true);
        yAxis.setEnabled(true);
        yAxis.setDrawGridLinesBehindData(true);
        yAxis.setTextSize(width / 125f);

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
        candleDataSet.setBarSpace(0.3f);
        candleDataSet.setValueTextSize(width / 200f);

        chart.setData(new CandleData(candleDataSet));
        chart.invalidate();

        chart.getLegend().setEnabled(false);

        return view;
    }
}