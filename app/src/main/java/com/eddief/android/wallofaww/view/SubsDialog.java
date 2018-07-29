package com.eddief.android.wallofaww.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.eddief.android.wallofaww.R;

import java.util.ArrayList;
import java.util.List;

import static com.eddief.android.wallofaww.view.MainActivity.subReddits;


public class SubsDialog extends DialogFragment {

    private static final String ARG_CHECKED = "checked";
    private static final String ARG_UNCHECKED = "unchecked";
    private List<Integer> mChecked;
    private List<Integer> mUnChecked;

    private OnFragmentInteractionListener mListener;

    public SubsDialog() {
        // Required empty public constructor
    }

    public static SubsDialog newInstance(ArrayList<Integer> checked, ArrayList<Integer> unchecked) {
        SubsDialog fragment = new SubsDialog();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARG_CHECKED, checked);
        args.putIntegerArrayList(ARG_UNCHECKED, unchecked);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChecked = getArguments().getIntegerArrayList(ARG_CHECKED);
            mUnChecked = getArguments().getIntegerArrayList(ARG_UNCHECKED);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_subs, null);
        final Context context = view.getContext();

        for (int id : mChecked) {
            CheckBox checkBox = view.findViewById(id);
            checkBox.setChecked(true);
        }
        for (int subreddit : subReddits) {
            setCheckBox(view, subreddit);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Choose SubReddits")
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(mChecked.isEmpty()) {
                            Toast.makeText(context, "Choose at least one subreddit", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        onButtonPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        alertDialog.show();
        return alertDialog;
    }

    private void setCheckBox(View view, final int id) {
        CheckBox checkBox = view.findViewById(id);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mChecked.add(id);
                    mUnChecked.remove((Integer) id);
                } else {
                    mUnChecked.add(id);
                    mChecked.remove((Integer) id);
                }
            }
        });
    }

    private void onButtonPressed() {
        if (mListener != null) {
            mListener.onPreferencesSaved(mChecked, mUnChecked);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onPreferencesSaved(List<Integer> checked, List<Integer> unchecked);
    }
}
