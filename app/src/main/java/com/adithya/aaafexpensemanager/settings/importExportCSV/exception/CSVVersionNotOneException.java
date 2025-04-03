package com.adithya.aaafexpensemanager.settings.importExportCSV.exception;

public class CSVVersionNotOneException extends RuntimeException{
    public CSVVersionNotOneException(String message){
        super(message);
    }
    public CSVVersionNotOneException(){
        super("CSV version is not 1");
    }
    public CSVVersionNotOneException(Exception e){
        super(e);
    }
}
