package org.recap.exception;

public class CGDRoundTripReportException extends RuntimeException {

    public CGDRoundTripReportException(String message){
        super(message);
    }
    public CGDRoundTripReportException(String message,Throwable throwable){
        super(message,throwable);
    }
}
