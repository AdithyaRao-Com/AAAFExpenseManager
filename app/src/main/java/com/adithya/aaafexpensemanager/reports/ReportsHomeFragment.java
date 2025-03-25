package com.adithya.aaafexpensemanager.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.adithya.aaafexpensemanager.R;

public class ReportsHomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports_home, container, false);
        TextView categorySummaryTextView = view.findViewById(R.id.categorySummaryTextView);
        //noinspection unused
        TextView balanceForecastTextView = view.findViewById(R.id.balanceForecastTextView);
        categorySummaryTextView.setOnClickListener(v->{
            //noinspection DataFlowIssue
            NavHostFragment
                    .findNavController(getParentFragment())
                    .navigate(R.id.action_reportsHomeFragment_to_categorySummaryFragment);
        });
        balanceForecastTextView.setOnClickListener( v->{
            //noinspection DataFlowIssue
            NavHostFragment
                    .findNavController(getParentFragment())
                    .navigate(R.id.action_reportsHomeFragment_to_balanceForecastFragment);
        });
        return view;
    }
}