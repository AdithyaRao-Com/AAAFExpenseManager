package com.adithya.aaafexpensemanager.reusableComponents.decimalLimitEditText;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

import com.adithya.aaafexpensemanager.R;
import com.google.android.material.textfield.TextInputEditText;

public class DecimalLimitEditText extends TextInputEditText {

    private int decimalPlaces = 6;

    public DecimalLimitEditText(Context context) {
        super(context);
        init(context, null);
    }

    public DecimalLimitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DecimalLimitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DecimalLimitEditText);
            decimalPlaces = typedArray.getInt(R.styleable.DecimalLimitEditText_decimalPlaces, 6);
            typedArray.recycle();
        }

        setFilters(new InputFilter[]{new DecimalDigitsInputFilter(decimalPlaces)});
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        setFilters(new InputFilter[]{new DecimalDigitsInputFilter(decimalPlaces)});
    }

    public class DecimalDigitsInputFilter implements InputFilter {
        private final int decimalDigits;

        public DecimalDigitsInputFilter(int decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int dotPos = -1;
            int len = dest.length();
            for (int i = 0; i < len; i++) {
                char c = dest.charAt(i);
                if (c == '.' || c == ',') {
                    dotPos = i;
                    break;
                }
            }
            if (dotPos >= 0) {
                // protects against many dots
                if (source.equals(".") || source.equals(",")) {
                    return "";
                }
                // if the user is adding digits after the dot
                if (dend <= dotPos) {
                    return null;
                }
                if (len - dotPos >= decimalDigits + 1 && dend > dotPos) { //added dend > dotpos to prevent cut off when deleting whole numbers.
                    return "";
                }
            }
            return null;
        }
    }
}