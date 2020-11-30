package org.recap.report;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    List<ReportGeneratorInterface> reportGenerators;

    @Autowired
    private CSVSolrExceptionReportGenerator csvSolrExceptionReportGenerator;

    @Autowired
    private S3SolrExceptionReportGenerator s3SolrExceptionReportGenerator;

    @Autowired
    private FSDeAccessionReportGenerator fsDeAccessionReportGenerator;

    @Autowired
    private S3DeAccessionReportGenerator s3DeAccessionReportGenerator;

    @Autowired
    private FSAccessionReportGenerator fsAccessionReportGenerator;

    @Autowired
    private S3AccessionReportGenerator s3AccessionReportGenerator;

    @Autowired
    private FSSubmitCollectionRejectionReportGenerator fsSubmitCollectionRejectionReportGenerator;

    @Autowired
    private S3SubmitCollectionRejectionReportGenerator s3SubmitCollectionRejectionReportGenerator;

    @Autowired
    private FSSubmitCollectionExceptionReportGenerator fsSubmitCollectionExceptionReportGenerator;

    @Autowired
    private S3SubmitCollectionExceptionReportGenerator s3SubmitCollectionExceptionReportGenerator;

    @Autowired
    private S3SubmitCollectionSummaryReportGenerator s3SubmitCollectionSummaryReportGenerator;

    @Autowired
    private FSSubmitCollectionSummaryReportGenerator fsSubmitCollectionSummaryReportGenerator;

    @Autowired
    private FSOngoingAccessionReportGenerator fsOngoingAccessionReportGenerator;

    @Autowired
    private S3OngoingAccessionReportGenerator s3OngoingAccessionReportGenerator;

    @Autowired
    private S3SubmitCollectionReportGenerator s3SubmitCollectionReportGenerator;

    @Autowired
    private S3SubmitCollectionSuccessReportGenerator s3SubmitCollectionSuccessReportGenerator;

    @Autowired
    private FSSubmitCollectionSuccessReportGenerator fsSubmitCollectionSuccessReportGenerator;

    @Autowired
    private S3SubmitCollectionFailureReportGenerator s3SubmitCollectionFailureReportGenerator;

    @Autowired
    private FSSubmitCollectionFailureReportGenerator fsSubmitCollectionFailureReportGenerator;

    /**
     * This method is used to generate report based on the reportType.
     *
     * @param fileName         the file name
     * @param institutionName  the institution name
     * @param reportType       the report type
     * @param transmissionType the transmission type
     * @param from             the from
     * @param to               the to
     * @return the string
     */
    public String generateReport(String fileName, String institutionName, String reportType, String transmissionType, Date from, Date to) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<ReportEntity> reportEntityList;
        reportEntityList = getReportEntities(fileName, institutionName, reportType, from, to);

        if(CollectionUtils.isNotEmpty(reportEntityList)) {
            String actualFileName = fileName;
            if(reportType.equalsIgnoreCase(RecapCommonConstants.ACCESSION_SUMMARY_REPORT) || reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_SUMMARY)){
                actualFileName = fileName+"-"+institutionName;
            } else if (reportType.equalsIgnoreCase(RecapConstants.ONGOING_ACCESSION_REPORT)){
                actualFileName = RecapConstants.ONGOING_ACCESSION_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)){
                actualFileName = RecapCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT)){
                actualFileName = RecapCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT)){
                actualFileName = RecapCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT)){
                actualFileName = RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_SUMMARY_REPORT)){
                actualFileName = RecapConstants.SUBMIT_COLLECTION_SUMMARY_REPORT+"-"+institutionName;
            }

            stopWatch.stop();
            logger.info("Total Time taken to fetch Report Entities From DB : {} " , stopWatch.getTotalTimeSeconds());
            logger.info("Total Num of Report Entities Fetched From DB : {} " , reportEntityList.size());

            for (Iterator<ReportGeneratorInterface> iterator = getReportGenerators().iterator(); iterator.hasNext(); ) {
                ReportGeneratorInterface reportGeneratorInterface = iterator.next();
                if(reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)){
                    String generatedFileName = reportGeneratorInterface.generateReport(actualFileName, reportEntityList);
                    logger.info("The Generated File Name is : {}" , generatedFileName);
                    return generatedFileName;
                }
            }
        }

        return null;
    }

    private List<ReportEntity> getReportEntities(String fileName, String institutionName, String reportType, Date from, Date to) {
        List<ReportEntity> reportEntityList;
        if(!institutionName.equalsIgnoreCase(RecapCommonConstants.LCCN_CRITERIA) && (reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)
                || reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT)
                || reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT)
                || reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT))){
            fileName = getFileNameLike(fileName);
            reportEntityList = reportDetailRepository.findByFileLikeAndInstitutionAndTypeAndDateRange(fileName,institutionName,reportType,from,to);
        }else if(institutionName.equalsIgnoreCase(RecapCommonConstants.LCCN_CRITERIA) && (reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)
                || reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT)
                || reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT)
                || reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT))){
            fileName = getFileNameLike(fileName);
            reportEntityList = reportDetailRepository.findByFileLikeAndTypeAndDateRange(fileName,reportType,from,to);
        } else if(reportType.equalsIgnoreCase(RecapCommonConstants.SUBMIT_COLLECTION_SUMMARY)){
            reportEntityList = reportDetailRepository.findByFileName(fileName);
        } else if(institutionName.equalsIgnoreCase(RecapCommonConstants.LCCN_CRITERIA)) {
            reportEntityList = reportDetailRepository.findByFileLikeAndTypeAndDateRange(fileName, reportType, from, to);
        } else {
            reportEntityList = reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(fileName, institutionName, reportType, from, to);
        }
        return reportEntityList;
    }


    /**
     * Gets report generators.
     *
     * @return the report generators
     */
    public List<ReportGeneratorInterface> getReportGenerators() {
        if(CollectionUtils.isEmpty(reportGenerators)) {
            reportGenerators = new ArrayList<>();
            reportGenerators.add(csvSolrExceptionReportGenerator);
            reportGenerators.add(s3SolrExceptionReportGenerator);
            reportGenerators.add(fsDeAccessionReportGenerator);
            reportGenerators.add(s3DeAccessionReportGenerator);
            reportGenerators.add(fsAccessionReportGenerator);
            reportGenerators.add(s3AccessionReportGenerator);
            reportGenerators.add(fsSubmitCollectionRejectionReportGenerator);
            reportGenerators.add(s3SubmitCollectionRejectionReportGenerator);
            reportGenerators.add(fsSubmitCollectionExceptionReportGenerator);
            reportGenerators.add(s3SubmitCollectionExceptionReportGenerator);
            reportGenerators.add(s3SubmitCollectionSummaryReportGenerator);
            reportGenerators.add(fsSubmitCollectionSummaryReportGenerator);
            reportGenerators.add(fsOngoingAccessionReportGenerator);
            reportGenerators.add(s3OngoingAccessionReportGenerator);
            reportGenerators.add(s3SubmitCollectionReportGenerator);
            reportGenerators.add(s3SubmitCollectionSuccessReportGenerator);
            reportGenerators.add(fsSubmitCollectionSuccessReportGenerator);
            reportGenerators.add(s3SubmitCollectionFailureReportGenerator);
            reportGenerators.add(fsSubmitCollectionFailureReportGenerator);
        }
        return reportGenerators;
    }

    /**
     * Generate submit collection  report to the FTP.
     *
     * @param reportRecordNumberList the report record number list
     * @param reportType             the report type
     * @param transmissionType       the transmission type
     * @return the string
     */
    public String generateReportBasedOnReportRecordNum(List<Integer> reportRecordNumberList,String reportType,String transmissionType) {
        String response = null;
        List<ReportGeneratorInterface> reportGeneratorInterfaces = getReportGenerators();
        for (ReportGeneratorInterface reportGeneratorInterface : reportGeneratorInterfaces) {
            if(reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)){
                 response = reportGeneratorInterface.generateReport(RecapConstants.SUBMIT_COLLECTION, getReportEntityList(reportRecordNumberList));
            }
        }
        return response;
    }

    private List<ReportEntity> getReportEntityList(List<Integer> reportRecordNumberList) {
        return reportDetailRepository.findByIdIn(reportRecordNumberList);
    }

    private String getFileNameLike(String fileName) {
        return fileName+"%";
    }
}
