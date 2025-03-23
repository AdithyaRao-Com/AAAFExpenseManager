package com.adithya.aaafexpensemanager.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;

import java.util.ArrayList;
import java.util.List;

/** */
public class AccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // TODO - The account type group by is not happening properly.
    public interface NavigationListener{
        void changeNavigation(int id, Bundle args);
    }
    /** @noinspection FieldCanBeLocal, unused */
    private final Context context;
    private final List<Object> items;
    private static final int TYPE_ACCOUNT_TYPE = 0;
    private static final int TYPE_ACCOUNT = 1;
    private final NavigationListener navigationListener;

    public AccountsAdapter(List<Account> accounts,
                           Context context,
                           NavigationListener navigationListener
                           ) {
        this.items = new ArrayList<>();
        this.context = context;
        this.navigationListener = navigationListener;
        addSeparators(accounts);
    }
    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_ACCOUNT_TYPE : TYPE_ACCOUNT;
    }
    private void addSeparators(List<Account> accounts) {
        String tempAccountType = null;
        for (Account account : accounts) {
            if (!account.accountType.equals(tempAccountType)) {
                items.add(account.accountType);
                tempAccountType = account.accountType;
            }
            items.add(account);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAccounts(List<Account> accounts) {
        items.clear();
        addSeparators(accounts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ACCOUNT_TYPE) {
            View view = inflater.inflate(R.layout.list_account_type_separator, parent, false);
            return new AccountTypeViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_account, parent, false); // Use custom layout
            return new AccountViewHolder(view);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        if (holder instanceof AccountsAdapter.AccountTypeViewHolder){
            AccountTypeViewHolder accountTypeViewHolder = (AccountTypeViewHolder) holder;
            String accountType = (String) item;
            accountTypeViewHolder.accountTypeTextView.setText(accountType);
        }else if(holder instanceof AccountViewHolder) {
            AccountViewHolder accountViewHolder = (AccountViewHolder) holder;
            Account account = (Account) item;
            if (account != null) {
                accountViewHolder.accountNameTextView.setText(account.accountName);
                accountViewHolder.accountTypeTextView.setText(account.accountType);
                accountViewHolder.accountBalanceTextView.setText(account.accountBalanceToIndianFormat());
                // Set balance color
                if (account.accountBalance >= 0) {
                    accountViewHolder.accountBalanceTextView.setTextColor(Color.GREEN);
                } else {
                    accountViewHolder.accountBalanceTextView.setTextColor(Color.RED);
                }
                accountViewHolder.itemView.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putParcelable("account",account);
                    navigationListener.changeNavigation(R.id.nav_create_account, args);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView accountTypeTextView;
        TextView accountBalanceTextView;
        TextView accountNameTextView;
        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            accountNameTextView = itemView.findViewById(R.id.accountNameTextView);
            accountTypeTextView = itemView.findViewById(R.id.accountTypeTextView);
            accountBalanceTextView = itemView.findViewById(R.id.accountBalanceTextView);
        }
    }
    public static class AccountTypeViewHolder extends RecyclerView.ViewHolder {
        TextView accountTypeTextView;
        public AccountTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTypeTextView = itemView.findViewById(R.id.list_account_type_separator_text);
        }
    }
}
