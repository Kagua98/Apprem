package com.application.apprem.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.application.apprem.R;
import com.application.apprem.adapters.WeekAdapter;
import com.application.apprem.models.Week;
import com.application.apprem.utils.DbHelper;
import com.application.apprem.utils.FragmentHelper;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class WeekdayFragment extends Fragment {
    public static final String KEY_MONDAY_FRAGMENT = "Monday";
    public static final String KEY_TUESDAY_FRAGMENT = "Tuesday";
    public static final String KEY_WEDNESDAY_FRAGMENT = "Wednesday";
    public static final String KEY_THURSDAY_FRAGMENT = "Thursday";
    public static final String KEY_FRIDAY_FRAGMENT = "Friday";
    public static final String KEY_SATURDAY_FRAGMENT = "Saturday";
    public static final String KEY_SUNDAY_FRAGMENT = "Sunday";

    @Nullable
    private DbHelper db;
    private ListView listView;
    @Nullable
    private WeekAdapter adapter;
    private View view;

    private final String key;

    public WeekdayFragment(String key) {
        super();
        this.key = key;
    }

    public WeekdayFragment() {
        super();
        this.key = KEY_MONDAY_FRAGMENT;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weekday, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupAdapter(view);
        setupListViewMultiSelect();
    }

    private void setupAdapter(@NonNull View view) {
        db = new DbHelper(getActivity());
        listView = view.findViewById(R.id.daylist);
        ArrayList<Week> weeks = db.getWeek(key);
        adapter = new WeekAdapter((AppCompatActivity) requireActivity(), listView, 0, weeks);
        listView.setAdapter(adapter);
    }

    private void setupListViewMultiSelect() {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(FragmentHelper.setupListViewMultiSelect((AppCompatActivity) getActivity(), listView, adapter, db));
    }

    public String getKey() {
        return key;
    }
}
