package org.recap.report;

import org.recap.RecapCommonConstants;
import org.recap.model.jpa.ReportEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Created by angelind on 30/9/16.
 */
@Component
public class FTPSolrExceptionReportGenerator extends CommonReportGenerator implements ReportGeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(FTPSolrExceptionReportGenerator.class);

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.SOLR_INDEX_EXCEPTION);
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FTP);
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        return generateReportForSolrExceptionCsvRecords(fileName, RecapCommonConstants.FTP_SOLR_EXCEPTION_REPORT_Q, reportEntityList);
    }
}
