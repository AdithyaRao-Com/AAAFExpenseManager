package com.adithya.aaafexpensemanager;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuHost;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.adithya.aaafexpensemanager.account.AccountRepository;
import com.adithya.aaafexpensemanager.batchJobs.DailyBackupTasks;
import com.adithya.aaafexpensemanager.databinding.ActivityMainBinding;
import com.adithya.aaafexpensemanager.futureTransaction.FutureTransactionRepository;
import com.adithya.aaafexpensemanager.recurring.RecurringRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

/**
 * @noinspection CallToPrintStackTrace
 */
public class MainActivity extends AppCompatActivity implements MenuHost {
    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        DailyBackupTasks.scheduleDailySave(getApplicationContext());
        try {
            FutureTransactionRepository futureTransactionRepository = new FutureTransactionRepository(getApplication());
            futureTransactionRepository.applyRecurringTransactions();
            RecurringRepository recurringRepository = new RecurringRepository(getApplication());
            recurringRepository.deleteInvalidSchedules();
            AccountRepository accountRepository = new AccountRepository(getApplication());
            accountRepository.refreshAccountTags();
            recurringRepository.keepFutureTransactionsUpToDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        com.adithya.aaafexpensemanager.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        fab = binding.appBarMain.fab;
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_account,
                R.id.nav_transaction,
                R.id.nav_recurring,
                R.id.nav_settings,
                R.id.nav_reports,
                R.id.nav_import_export_home,
                R.id.nav_transaction_filter_list,
                R.id.nav_test_fragment)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_account) {
                navController.navigate(R.id.nav_account);
            } else if (item.getItemId() == R.id.nav_transaction) {
                navController.navigate(R.id.nav_transaction);
            } else if (item.getItemId() == R.id.nav_recurring) {
                navController.navigate(R.id.nav_recurring);
            } else if (item.getItemId() == R.id.nav_settings) {
                navController.navigate(R.id.nav_settings);
            } else if (item.getItemId() == R.id.nav_export_database) {
                navController.navigate(R.id.nav_export_database);
            } else if (item.getItemId() == R.id.nav_import_database) {
                navController.navigate(R.id.nav_import_database);
            } else if (item.getItemId() == R.id.nav_future_transactions) {
                navController.navigate(R.id.nav_future_transactions);
            } else if (item.getItemId() == R.id.nav_reports) {
                navController.navigate(R.id.nav_reports);
            } else if (item.getItemId() == R.id.nav_currency) {
                navController.navigate(R.id.nav_currency);
            } else if (item.getItemId() == R.id.nav_import_export_home) {
                navController.navigate(R.id.nav_import_export_home);
            } else if (item.getItemId() == R.id.nav_create_saved_report) {
                navController.navigate(R.id.nav_create_saved_report);
            } else if (item.getItemId() == R.id.nav_transaction_filter_list) {
                navController.navigate(R.id.nav_transaction_filter_list);
            } else if (item.getItemId() == R.id.nav_test_fragment) {
                navController.navigate(R.id.nav_test_fragment);
            }
            drawer.close();
            return true;
        });
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d("Navigation", "Destination changed: " + destination.getLabel());
            final int fabDestination;
            if (destination.getId() == R.id.nav_account) {
                fabDestination = R.id.nav_create_account;
            } else if (destination.getId() == R.id.nav_transaction) {
                fabDestination = R.id.nav_create_transaction;
            } else if (destination.getId() == R.id.nav_category) {
                fabDestination = R.id.nav_create_category;
            } else if (destination.getId() == R.id.nav_account_type) {
                fabDestination = R.id.nav_create_account_type;
            } else if (destination.getId() == R.id.nav_recurring) {
                fabDestination = R.id.nav_create_recurring;
            } else if (destination.getId() == R.id.nav_currency) {
                fabDestination = R.id.nav_create_currency;
            } else if (destination.getId() == R.id.nav_transaction_filter_list) {
                fabDestination = R.id.nav_create_saved_report;
            }else {
                fabDestination = 0;
            }
            if (fabDestination != 0) {
                fab.show();
                fab.setOnClickListener(view -> navController.navigate(fabDestination));
            } else {
                fab.hide();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}