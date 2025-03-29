package com.adithya.aaafexpensemanager.settings.currency.exception;

/**
 * @noinspection unused
 */
public class CurrencyExistsException extends Exception {
    public CurrencyExistsException(String message) {
        super(String.format("Currency %s already exists", message));
    }

    public CurrencyExistsException() {
        super("Currency already exists");
    }
}
