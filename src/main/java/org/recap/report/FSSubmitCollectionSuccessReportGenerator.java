package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Created by rajeshbabuk on 20/7/17.
 */
@Component
public class FSSubmitCollectionSuccessReportGenerator extends  CommonReportGenerator implements ReportGeneratorInterface {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT);
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapCommonConstants.FILE_SYSTEM);
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        return generateSubmitCollectionReportFile(fileName, reportEntityList, RecapConstants.FS_SUBMIT_COLLECTION_SUCCESS_REPORT_Q);
    }
}
