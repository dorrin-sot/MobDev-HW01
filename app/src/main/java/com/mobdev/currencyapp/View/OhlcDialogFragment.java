package com.mobdev.currencyapp.View;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.CandleEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.O;
import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;
import static com.mobdev.currencyapp.R.id;
import static com.mobdev.currencyapp.R.layout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OhlcDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OhlcDialogFragment extends DialogFragment implements TabLayout.OnTabSelectedListener {
    private static ArrayList<CandleEntry> ohlcData1Week, ohlcData1Month;
    private static boolean startFrom1WeekOrMonth;
    private static String coinName;
    private static final String ohlcData1WeekArg = "ohlcData1Week", ohlcData1MonthArg = "ohlcData1Month",
            startFrom1WeekOrMonthArg = "startFrom1WeekOrMonth", coinNameArg = "coinName";

    private TabLayout tabLayout;

    public OhlcDialogFragment() {
    }

    /**
     * @return A new instance of fragment OhlcDialogFragment.
     */
    public static OhlcDialogFragment newInstance(ArrayList<CandleEntry> ohlcData1Week,
                                                 ArrayList<CandleEntry> ohlcData1Month,
                                                 boolean startFrom1WeekOrMonth,
                                                 String coinName) {
        OhlcDialogFragment fragment = new OhlcDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ohlcData1WeekArg, ohlcData1Week);
        args.putSerializable(ohlcData1MonthArg, ohlcData1Month);
        args.putBoolean(startFrom1WeekOrMonthArg, startFrom1WeekOrMonth);
        args.putString(coinNameArg, coinName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ohlcData1Week = (ArrayList<CandleEntry>) getArguments().getSerializable(ohlcData1WeekArg);
            ohlcData1Month = (ArrayList<CandleEntry>) getArguments().getSerializable(ohlcData1MonthArg);
            startFrom1WeekOrMonth = getArguments().getBoolean(startFrom1WeekOrMonthArg);
            coinName = getArguments().getString(coinNameArg);
        }
    }

    @RequiresApi(api = O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_ohlc_dialog, container, false);

        tabLayout = view.findViewById(id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);
        onTabSelected(tabLayout.getTabAt(startFrom1WeekOrMonth ? 0 : 1));

        return view;
    }

    @Override
    public void onTabSelected(Tab tab) {
        boolean show1WeekOrMonth = tab.getText().toString().toLowerCase().contains("week");

        getChildFragmentManager().beginTransaction()
                .replace(id.chartFragment, CandleStickChartFragment.newInstance(
                        show1WeekOrMonth
                                ?
                                ohlcData1Week : ohlcData1Month
                ))
                .addToBackStack(null)
                .setTransition(TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onTabUnselected(Tab tab) {
    }

    @Override
    public void onTabReselected(Tab tab) {
    }
}