package org.recap.model.reports;

import lombok.Data;

/**
 * Created by premkb on 23/3/17.
 */
@Data
public class ReportDataRequest {
    private String fileName;
    private String institutionCode;
    private String reportType;
    private String transmissionType;
}
