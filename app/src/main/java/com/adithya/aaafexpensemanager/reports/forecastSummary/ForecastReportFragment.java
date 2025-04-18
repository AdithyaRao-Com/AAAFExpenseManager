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
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterDialog;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ForecastReportFragment extends Fragment {
    private final List<LookupEditText.LookupEditTextItem> timePeriods = Arrays
            .stream(ForecastConstants.ForecastTimePeriod.values())
            .collect(Collectors.toList());
    private TransactionFilter transactionFilter = new TransactionFilter();
    private LookupEditText timePeriodSelection;
    private Button filterButton;
    private ForecastTimePeriod selectedTimePeriod;
    private RecyclerView reportsRecyclerView;
    private Application application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.application = requireActivity().getApplication();
        View view = inflater.inflate(R.layout.fragment_report_forecast_summary, container, false);
        assignLayoutComponents(view);
        parseArgs();
        setupTimePeriodSelection();
        setupFilterButton();
        loadReportData();
        return view;
    }

    private void parseArgs() {
        Bundle args = getArguments();
        if (args != null) {
            //noinspection deprecation
            transactionFilter = args.getParcelable("transactionFilter");
            assert transactionFilter != null;
            if (transactionFilter.periodName == null || transactionFilter.periodName.isBlank()) {
                transactionFilter.periodName = "Custom";
            }
            String selectedTimePeriodString = transactionFilter.periodName;
            selectedTimePeriod = getSelectedPeriodEnum(selectedTimePeriodString);
        } else {
            setDefaultTimePeriodSelection();
            transactionFilter = new TransactionFilter();
        }
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> new TransactionFilterDialog(requireContext(),
                requireActivity(),
                transactionFilter,
                filter -> loadReportData(),
                false)
                .showDialog());
    }

    private void loadReportData() {
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (selectedTimePeriod != ForecastTimePeriod.CUSTOM) {
            transactionFilter.setFromTransactionDate(selectedTimePeriod.getStartDate());
            transactionFilter.setToTransactionDate(selectedTimePeriod.getEndDate());
        }
        List<ForecastReportRecord> forecastReportRecords;
        ForecastReportRepository repository = new ForecastReportRepository(application);
        forecastReportRecords = repository.getForecastReportData(transactionFilter);
        forecastReportRecords = fillGapData(forecastReportRecords, transactionFilter);
        ForecastReportAdapter forecastReportAdapter = new ForecastReportAdapter(forecastReportRecords);
        reportsRecyclerView.setAdapter(forecastReportAdapter);
    }

    private List<ForecastReportRecord> fillGapData(List<ForecastReportRecord> forecastReportRecords, TransactionFilter transactionFilterTemp) {
        NavigableMap<LocalDate, ForecastReportRecord> forecastReportRecordHashMap = new TreeMap<>();
        LocalDate startDateLocal = transactionFilterTemp.getFromTransactionDateLocalDate();
        LocalDate endDateLocal = transactionFilterTemp.getToTransactionDateLocalDate().plusDays(1);
        forecastReportRecords
                .forEach(forecastReportRecord -> forecastReportRecordHashMap.put(forecastReportRecord.transactionDate, forecastReportRecord));
        //Populate first value
        LocalDate firstValueKey = transactionFilterTemp.getFromTransactionDateLocalDate();
        if (!forecastReportRecordHashMap.containsKey(firstValueKey)) {
            LocalDate nextKey = forecastReportRecordHashMap.higherKey(firstValueKey);
            ForecastReportRecord nextValue = forecastReportRecordHashMap.get(nextKey);
            assert nextValue != null;
            forecastReportRecordHashMap.put(firstValueKey, new ForecastReportRecord(firstValueKey, nextValue.amount, nextValue.currency));
        }
        startDateLocal.datesUntil(endDateLocal).forEach(date -> {
            if (!forecastReportRecordHashMap.containsKey(date)) {
                LocalDate previousKey = forecastReportRecordHashMap.lowerKey(date);
                ForecastReportRecord previousValue = forecastReportRecordHashMap.get(previousKey);
                assert previousValue != null;
                forecastReportRecordHashMap.putIfAbsent(date, new ForecastReportRecord(date, previousValue.amount, previousValue.currency));
            }
        });
        return forecastReportRecordHashMap.values().stream().toList();
    }

    private void setDefaultTimePeriodSelection() {
        timePeriodSelection.setText(timePeriods.get(0).toString());
        selectedTimePeriod = (ForecastTimePeriod) timePeriods.get(0);
    }

    private void setupTimePeriodSelection() {
        timePeriodSelection.setItems(timePeriods);
        timePeriodSelection.setText(selectedTimePeriod.toString());
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

    private ForecastTimePeriod getSelectedPeriodEnum(String selectedTimePeriodString) {
        try {
            ForecastTimePeriod.valueOf(selectedTimePeriodString);
        } catch (Exception e) {
            ForecastTimePeriod[] l1 = ForecastTimePeriod.values();
            for (ForecastTimePeriod listItem : l1) {
                if (listItem.toString().equals(selectedTimePeriodString)) {
                    return listItem;
                }
            }
        }
        return null;
    }
}