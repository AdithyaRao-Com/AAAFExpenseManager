package com.adithya.aaafexpensemanager.reports.categorySummary;

import android.app.Application;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilter;
import com.adithya.aaafexpensemanager.transactionFilter.TransactionFilterDialog;
import com.adithya.aaafexpensemanager.util.CurrencyFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategorySummaryFragment extends Fragment {
    CategorySummaryRecord.TimePeriod selectedTimePeriod;
    private LookupEditText timePeriodSelection;
    private MaterialButton previousTimePeriodButton;
    private MaterialTextView timePeriodTextView;
    private MaterialButton nextTimePeriodButton;
    private MaterialTextView totalValueTextView;
    private TransactionFilterDialog filterDialog;
    private LocalDate selectedLocalDate;
    private RecyclerView reportsRecyclerView;
    private CategorySummaryAdapter categorySummaryAdapter;
    private CategorySummaryRepository categorySummaryRepository;
    private TransactionFilter transactionFilter;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setupTheViewElements(inflater, container);
        //noinspection DataFlowIssue
        categorySummaryRepository = new CategorySummaryRepository((Application) getContext().getApplicationContext());
        getParseArgs();
        setUpRecyclerView();
        setupTheTimePeriodSelection();
        setupPrevAndNext();
        return view;
    }

    private void setUpRecyclerView() {
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<CategorySummaryRecord> categorySummaryRecords = new ArrayList<>();
        categorySummaryAdapter = new CategorySummaryAdapter(categorySummaryRecords);
        reportsRecyclerView.setAdapter(categorySummaryAdapter);
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

    /**
     * @noinspection deprecation
     */
    private void getReportDataFromRepository() {
        if (transactionFilter == null) {
            transactionFilter = new TransactionFilter();
        }
        transactionFilter.fromTransactionDate = Integer.parseInt(selectedTimePeriod.truncateToStart(selectedLocalDate).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        transactionFilter.toTransactionDate = Integer.parseInt(selectedTimePeriod.truncateToEnd(selectedLocalDate).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        List<CategorySummaryRecord> records = categorySummaryRepository.getMonthlySummaryCategoryWise(transactionFilter, selectedTimePeriod);
        categorySummaryAdapter.setRecords(records);
        double totalAmount = records.stream().mapToDouble(c -> c.amount).sum();
        if (totalAmount < 0) {
            totalValueTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            totalValueTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        totalValueTextView.setText(CurrencyFormatter.formatIndianStyle(totalAmount, "INR"));
    }

    private void setupTheTimePeriodSelection() {
        List<Object> timePeriods = Arrays
                .stream(CategorySummaryRecord.TimePeriod.values())
                .collect(Collectors.toList());
        timePeriodSelection.setItemObjects(timePeriods);
        setDefaultValuesForTimePeriod(timePeriods);
        timePeriodSelection.setOnItemClickListener((item, int1) -> {
            this.selectedTimePeriod = (CategorySummaryRecord.TimePeriod) timePeriods.get(int1);
            this.selectedLocalDate = LocalDate.now();
            setItemTimePeriodTextView(selectedTimePeriod, selectedLocalDate);
            getReportDataFromRepository();
        });
    }

    private void setItemTimePeriodTextView(CategorySummaryRecord.TimePeriod selectedTimePeriod,
                                           LocalDate localDate) {
        this.selectedLocalDate = localDate;
        this.selectedTimePeriod = selectedTimePeriod;
        String timePeriodStartAndEndDateString = getStartEndDateAsString(selectedTimePeriod, localDate);
        timePeriodTextView.setText(timePeriodStartAndEndDateString);
    }

    private void setDefaultValuesForTimePeriod(List<Object> timePeriods) {
        Bundle args = getArguments();
        if (args == null) {
            this.selectedTimePeriod
                    = (CategorySummaryRecord.TimePeriod) timePeriods.get(0);
            this.selectedLocalDate = LocalDate.now();
        }
        timePeriodSelection.setText(this.selectedTimePeriod.toString());
        setItemTimePeriodTextView(this.selectedTimePeriod, this.selectedLocalDate);
        getReportDataFromRepository();
    }

    @NonNull
    private View setupTheViewElements(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_report_category_summary, container, false);
        timePeriodSelection = view.findViewById(R.id.timePeriodSelection);
        previousTimePeriodButton = view.findViewById(R.id.previousTimePeriodButton);
        timePeriodTextView = view.findViewById(R.id.timePeriodTextView);
        nextTimePeriodButton = view.findViewById(R.id.nextTimePeriodButton);
        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        totalValueTextView = view.findViewById(R.id.totalValueTextView);
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
                if (menuItem.getItemId() == R.id.action_filters) {
                    if (transactionFilter == null) {
                        transactionFilter = new TransactionFilter();
                    }
                    new TransactionFilterDialog(requireContext(),
                            requireActivity(),
                            transactionFilter,
                            v -> getReportDataFromRepository(),
                            false)
                            .showDialog();
                    return true;
                } else if (menuItem.getItemId() == R.id.action_show_data_chart) {
                    Bundle args = new Bundle();
                    transactionFilter.periodName = selectedTimePeriod.name();
                    args.putParcelable("transactionFilter", transactionFilter);
                    Navigation.findNavController(view).navigate(R.id.action_categorySummaryFragment_to_categorySummaryChartFragment, args);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    /**
     * @noinspection deprecation
     */
    public void getParseArgs() {
        Bundle args = getArguments();
        if (args != null) {
            this.transactionFilter = args.getParcelable("transactionFilter");
            assert this.transactionFilter != null;
            String timePeriodString = this.transactionFilter.periodName;
            if (timePeriodString != null) {
                this.selectedTimePeriod = getSelectedPeriodEnum(timePeriodString);
                this.selectedLocalDate = transactionFilter.fromTransactionDateToLocalDate();
            }
        }
    }

    private CategorySummaryRecord.TimePeriod getSelectedPeriodEnum(String selectedTimePeriodString) {
        try {
            CategorySummaryRecord.TimePeriod.valueOf(selectedTimePeriodString);
        } catch (Exception e) {
            CategorySummaryRecord.TimePeriod[] l1 = CategorySummaryRecord.TimePeriod.values();
            for (CategorySummaryRecord.TimePeriod listItem : l1) {
                if (listItem.toString().equals(selectedTimePeriodString)) {
                    return listItem;
                }
            }
        }
        return null;
    }
}