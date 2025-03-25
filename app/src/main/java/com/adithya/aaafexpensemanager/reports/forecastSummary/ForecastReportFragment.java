package com.adithya.aaafexpensemanager.reports.forecastSummary;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reports.forecastSummary.ForecastConstants.ForecastTimePeriod;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForecastReportFragment extends Fragment {
    // TODO: Make sure that the future transactions are present till max date - ForecastReportFragment
    private LookupEditText timePeriodSelection;
    private Button filterButton;
    private ForecastTimePeriod selectedTimePeriod;
    private RecyclerView reportsRecyclerView;
    private final TransactionFilter transactionFilter = new TransactionFilter();
    private List<LookupEditText.LookupEditTextItem> timePeriods = new ArrayList<>();
    private Application application;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.application = requireActivity().getApplication();
        View view =  inflater.inflate(R.layout.fragment_report_forecast_summary, container, false);
        assignLayoutComponents(view);
        setupTimePeriodSelection();
        setupFilterButton();
        loadReportData();
        return view;
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> new ForecastReportFilterDialog(requireContext(),
                requireActivity(),
                transactionFilter,
                filter-> loadReportData())
                .showDialog());
    }

    private void loadReportData() {
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionFilter.setFromTransactionDate(selectedTimePeriod.getStartDate());
        transactionFilter.setToTransactionDate(selectedTimePeriod.getEndDate());
        List<ForecastReportRecord> forecastReportRecords;
        ForecastReportRepository repository = new ForecastReportRepository(application);
        forecastReportRecords = repository.getForecastReportData(transactionFilter);
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
        filterButton = view.findViewById(R.id.filterButton);
        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
    }
}