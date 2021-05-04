package org.recap.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionResponse {

    private String message;
    private String details;
    private Date timestamp;
    private Throwable throwable;

}