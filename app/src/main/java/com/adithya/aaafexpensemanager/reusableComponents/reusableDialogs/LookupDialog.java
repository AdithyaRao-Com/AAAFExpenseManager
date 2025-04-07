package com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.R;
import com.adithya.aaafexpensemanager.reusableComponents.lookupEditText.LookupEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Optional;

public class LookupDialog {
    private final Context context;
    private final View customLayout;
    private final LookupEditText lookupEditText;
    private final LookupDialog.PositiveListener positiveListener;
    private final LookupDialog.NegativeListener negativeListener;
    private final String title;
    private final String positiveButtonText;
    private final String negativeButtonText;

    @SuppressLint("InflateParams")
    public LookupDialog(@NonNull
                        Context context,
                        @NonNull
                        String title,
                        List<String> lookupItems,
                        @NonNull
                        LookupDialog.PositiveListener positiveListener,
                        LookupDialog.NegativeListener negativeListener,
                        String inputHint,
                        @NonNull
                        String positiveButtonText,
                        String negativeButtonText
    ) {
        this.context = context;
        this.title = title;
        this.positiveListener = positiveListener;
        this.negativeListener = negativeListener;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.customLayout = LayoutInflater.from(context).inflate(R.layout.reuse_dialog_lookup, null);
        this.lookupEditText = customLayout.findViewById(R.id.dialogLookUpEditText);
        this.lookupEditText.setItemStrings(lookupItems);
        TextInputLayout textInputLayout = customLayout.findViewById(R.id.textInputLayout);
        if (inputHint != null && !inputHint.isBlank()) {
            textInputLayout.setHint(inputHint);
        }
        getAlertBuilder();
    }

    public LookupDialog(@NonNull
                        Context context,
                        @NonNull
                        String title,
                        List<String> lookupItems,
                        @NonNull
                        LookupDialog.PositiveListener positiveListener,
                        LookupDialog.NegativeListener negativeListener,
                        String inputHint) {
        this(context,
                title,
                lookupItems,
                positiveListener,
                negativeListener,
                inputHint,
                "Ok",
                "Cancel");
    }

    /**
     * @noinspection unused
     */
    public LookupDialog(@NonNull
                        Context context,
                        @NonNull
                        String title,
                        List<String> lookupItems,
                        @NonNull
                        LookupDialog.PositiveListener positiveListener,
                        LookupDialog.NegativeListener negativeListener) {
        this(context,
                title,
                lookupItems,
                positiveListener,
                negativeListener,
                "",
                "Ok",
                "Cancel");
    }

    private void getAlertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(title);
        builder.setView(this.customLayout);
        if (negativeListener != null) builder.setNegativeButton(negativeButtonText, null);
        if (positiveListener != null) builder.setPositiveButton(positiveButtonText, null);
        AlertDialog dialog = builder.create();
        dialog.show();
        if (positiveListener != null) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
                String selectedText = Optional.ofNullable(lookupEditText.getText())
                        .orElse(Editable.Factory.getInstance().newEditable(""))
                        .toString();
                if (selectedText.isBlank()) {
                    lookupEditText.setError("Invalid data provided");
                } else {
                    positiveListener.onPositive(selectedText);
                    dialog.dismiss();
                }
            });
        }
        if (negativeListener != null) {
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(view -> {
                String selectedText;
                selectedText = Optional.ofNullable(lookupEditText.getText())
                        .orElse(Editable.Factory.getInstance().newEditable(""))
                        .toString();
                negativeListener.onNegative(selectedText);
                dialog.dismiss();
            });
        }
    }

    public interface PositiveListener {
        void onPositive(String selectedText);
    }

    public interface NegativeListener {
        void onNegative(String selectedText);
    }
}
