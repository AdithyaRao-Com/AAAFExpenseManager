package com.adithya.aaafexpensemanager.transaction.exception;

public class InterCurrencyTransferNotSupported extends RuntimeException{
    public static final String INTER_CURRENCY_TRANSFER_NOT_SUPPORTED =
            "Inter currency transfers is not supported.\n" +
                    "From Account and To Account should have same currency.";
    public static final String INTER_CURRENCY_TRANSFER_NOT_SUPPORTED2 =
            "Inter currency transfers is not supported.\n" +
                    "From Account (%s) and To Account (%s) should have same currency.";
    public InterCurrencyTransferNotSupported(){
        super(INTER_CURRENCY_TRANSFER_NOT_SUPPORTED);
    }
    public InterCurrencyTransferNotSupported(String message){
        super(message);
    }
    public InterCurrencyTransferNotSupported(String fromCurrency,String toCurrency){
        this(String.format(INTER_CURRENCY_TRANSFER_NOT_SUPPORTED2,fromCurrency,toCurrency));
    }
}
