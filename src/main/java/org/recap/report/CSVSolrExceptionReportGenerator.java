package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.ReportEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Created by angelind on 30/9/16.
 */
@Component
public class CSVSolrExceptionReportGenerator extends CommonReportGenerator implements ReportGeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(CSVSolrExceptionReportGenerator.class);

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.SOLR_INDEX_EXCEPTION) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FILE_SYSTEM) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        return generateReportForSolrExceptionCsvRecords(fileName, RecapCommonConstants.CSV_SOLR_EXCEPTION_REPORT_Q, reportEntityList);
    }
}
