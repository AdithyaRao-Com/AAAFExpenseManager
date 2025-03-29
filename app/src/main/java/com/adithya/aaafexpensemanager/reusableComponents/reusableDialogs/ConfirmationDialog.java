package com.adithya.aaafexpensemanager.reusableComponents.reusableDialogs;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class ConfirmationDialog {
    public ConfirmationDialog(Context context,
                              String title,
                              String message,
                              PositiveListener positiveListener,
                              NegativeListener negativeListener,
                              String positiveButtonText,
                              String negativeButtonText
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if (positiveListener != null) {
            builder.setPositiveButton(positiveButtonText, (dialog, which) -> positiveListener.onPositive());
        }
        if (negativeListener != null) {
            builder.setNegativeButton(negativeButtonText, (dialog, which) -> negativeListener.onNegative());
        }
        builder.show();
    }

    public interface PositiveListener {
        void onPositive();
    }

    public interface NegativeListener {
        void onNegative();
    }
}
