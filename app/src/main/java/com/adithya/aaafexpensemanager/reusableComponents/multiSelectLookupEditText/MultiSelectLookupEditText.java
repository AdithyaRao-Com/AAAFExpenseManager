package com.adithya.aaafexpensemanager.reusableComponents.multiSelectLookupEditText;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adithya.aaafexpensemanager.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectLookupEditText extends TextInputEditText implements View.OnClickListener {

    private Context context;
    private OnItemsSelectedListener onItemsSelectedListener;
    private List<String> items;
    private List<String> selectedItems = new ArrayList<>();

    public interface OnItemsSelectedListener {
        void onItemsSelected(List<String> selectedItems);
    }

    public MultiSelectLookupEditText(Context context) {
        super(context);
        init(context);
    }

    public MultiSelectLookupEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MultiSelectLookupEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOnClickListener(this);
        setFocusable(false);
        setClickable(true);
    }

    public void setOnItemsSelectedListener(OnItemsSelectedListener listener) {
        this.onItemsSelectedListener = listener;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
    public void setSelectedItems(List<String> selectedItems) {
        this.selectedItems = selectedItems;
        setSelectedText();
    }
    public void setItemObjects(List<Object> items) {
        this.items = new ArrayList<>();
        for (Object item : items) {
            this.items.add(item.toString());
        }
    }

    @Override
    public void setError(CharSequence error) {
        if (error != null) {
            Snackbar.make(this, error, Snackbar.LENGTH_SHORT).show();
        }
        super.setError(error);
    }

    @Override
    public void onClick(View v) {
        showMultiSelectDialog();
    }

    private void showMultiSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.reuse_dialog_multiselect_lookup, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        EditText searchEditText = dialogView.findViewById(R.id.search_edittext);
        RecyclerView itemsRecyclerView = dialogView.findViewById(R.id.items_recyclerview);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        MultiSelectLookupAdapter adapter = new MultiSelectLookupAdapter(items, selectedItems, (item, isChecked) -> {
            if (isChecked) {
                if (!selectedItems.contains(item)) {
                    selectedItems.add(item);
                }
            } else {
                selectedItems.remove(item);
            }
        });
        itemsRecyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialogView.findViewById(R.id.select_items_button).setOnClickListener(v -> {
            setSelectedText();
            if (onItemsSelectedListener != null) {
                onItemsSelectedListener.onItemsSelected(selectedItems);
            }
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void setSelectedText() {
        StringBuilder selectedText = new StringBuilder();
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedText.append(selectedItems.get(i));
            if (i < selectedItems.size() - 1) {
                selectedText.append(", ");
            }
        }
        setText(selectedText.toString());
    }

    public List<String> getSelectedItems(){
        return this.selectedItems;
    }
}