package org.recap.model.camel;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rajeshbabuk on 19/1/17.
 */
@Data
public class EmailPayLoad implements Serializable {
    private String itemBarcode;
    private String itemInstitution;
    private String oldCgd;
    private String newCgd;
    private String notes;
    private String jobName;
    private String jobDescription;
    private String jobAction;
    private Date startDate;
    private String status;
    private String message;
    private String from;
    private String to;
    private String cc;
    private String subject;
}
