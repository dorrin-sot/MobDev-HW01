package com.mobdev.currencyapp.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.data.CandleEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import com.mobdev.currencyapp.R;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.O;
import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE;
import static com.mobdev.currencyapp.R.*;
import static com.mobdev.currencyapp.R.layout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OhlcDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OhlcDialog extends DialogFragment implements TabLayout.OnTabSelectedListener {
    private static ArrayList<CandleEntry> ohlcData1Week, ohlcData1Month;
    private static final String ohlcData1WeekArg = "ohlcData1Week", ohlcData1MonthArg = "ohlcData1Month";

    private TabLayout tabLayout;

    public OhlcDialog() {
    }

    /**
     * @return A new instance of fragment OhlcDialog.
     */
    public static OhlcDialog newInstance(ArrayList<CandleEntry> ohlcData1Week,
                                         ArrayList<CandleEntry> ohlcData1Month) {
        OhlcDialog fragment = new OhlcDialog();
        Bundle args = new Bundle();
        args.putSerializable(ohlcData1WeekArg, ohlcData1Week);
        args.putSerializable(ohlcData1MonthArg, ohlcData1Month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ohlcData1Week = (ArrayList<CandleEntry>) getArguments().getSerializable(ohlcData1WeekArg);
            ohlcData1Month = (ArrayList<CandleEntry>) getArguments().getSerializable(ohlcData1MonthArg);
        }
    }

    @RequiresApi(api = O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(layout.fragment_ohlc_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);
        tabLayout.getTabAt(0).select();
    }

    @Override
    public void onTabSelected(Tab tab) {
//        getChildFragmentManager().beginTransaction()
//                .replace(id.chartFragment, CandleStickChartFragment.newInstance(
//                        tab.getId() == id.show1WTab ? ohlcData1Week : ohlcData1Month)
//                )
//                .addToBackStack(null)
//                .setTransition(TRANSIT_FRAGMENT_FADE)
//                .commit();
        getChildFragmentManager().beginTransaction()
                .replace(id.chartFragment, CandleStickChartFragment.newInstance(
                        tab.getId() == id.show1WTab ? ohlcData1Week : ohlcData1Month)
                )
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