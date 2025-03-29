package com.adithya.aaafexpensemanager.reusableComponents.lookupEditText;

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

public class LookupEditText extends TextInputEditText implements View.OnClickListener {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<LookupEditTextItem> items;
    public LookupEditText(Context context) {
        super(context);
        init(context);
    }

    public LookupEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LookupEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOnClickListener(this);
        setFocusable(false); // Make it non-focusable
        setClickable(true); // Make it clickable
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setItemStrings(List<String> items) {
        this.items = new ArrayList<>();
        for (String item : items) {
            this.items.add(new DefaultLookupEditTextItem(item));
        }
    }

    public void setItems(List<LookupEditTextItem> items) {
        this.items = new ArrayList<>();
        this.items.addAll(items);
    }

    public void setItemObjects(List<Object> items) {
        this.items = new ArrayList<>();
        for (Object item : items) {
            this.items.add(new DefaultLookupEditTextItem(item));
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
        showLookupDialog();
    }

    private void showLookupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.reuse_dialog_base_lookupedittext, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        EditText searchEditText = dialogView.findViewById(R.id.search_edittext);
        RecyclerView itemsRecyclerView = dialogView.findViewById(R.id.items_recyclerview);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        LookupAdapter adapter = new LookupAdapter(items, this::onItemSelected, alertDialog);
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

        alertDialog.show();
    }

    private void onItemSelected(LookupEditTextItem item) {
        setText(item.toEditTextLookupString());
        if (onItemClickListener != null) {
            int position = items.indexOf(item);
            onItemClickListener.onItemClick(item, position);
        }
    }

    public interface LookupEditTextItem {
        String toEditTextLookupString();
    }

    public interface OnItemClickListener {
        void onItemClick(LookupEditTextItem selectedItem, int position);
    }

    public static class DefaultLookupEditTextItem implements LookupEditTextItem {
        private Object lookupEditTextItem;

        public DefaultLookupEditTextItem() {
            super();
        }

        public DefaultLookupEditTextItem(Object object) {
            this.lookupEditTextItem = object;
        }

        public DefaultLookupEditTextItem(String object) {
            this.lookupEditTextItem = object;
        }

        @Override
        public String toEditTextLookupString() {
            return this.lookupEditTextItem.toString();
        }
    }
}