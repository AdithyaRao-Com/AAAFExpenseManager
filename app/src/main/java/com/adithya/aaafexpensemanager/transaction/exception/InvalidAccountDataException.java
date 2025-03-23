package com.adithya.aaafexpensemanager.transaction.exception;

public class InvalidAccountDataException extends RuntimeException{
    public static final String INVALID_ACCOUNT_DATA_NULL = "Account is null. Please select valid account.";
    public InvalidAccountDataException(String message) {
        super(message);
    }
    public InvalidAccountDataException(){
        this("Invalid Account Data");
    }
}
