package com.mobdev.currencyapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.mobdev.currencyapp.R.*;
import static java.lang.String.format;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OhlcDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OhlcDialog extends DialogFragment {
    private static final String ARG_COIN_ID = "coinID";
    private int coinID;

    public OhlcDialog() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param coinID id for the coin to show ohlc chart of
     *
     * @return A new instance of fragment OhlcDialog.
     */
    public static OhlcDialog newInstance(int coinID) {
        OhlcDialog fragment = new OhlcDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_COIN_ID, coinID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            coinID = getArguments().getInt(ARG_COIN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_ohlc_dialog, container, false);

        ((TextView) view.findViewById(id.coinIDTextView))
                .setText(format("%s: %d", ARG_COIN_ID, coinID));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}