package com.adithya.aaafexpensemanager.settings.aboutApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.util.AppConstants;
import com.adithya.aaafexpensemanager.util.DBHelperSharedPrefs;

public class AboutAppFragment extends Fragment {

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        TextView applicationVersionTextView = view.findViewById(R.id.applicationVersionTextView);
        TextView databaseVersionTextView = view.findViewById(R.id.databaseVersionTextView);
        applicationVersionTextView.setText(getString(R.string.application_version)+":"+ AppConstants.APPLICATION_VERSION);
        DBHelperSharedPrefs dbHelperSharedPrefs = new DBHelperSharedPrefs(requireContext());
        databaseVersionTextView.setText(getString(R.string.database_version)+":"+ dbHelperSharedPrefs.getCurrentDataBaseVersion(0));
        return view;
    }
}