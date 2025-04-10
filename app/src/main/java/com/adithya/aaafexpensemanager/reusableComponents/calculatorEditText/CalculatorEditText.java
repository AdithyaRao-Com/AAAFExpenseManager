package com.adithya.aaafexpensemanager.reusableComponents.calculatorEditText;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adithya.aaafexpensemanager.R;
import com.google.android.material.textfield.TextInputEditText;

public class CalculatorEditText extends TextInputEditText {
    public interface OnCalculatedResultListener {
        void onCalculatedResult(double result);
    }
    private int decimalPlaces = 2;
    private double currentValue = 0.0;
    private boolean allowNegativeValues = true;
    private OnCalculatedResultListener listener;
    public CalculatorEditText(Context context) {
        super(context);
        init(null);
    }

    public CalculatorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CalculatorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    public void setOnCalculatedResultListener(OnCalculatedResultListener listener) {
        this.listener = listener;
    }
    public void setAllowNegativeValues(boolean allowNegativeValues){
        this.allowNegativeValues = allowNegativeValues;
    }
    public boolean getAllowNegativeValues(){
        return allowNegativeValues;
    }
    private void init(AttributeSet attrs) {
        setInputType(InputType.TYPE_NULL);
        setFocusable(false);
        setClickable(true);

        if (attrs != null) {
            android.content.res.TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CalculatorEditText);
            decimalPlaces = typedArray.getInt(R.styleable.CalculatorEditText_decimalPlaces, 2);
            allowNegativeValues = typedArray.getBoolean(R.styleable.CalculatorEditText_allowNegativePlaces, true);
            typedArray.recycle();
        }

        setOnClickListener(v -> showCalculatorDialog());

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //noinspection SizeReplaceableByIsEmpty
                if (s.length() > 0) {
                    try {
                        currentValue = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        // Handle invalid input
                    }
                }
            }
        });
    }

    private void showCalculatorDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.reuse_dialog_calculator_edittext, null);
        final TextView calculatorDisplay = dialogView.findViewById(R.id.calculatorDisplay);
        Button[] buttons = new Button[]{
                dialogView.findViewById(R.id.button7),
                dialogView.findViewById(R.id.button8),
                dialogView.findViewById(R.id.button9),
                dialogView.findViewById(R.id.buttonDivide),
                dialogView.findViewById(R.id.button4),
                dialogView.findViewById(R.id.button5),
                dialogView.findViewById(R.id.button6),
                dialogView.findViewById(R.id.buttonMultiply),
                dialogView.findViewById(R.id.button1),
                dialogView.findViewById(R.id.button2),
                dialogView.findViewById(R.id.button3),
                dialogView.findViewById(R.id.buttonSubtract),
                dialogView.findViewById(R.id.button0),
                dialogView.findViewById(R.id.buttonDecimal),
                dialogView.findViewById(R.id.buttonEquals),
                dialogView.findViewById(R.id.buttonAdd),
                dialogView.findViewById(R.id.buttonClear),
                dialogView.findViewById(R.id.buttonPlusMinus)
        };

        final Calculator calculator = new Calculator(calculatorDisplay);

        for (Button button : buttons) {
            button.setOnClickListener(v -> {
                Button clickedButton = (Button) v;
                calculator.onButtonClick(clickedButton.getText().toString());
            });
        }
        try {
            //noinspection DataFlowIssue
            String amountText = getText().toString();
            double amountDouble = Double.parseDouble(amountText);
            calculator.setCurrentInput(amountText);
        }
        catch (Exception e){
            calculator.setCurrentInput("0");
        }
        AlertDialog.Builder builder = getAlertBuilder(dialogView, calculator);
        builder.show();
    }

    private AlertDialog.Builder getAlertBuilder(View dialogView, Calculator calculator) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            double result = calculator.getResult();
            if(!allowNegativeValues) {
                result = Math.abs(result);
            }
            //noinspection MalformedFormatString
            String formattedResult = String.format("%." + decimalPlaces + "f", result);
            setText(formattedResult);
            if (listener != null) {
                double calculatedResult = Double.parseDouble(formattedResult);
                listener.onCalculatedResult(calculatedResult);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder;
    }

    static class Calculator {
        private final TextView display;
        private String currentInput = "";
        private String previousInput = "";
        private String currentOperator = "";

        Calculator(TextView display) {
            this.display = display;
        }
        void onButtonClick(String buttonText) {
            switch (buttonText) {
                case "C":
                    currentInput = "";
                    previousInput = "";
                    currentOperator = "";
                    display.setText("0");
                    break;
                case "=":
                    calculate();
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    if (!currentInput.isEmpty()) {
                        if (!previousInput.isEmpty()) {
                            calculate();
                        }
                        previousInput = currentInput;
                        currentInput = "";
                        currentOperator = buttonText;
                    }
                    break;
                case "+/-":
                    if (!currentInput.isEmpty()) {
                        calculate();
                        previousInput = currentInput;
                        currentOperator = buttonText;
                        calculate();
                    }
                    break;
                default:
                    currentInput += buttonText;
                    display.setText(currentInput);
                    break;
            }
        }
        public void setCurrentInput(String currentInput){
            if(Double.parseDouble(currentInput)!=0){
                this.currentInput = currentInput;
                this.previousInput = currentInput;
                this.currentOperator = "=";
            }
            display.setText(currentInput);
        }

        private void calculate() {
            if (!previousInput.isEmpty() && !currentInput.isEmpty()) {
                double num1 = Double.parseDouble(previousInput);
                double num2 = Double.parseDouble(currentInput);
                double result = switch (currentOperator) {
                    case "+" -> num1 + num2;
                    case "-" -> num1 - num2;
                    case "*" -> num1 * num2;
                    case "/" -> num1 / num2;
                    case "+/-" -> (-1)*num1;
                    default -> num2;
                };
                currentInput = String.valueOf(result);
                display.setText(currentInput);
                previousInput = "";
                currentOperator = "";
            }
        }

        double getResult() {
            try {
                return Double.parseDouble(currentInput);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }
}