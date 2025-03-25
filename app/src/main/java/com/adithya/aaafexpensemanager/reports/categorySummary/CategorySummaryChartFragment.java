package com.adithya.aaafexpensemanager.reports.categorySummary;

import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategorySummaryChartFragment extends Fragment {
    private LookupEditText timePeriodSelection;
    private MaterialButton previousTimePeriodButton;
    private MaterialTextView timePeriodTextView;
    private MaterialButton nextTimePeriodButton;
    private CategorySummaryFilterDialog filterDialog;
    CategorySummaryRecord.TimePeriod selectedTimePeriod;
    private LocalDate selectedLocalDate;
    private CategorySummaryRepository categorySummaryRepository;
    private TransactionFilter transactionFilter;
    private BarChart barChart;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setupTheViewElements(inflater, container);
        //noinspection DataFlowIssue
        categorySummaryRepository = new CategorySummaryRepository((Application) getContext().getApplicationContext());
        getParseArgs();
        setupTheTimePeriodSelection();
        setupPrevAndNext();
        return view;
    }
    private void setupPrevAndNext() {
        previousTimePeriodButton.setOnClickListener(v -> {
            this.selectedLocalDate = selectedTimePeriod.truncateToStart(selectedLocalDate).minusDays(1);
            setItemTimePeriodTextView(selectedTimePeriod, selectedLocalDate);
            getReportDataFromRepository();
        });
        nextTimePeriodButton.setOnClickListener(v -> {
            LocalDate tempLocalDate =
                    selectedTimePeriod.truncateToEnd(selectedLocalDate).plusDays(1);
            this.selectedLocalDate = selectedTimePeriod.truncateToStart(tempLocalDate);
            setItemTimePeriodTextView(selectedTimePeriod, selectedLocalDate);
            getReportDataFromRepository();
        });
    }
    private void getReportDataFromRepository() {
        if(transactionFilter==null){
            transactionFilter = new TransactionFilter();
        }
        transactionFilter.fromTransactionDate = Integer.parseInt(selectedTimePeriod.truncateToStart(selectedLocalDate).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        transactionFilter.toTransactionDate = Integer.parseInt(selectedTimePeriod.truncateToEnd(selectedLocalDate).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        List<CategorySummaryRecord> records =  categorySummaryRepository.getMonthlySummaryCategoryWise(transactionFilter,selectedTimePeriod);
        createBarChartFromReportData(records);
    }

    private void createBarChartFromReportData(List<CategorySummaryRecord> records) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> categoryNames = new ArrayList<>();
        int loopNumber = 0;
        for(CategorySummaryRecord record : records){
            categoryNames.add(record.category);
            entries.add(new BarEntry(loopNumber,(float)record.amount));
            loopNumber++;
        }
        BarDataSet dataSet = new BarDataSet(entries, "Expenses");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(true);
        barChart.getDescription().setText("Category Summary");
        barChart.getDescription().setTextColor(Color.WHITE);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < categoryNames.size()) {
                    return categoryNames.get(index);
                } else {
                    return "";
                }
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(categoryNames.size());
        xAxis.setTextColor(Color.WHITE);
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        barChart.invalidate();
    }

    private void setupTheTimePeriodSelection() {
        List<Object> timePeriods = Arrays
                .stream(CategorySummaryRecord.TimePeriod.values())
                .collect(Collectors.toList());
        timePeriodSelection.setItemObjects(timePeriods);
        setDefaultValuesForTimePeriod(timePeriods);
        timePeriodSelection.setOnItemClickListener((item,int1) -> {
            this.selectedTimePeriod = (CategorySummaryRecord.TimePeriod) timePeriods.get(int1);
            this.selectedLocalDate = LocalDate.now();
            setItemTimePeriodTextView(selectedTimePeriod, selectedLocalDate);
            getReportDataFromRepository();
        });
    }
    private void setItemTimePeriodTextView(CategorySummaryRecord.TimePeriod selectedTimePeriod,
                                           LocalDate localDate){
        this.selectedLocalDate = localDate;
        this.selectedTimePeriod = selectedTimePeriod;
        String timePeriodStartAndEndDateString = getStartEndDateAsString(selectedTimePeriod,localDate);
        timePeriodTextView.setText(timePeriodStartAndEndDateString);
    }
    private void setDefaultValuesForTimePeriod(List<Object> timePeriods) {
        Bundle args = getArguments();
        if(args==null) {
            this.selectedTimePeriod
                    = (CategorySummaryRecord.TimePeriod) timePeriods.get(0);
            this.selectedLocalDate = LocalDate.now();
        }
        timePeriodSelection.setText(this.selectedTimePeriod.toString());
        setItemTimePeriodTextView(this.selectedTimePeriod, this.selectedLocalDate);
        getReportDataFromRepository();
    }
    @NonNull
    private static String getStartEndDateAsString(CategorySummaryRecord.TimePeriod selectedTimePeriod,
                                                  LocalDate localDate) {
        return selectedTimePeriod
                .truncateToStart(localDate)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " TO " +
                selectedTimePeriod
                        .truncateToEnd(localDate)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    @NonNull
    private View setupTheViewElements(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_report_category_summary_chart, container, false);
        timePeriodSelection = view.findViewById(R.id.timePeriodSelection);
        previousTimePeriodButton = view.findViewById(R.id.previousTimePeriodButton);
        timePeriodTextView = view.findViewById(R.id.timePeriodTextView);
        nextTimePeriodButton = view.findViewById(R.id.nextTimePeriodButton);
        barChart = view.findViewById(R.id.barChart);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.category_summary_menu, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.action_filters){
                    if(transactionFilter==null){
                        transactionFilter = new TransactionFilter();
                    }
                    new CategorySummaryFilterDialog(requireContext(),
                            requireActivity(),
                            transactionFilter,
                            v->{getReportDataFromRepository();})
                            .showDialog();
                    return true;
                }
                else if(menuItem.getItemId()==R.id.action_show_data_chart){
                    Bundle args = new Bundle();
                    args.putParcelable("transactionFilter",transactionFilter);
                    args.putString("timePeriod",selectedTimePeriod.name());
                    Navigation.findNavController(view).navigate(R.id.action_categorySummaryChartFragment_to_categorySummaryFragment,args);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
    public void getParseArgs(){
        Bundle args = getArguments();
        if(args!=null){
            this.transactionFilter = args.getParcelable("transactionFilter");
            String timePeriodString = args.getString("timePeriod");
            if(timePeriodString!=null){
                this.selectedTimePeriod = CategorySummaryRecord.TimePeriod.valueOf(timePeriodString);
                this.selectedLocalDate = transactionFilter.fromTransactionDateToLocalDate();
            }
        }
    }
}
