package com.adithya.aaafexpensemanager.reports.forecastSummary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reports.forecastSummary.ForecastConstants.ForecastTimePeriod;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForecastReportFragment extends Fragment {
    // TODO: Implement forecast report fragment - ForecastReportFragment
    // TODO: Create a list view adapter for the forecast report - ForecastReportFragment
    // TODO: Test the listview adapter - ForecastReportFragment
    // TODO: Add a ENum for the category selection - ForecastReportFragment
    // TODO: Make sure that the future transactions are present till max date - ForecastReportFragment
    private LookupEditText timePeriodSelection;
    private ForecastTimePeriod selectedTimePeriod;
    private RecyclerView reportsRecyclerView;
    private List<LookupEditText.LookupEditTextItem> timePeriods = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_report_forecast_summary, container, false);
        assignLayoutComponents(view);
        setupTimePeriodSelection();
        loadReportData();
        return view;
    }

    private void loadReportData() {
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ForecastReportRecord> forecastReportRecords = new ArrayList<>();
        ForecastReportAdapter forecastReportAdapter = new ForecastReportAdapter(forecastReportRecords);
        reportsRecyclerView.setAdapter(forecastReportAdapter);
    }

    private void setDefaultTimePeriodSelection(){
        timePeriodSelection.setText(timePeriods.get(0).toString());
        selectedTimePeriod = (ForecastTimePeriod) timePeriods.get(0);
    }
    private void setupTimePeriodSelection() {
        timePeriods = Arrays
                .stream(ForecastConstants.ForecastTimePeriod.values())
                .collect(Collectors.toList());
        setDefaultTimePeriodSelection();
        timePeriodSelection.setItems(timePeriods);
        timePeriodSelection.setOnItemClickListener((selectedItem, position) -> {
            selectedTimePeriod = (ForecastTimePeriod) selectedItem;
            loadReportData();
        });
    }

    private void assignLayoutComponents(View view) {
        timePeriodSelection = view.findViewById(R.id.timePeriodSelection);
        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
    }
}