package org.recap.report;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by akulak on 30/5/17.
 */
public class S3SubmitCollectionReportGeneratorUT extends BaseTestCaseUT {

    @InjectMocks
    S3SubmitCollectionReportGenerator s3SubmitCollectionReportGenerator;

    @InjectMocks
    S3SubmitCollectionSummaryReportGenerator s3SubmitCollectionSummaryReportGenerator;

    @InjectMocks
    S3SubmitCollectionFailureReportGenerator S3SubmitCollectionFailureReportGenerator;

    @InjectMocks
    S3SubmitCollectionSuccessReportGenerator S3SubmitCollectionSuccessReportGenerator;

    @InjectMocks
    FSSubmitCollectionFailureReportGenerator FSSubmitCollectionFailureReportGenerator;

    @InjectMocks
    FSSubmitCollectionSuccessReportGenerator FSSubmitCollectionSuccessReportGenerator;

    @InjectMocks
    FSSubmitCollectionSummaryReportGenerator FSSubmitCollectionSummaryReportGenerator;


    @InjectMocks
    S3SolrExceptionReportGenerator S3SolrExceptionReportGenerator;


    @InjectMocks
    CSVSolrExceptionReportGenerator CSVSolrExceptionReportGenerator;

    @Mock
    private ReportDetailRepository reportDetailRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    CamelContext camelContext;

    @InjectMocks
    ReportGenerator reportGenerator;


    @Test
    public void testFTPSubmitCollectionFailureReportGenerator() throws Exception {
        List<ReportEntity> reportEntityList =saveSubmitCollectionExceptionReport(RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT);
        Mockito.when(reportDetailRepository.findByFileLikeAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface= S3SubmitCollectionFailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.FTP_SUBMIT_COLLECTION_FAILURE_REPORT_Q, RecapCommonConstants.LCCN_CRITERIA, RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT, RecapCommonConstants.FTP,getFromDate(new Date()),getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }



    @Test
    public void testFSSubmitCollectionFailureReportGenerator() throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        reportEntityList.add(reportEntity);
        String generatedReportFileName = FSSubmitCollectionFailureReportGenerator.generateReport(RecapConstants.FS_SUBMIT_COLLECTION_FAILURE_REPORT_Q,reportEntityList);
        assertTrue(FSSubmitCollectionFailureReportGenerator.isTransmitted(RecapCommonConstants.FILE_SYSTEM));
        assertTrue(FSSubmitCollectionFailureReportGenerator.isInterested(RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT));
    }


  @Test
    public void testFTPSolrExceptionReportGenerator() throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        reportEntityList.add(reportEntity);
        String generatedReportFileName = S3SolrExceptionReportGenerator.generateReport(RecapCommonConstants.FTP_SOLR_EXCEPTION_REPORT_Q,reportEntityList);
        assertTrue(S3SolrExceptionReportGenerator.isTransmitted(RecapCommonConstants.FTP));
        assertTrue(S3SolrExceptionReportGenerator.isInterested(RecapCommonConstants.SOLR_INDEX_EXCEPTION));
    }

    @Test
    public void testCSVSolrExceptionReportGenerator() throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        reportEntityList.add(reportEntity);
        String generatedReportFileName = CSVSolrExceptionReportGenerator.generateReport(RecapCommonConstants.CSV_SOLR_EXCEPTION_REPORT_Q,reportEntityList);
        assertTrue(CSVSolrExceptionReportGenerator.isTransmitted(RecapCommonConstants.FILE_SYSTEM));
        assertTrue(CSVSolrExceptionReportGenerator.isInterested(RecapCommonConstants.SOLR_INDEX_EXCEPTION));
    }

    @Test
    public void FSSubmitCollectionSummaryReportGenerator() throws Exception {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        reportEntityList.add(reportEntity);
        String generatedReportFileName = FSSubmitCollectionSummaryReportGenerator.generateReport(RecapConstants.FS_SUBMIT_COLLECTION_SUMMARY_REPORT_Q,reportEntityList);
        assertTrue(FSSubmitCollectionSummaryReportGenerator.isTransmitted(RecapCommonConstants.FILE_SYSTEM));
        assertTrue(FSSubmitCollectionSummaryReportGenerator.isInterested(RecapCommonConstants.SUBMIT_COLLECTION_SUMMARY));
    }

    @Test
    public void testGenerateReport() throws Exception{
        camelContext.getEndpoint(RecapConstants.FTP_SUBMIT_COLLECTION_REPORT_Q, MockEndpoint.class);
        boolean isInterested = s3SubmitCollectionReportGenerator.isInterested(RecapConstants.SUBMIT_COLLECTION);
        assertTrue(isInterested);
        boolean isTransmitted = s3SubmitCollectionReportGenerator.isTransmitted(RecapCommonConstants.FTP);
        assertTrue(isTransmitted);
        String response = s3SubmitCollectionReportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION,saveSubmitCollectionExceptionReport(RecapCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT));
        String errorResponse = s3SubmitCollectionReportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION,null);
        assertNotNull(response);
        assertEquals(RecapCommonConstants.SUCCESS,response);
        assertEquals(RecapConstants.ERROR,errorResponse);
    }

    @Test
    public void testSubmitCollectionSummaryReportGenerator() throws Exception{
        camelContext.getEndpoint(RecapConstants.FTP_SUBMIT_COLLECTION_REPORT_Q, MockEndpoint.class);
        boolean isInterested = s3SubmitCollectionSummaryReportGenerator.isInterested(RecapCommonConstants.SUBMIT_COLLECTION_SUMMARY);
        assertTrue(isInterested);
        boolean isTransmitted = s3SubmitCollectionSummaryReportGenerator.isTransmitted(RecapCommonConstants.FTP);
        assertTrue(isTransmitted);
        String response = s3SubmitCollectionSummaryReportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION,saveSubmitCollectionExceptionReport(RecapCommonConstants.SUBMIT_COLLECTION_SUMMARY));
        assertNotNull(response);
    }

    private List<ReportEntity> saveSubmitCollectionExceptionReport(String reportType){
        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapCommonConstants.SUBMIT_COLLECTION_REPORT);
        reportEntity.setType(reportType);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity itemBarcodeReportDataEntity = new ReportDataEntity();
        itemBarcodeReportDataEntity.setHeaderName(RecapCommonConstants.SUBMIT_COLLECTION_ITEM_BARCODE);
        itemBarcodeReportDataEntity.setHeaderValue("123");
        reportDataEntities.add(itemBarcodeReportDataEntity);

        ReportDataEntity customerCodeReportDataEntity = new ReportDataEntity();
        customerCodeReportDataEntity.setHeaderName(RecapCommonConstants.SUBMIT_COLLECTION_CUSTOMER_CODE);
        customerCodeReportDataEntity.setHeaderValue("PB");
        reportDataEntities.add(customerCodeReportDataEntity);

        ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();
        owningInstitutionReportDataEntity.setHeaderName(RecapCommonConstants.OWNING_INSTITUTION);
        owningInstitutionReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(owningInstitutionReportDataEntity);

        ReportDataEntity message = new ReportDataEntity();
        message.setHeaderName(RecapCommonConstants.MESSAGE);
        message.setHeaderValue("1");
        reportDataEntities.add(message);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        return reportEntityList;
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}
