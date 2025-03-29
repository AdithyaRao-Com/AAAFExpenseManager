package com.adithya.aaafexpensemanager.reusableComponents.lookupAutoCompleteList;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.adithya.aaafexpensemanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection FieldMayBeFinal, FieldCanBeLocal
 */
public class LookupAutoCompleteList extends AppCompatEditText {
    private List<String> promptList = new ArrayList<>();
    private List<String> selectedItems = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private ListView listView;

    public LookupAutoCompleteList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFocusable(false);
        setOnClickListener(v -> showSelectionDialog());
    }

    public void setPromptList(List<String> promptList) {
        this.promptList = promptList;
    }

    private void showSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.reuse_dialog_autocomplete_list_lookup, null);
        builder.setView(dialogView);

        AutoCompleteTextView autoCompleteTextView = dialogView.findViewById(R.id.autoCompleteTextView);
        Button addButton = dialogView.findViewById(R.id.addButton);
        listView = dialogView.findViewById(R.id.listView);
        Button okButton = dialogView.findViewById(R.id.okButton);

        autoCompleteTextView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, promptList));
        listAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selectedItems);
        listView.setAdapter(listAdapter);
        addButton.setOnClickListener(v -> {
            String input = autoCompleteTextView.getText().toString().trim();
            if (!input.isEmpty() && !selectedItems.contains(input)) {
                selectedItems.add(input);
                listAdapter.notifyDataSetChanged();
                autoCompleteTextView.setText("");
            } else {
                Toast.makeText(getContext(), "Duplicate or empty value!", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedItems.remove(position);
            listAdapter.notifyDataSetChanged();
        });

        AlertDialog dialog = builder.create();
        okButton.setOnClickListener(v -> {
            setText(TextUtils.join(", ", selectedItems));
            dialog.dismiss();
        });

        dialog.show();
    }

    public List<String> getSelectedItems() {
        return this.selectedItems;
    }

    public void setSelectedItems(List<String> selectedItems) {
        if (selectedItems == null) {
            this.selectedItems = new ArrayList<>();
        } else {
            this.selectedItems = selectedItems;
            setText(TextUtils.join(", ", this.selectedItems));
        }
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }
}
