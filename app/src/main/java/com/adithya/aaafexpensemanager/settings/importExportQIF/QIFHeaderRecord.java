package com.adithya.aaafexpensemanager.settings.importExportQIF;

public class QIFHeaderRecord {
    public String accountName;
    public String accountType;

    public QIFHeaderRecord(String accountName, String accountType) {
        this.accountName = accountName;
        this.accountType = accountType;
    }
}
