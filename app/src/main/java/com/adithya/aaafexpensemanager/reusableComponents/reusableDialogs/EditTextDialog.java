package com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.adithya.aaafexpensemanager.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Optional;

/**
 * @noinspection ExtractMethodRecommender
 */
public class EditTextDialog {
    private final Context context;
    private final View customLayout;
    private final TextInputEditText materialEditText;
    private final PositiveListener positiveListener;
    private final NegativeListener negativeListener;
    private final String title;
    private final String positiveButtonText;
    private final String negativeButtonText;

    @SuppressLint("InflateParams")
    public EditTextDialog(@NonNull
                          Context context,
                          @NonNull
                          String title,
                          @NonNull
                          String positiveButtonText,
                          String negativeButtonText,
                          @NonNull
                          PositiveListener positiveListener,
                          NegativeListener negativeListener,
                          String inputHint) {
        this.context = context;
        this.title = title;
        this.positiveListener = positiveListener;
        this.negativeListener = negativeListener;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.customLayout = LayoutInflater.from(context).inflate(R.layout.reuse_dialog_edit_text, null);
        this.materialEditText = customLayout.findViewById(R.id.materialEditText);
        TextInputLayout textInputLayout = customLayout.findViewById(R.id.textInputLayout);
        if (inputHint != null && !inputHint.isBlank()) {
            textInputLayout.setHint(inputHint);
        }
        AlertDialog.Builder builder = getAlertBuilder();
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public EditTextDialog(Context context,
                          String title,
                          PositiveListener positiveListener,
                          NegativeListener negativeListener) {
        this(context,
                title,
                context.getString(R.string.ok),
                context.getString(R.string.cancel),
                positiveListener,
                negativeListener,
                null
        );
    }

    public EditTextDialog(Context context,
                          String title,
                          PositiveListener positiveListener,
                          NegativeListener negativeListener,
                          String inputHint) {
        this(context,
                title,
                context.getString(R.string.ok),
                context.getString(R.string.cancel),
                positiveListener,
                negativeListener,
                inputHint
        );
    }

    @NonNull
    private AlertDialog.Builder getAlertBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(title);
        builder.setView(this.customLayout);
        if (positiveListener != null) {
            builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                String inputText = Optional.ofNullable(materialEditText.getText())
                        .orElse(Editable.Factory.getInstance().newEditable(""))
                        .toString();
                positiveListener.onPositive(inputText);
            });
        }
        if (negativeListener != null) {
            builder.setNegativeButton(negativeButtonText, (dialog, which) -> {
                String inputText = Optional.ofNullable(materialEditText.getText())
                        .orElse(Editable.Factory.getInstance().newEditable(""))
                        .toString();
                negativeListener.onNegative(inputText);
            });
        }
        return builder;
    }

    public interface PositiveListener {
        void onPositive(String inputText);
    }

    public interface NegativeListener {
        void onNegative(String inputText);
    }
}
