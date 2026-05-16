package com.adithya.aaafexpensemanagerv2.settings.currency;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanagerv2.R;

import java.util.ArrayList;
import java.util.List;

public class CurrencyHomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.currencyHomeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<CurrencyMenuItem> items = new ArrayList<>();
        items.add(new CurrencyMenuItem(getString(R.string.set_change_primary_account), v -> 
            Navigation.findNavController(v).navigate(R.id.action_currencyHomeFragment_to_createPrimaryCurrencyFragment)));
        items.add(new CurrencyMenuItem(getString(R.string.add_edit_currencies), v -> 
            Navigation.findNavController(v).navigate(R.id.action_currencyHomeFragment_to_currencyFragment)));

        recyclerView.setAdapter(new CurrencyMenuAdapter(items));

        return view;
    }

    private static class CurrencyMenuItem {
        String title;
        View.OnClickListener listener;

        CurrencyMenuItem(String title, View.OnClickListener listener) {
            this.title = title;
            this.listener = listener;
        }
    }

    private static class CurrencyMenuAdapter extends RecyclerView.Adapter<CurrencyMenuAdapter.ViewHolder> {
        private final List<CurrencyMenuItem> items;

        CurrencyMenuAdapter(List<CurrencyMenuItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CurrencyMenuItem item = items.get(position);
            holder.titleTextView.setText(item.title);
            holder.itemView.setOnClickListener(item.listener);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;

            ViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.menuItemTitle);
            }
        }
    }
}
