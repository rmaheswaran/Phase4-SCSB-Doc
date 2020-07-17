package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Created by hemalathas on 21/11/16.
 */
@Component
public class FSAccessionReportGenerator extends CommonReportGenerator implements ReportGeneratorInterface{

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.ACCESSION_SUMMARY_REPORT);
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FILE_SYSTEM);
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        return generateAccessionReportFile(fileName, reportEntityList, RecapConstants.FS_ACCESSION_SUMMARY_REPORT_Q);
    }
}
