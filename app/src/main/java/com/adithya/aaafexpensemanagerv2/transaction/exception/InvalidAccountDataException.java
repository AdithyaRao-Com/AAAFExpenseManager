package com.adithya.aaafexpensemanagerv2.transaction.exception;

public class InvalidAccountDataException extends RuntimeException {
    public static final String INVALID_ACCOUNT_DATA_NULL = "Account is null. Please select valid account.";

    public InvalidAccountDataException(String message) {
        super(message);
    }

    public InvalidAccountDataException() {
        this("Invalid Account Data");
    }
}
