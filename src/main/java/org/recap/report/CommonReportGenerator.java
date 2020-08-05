package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.AccessionSummaryRecord;
import org.recap.model.csv.SolrExceptionReportReCAPCSVRecord;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.AccessionSummaryRecordGenerator;
import org.recap.util.ReCAPCSVSolrExceptionRecordGenerator;
import org.recap.util.SubmitCollectionReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CommonReportGenerator.class);

    @Autowired
    private ProducerTemplate producerTemplate;

    public String generateSubmitCollectionReportFile(String fileName, List<ReportEntity> reportEntityList, String reportQueue) {
        String generatedFileName;
        List<SubmitCollectionReportRecord> submitCollectionReportRecordList = new ArrayList<>();
        SubmitCollectionReportGenerator submitCollectionReportGenerator = new SubmitCollectionReportGenerator();
        for(ReportEntity reportEntity : reportEntityList) {
            List<SubmitCollectionReportRecord> submitCollectionReportRecords = submitCollectionReportGenerator.prepareSubmitCollectionRejectionRecord(reportEntity);
            submitCollectionReportRecordList.addAll(submitCollectionReportRecords);
        }
        DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
        generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
        if (StringUtils.containsIgnoreCase(reportQueue, RecapConstants.SUBMIT_COLLECTION_SUMMARY_Q_SUFFIX)) {
            fileName = generatedFileName;
        }
        producerTemplate.sendBodyAndHeader(reportQueue, submitCollectionReportRecordList, RecapConstants.FILE_NAME, fileName);
        return generatedFileName;

    }

    public String generateAccessionReportFile(String fileName, List<ReportEntity> reportEntityList, String reportQueue) {
        String generatedFileName;
        List<AccessionSummaryRecord> accessionSummaryRecordList;
        AccessionSummaryRecordGenerator accessionSummaryRecordGenerator = new AccessionSummaryRecordGenerator();
        accessionSummaryRecordList = accessionSummaryRecordGenerator.prepareAccessionSummaryReportRecord(reportEntityList);
        producerTemplate.sendBodyAndHeader(reportQueue, accessionSummaryRecordList, RecapConstants.FILE_NAME, fileName);
        DateFormat df = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_FOR_FILE_NAME);
        generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
        return generatedFileName;
    }

    public String generateReportForSolrExceptionCsvRecords(String fileName, String queueName, List<ReportEntity> reportEntityList) {
        List<SolrExceptionReportReCAPCSVRecord> solrExceptionReportReCAPCSVRecords = getSolrExceptionReportReCAPCSVRecords(reportEntityList);
        logger.info("Total Num of CSVRecords Prepared : {}  ",solrExceptionReportReCAPCSVRecords.size());

        if(!CollectionUtils.isEmpty(solrExceptionReportReCAPCSVRecords)) {
            producerTemplate.sendBodyAndHeader(queueName, solrExceptionReportReCAPCSVRecords, RecapCommonConstants.REPORT_FILE_NAME, fileName);
            DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
            return fileName + "-" + df.format(new Date()) + ".csv";
        }
        return null;
    }

    public List<SolrExceptionReportReCAPCSVRecord> getSolrExceptionReportReCAPCSVRecords(List<ReportEntity> reportEntityList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<SolrExceptionReportReCAPCSVRecord> solrExceptionReportReCAPCSVRecords = new ArrayList<>();

        ReCAPCSVSolrExceptionRecordGenerator reCAPCSVSolrExceptionRecordGenerator = new ReCAPCSVSolrExceptionRecordGenerator();
        for(ReportEntity reportEntity : reportEntityList) {
            SolrExceptionReportReCAPCSVRecord solrExceptionReportReCAPCSVRecord = reCAPCSVSolrExceptionRecordGenerator.prepareSolrExceptionReportReCAPCSVRecord(reportEntity, new SolrExceptionReportReCAPCSVRecord());
            solrExceptionReportReCAPCSVRecords.add(solrExceptionReportReCAPCSVRecord);
        }

        stopWatch.stop();
        logger.info("Total time taken to prepare CSVRecords : {} ",stopWatch.getTotalTimeSeconds());
        return solrExceptionReportReCAPCSVRecords;
    }
}
