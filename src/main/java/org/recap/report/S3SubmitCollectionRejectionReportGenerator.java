package org.recap.report;

import org.recap.RecapCommonConstants;
import org.recap.model.jpa.ReportEntity;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Created by hemalathas on 21/12/16.
 */
@Component
public class S3SubmitCollectionRejectionReportGenerator extends CommonReportGenerator implements ReportGeneratorInterface {

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT);
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FTP);
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        return generateSubmitCollectionReportFile(fileName, reportEntityList, RecapCommonConstants.FTP_SUBMIT_COLLECTION_REJECTION_REPORT_Q);
    }
}
