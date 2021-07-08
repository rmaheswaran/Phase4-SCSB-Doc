package org.recap.matchingalgorithm.report;

import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.submitCollection.SubmitCollectionReport;
import org.recap.report.FSAccessionReportGenerator;
import org.recap.report.FSSubmitCollectionFailureReportGenerator;
import org.recap.report.FSSubmitCollectionSuccessReportGenerator;
import org.recap.report.FSSubmitCollectionSummaryReportGenerator;
import org.recap.report.ReportGenerator;
import org.recap.report.ReportGeneratorInterface;
import org.recap.report.S3AccessionReportGenerator;
import org.recap.report.S3SubmitCollectionExceptionReportGenerator;
import org.recap.report.S3SubmitCollectionRejectionReportGenerator;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by angelind on 23/8/16.
 */
public class ReportGeneratorUT extends BaseTestCaseUT {

    @InjectMocks
    ReportGenerator reportGenerator;

    @InjectMocks
    FSAccessionReportGenerator fsAccessionReportGenerator;

    @InjectMocks
    S3AccessionReportGenerator s3AccessionReportGenerator;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Value("${" + PropertyKeyConstants.SCSB_COLLECTION_REPORT_DIRECTORY + "}")
    String reportsDirectory;

    @Mock
    DateUtil dateUtil;

    @Mock
    ReportEntity reportEntity;

    @Mock
    ReportDataEntity reportDataEntity;

    @Mock
    SubmitCollectionReport submitCollectionReprot;

    @Mock
    S3SubmitCollectionExceptionReportGenerator s3SubmitCollectionExceptionReportGenerator;

    @Mock
    S3SubmitCollectionRejectionReportGenerator S3SubmitCollectionRejectionReportGenerator;

    @Mock
    FSSubmitCollectionSuccessReportGenerator FSSubmitCollectionSuccessReportGenerator;

    @Mock
    FSSubmitCollectionFailureReportGenerator FSSubmitCollectionFailureReportGenerator;

    @Mock
    FSSubmitCollectionSummaryReportGenerator FSSubmitCollectionSummaryReportGenerator;

    @Test
    public void testSubmitCollectionExceptionReportLCCN() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=s3SubmitCollectionExceptionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(s3SubmitCollectionExceptionReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(s3SubmitCollectionExceptionReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(s3SubmitCollectionExceptionReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionExceptionReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.LCCN_CRITERIA,ScsbCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT,ScsbCommonConstants.FTP,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionExceptionReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionExceptionReport() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=s3SubmitCollectionExceptionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(s3SubmitCollectionExceptionReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(s3SubmitCollectionExceptionReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(s3SubmitCollectionExceptionReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionExceptionReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT,ScsbCommonConstants.FTP,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionExceptionReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionRejectionReportLCCN() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=S3SubmitCollectionRejectionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(S3SubmitCollectionRejectionReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(S3SubmitCollectionRejectionReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(S3SubmitCollectionRejectionReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionRejectionReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.LCCN_CRITERIA,ScsbCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT,ScsbCommonConstants.FTP,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionRejectionReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionRejectionReport() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=S3SubmitCollectionRejectionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(S3SubmitCollectionRejectionReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(S3SubmitCollectionRejectionReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(S3SubmitCollectionRejectionReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionRejectionReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT,ScsbCommonConstants.FTP,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionRejectionReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionSuccessReportLCCN() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=FSSubmitCollectionSuccessReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(FSSubmitCollectionSuccessReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSuccessReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSuccessReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionSuccessReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.LCCN_CRITERIA,ScsbCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT,ScsbCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionSuccessReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionSuccessReport() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=FSSubmitCollectionSuccessReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(FSSubmitCollectionSuccessReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSuccessReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSuccessReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionSuccessReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT,ScsbCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionSuccessReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionFailureReportLCCN() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=FSSubmitCollectionFailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(FSSubmitCollectionFailureReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionFailureReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionFailureReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionFailureReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.LCCN_CRITERIA,ScsbCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT,ScsbCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionFailureReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionFailureReport() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileLikeAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=FSSubmitCollectionFailureReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(FSSubmitCollectionFailureReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionFailureReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionFailureReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionFailureReport.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.PRINCETON,ScsbCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT,ScsbCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionFailureReport.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionSummary() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileName(Mockito.anyString())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=FSSubmitCollectionSummaryReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(FSSubmitCollectionSummaryReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSummaryReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSummaryReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionSummary.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.LCCN_CRITERIA, ScsbConstants.SUBMIT_COLLECTION_SUMMARY_REPORT,ScsbCommonConstants.FILE_SYSTEM,null,null);
        assertEquals("SubmitCollectionSummary.csv",generatedReportFileName);
    }

    @Test
    public void testSubmitCollectionSummaryDateRange() {
        List<ReportEntity> reportEntityList=new ArrayList<>();
        reportEntityList.add(reportEntity);
        Mockito.when(reportDetailRepository.findByFileNameLikeAndInstitutionAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=FSSubmitCollectionSummaryReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        Mockito.when(FSSubmitCollectionSummaryReportGenerator.isInterested(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSummaryReportGenerator.isTransmitted(Mockito.anyString())).thenReturn(true);
        Mockito.when(FSSubmitCollectionSummaryReportGenerator.generateReport(Mockito.anyString(),Mockito.anyList())).thenReturn("SubmitCollectionSummary.csv");
        String generatedReportFileName =  reportGenerator.generateReport("",ScsbCommonConstants.PRINCETON, ScsbConstants.SUBMIT_COLLECTION_SUMMARY_REPORT,ScsbCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertEquals("SubmitCollectionSummary.csv",generatedReportFileName);
    }

    @Test
    public void testMatchingReportForFileSystem() {
        List<ReportEntity> reportEntityList =saveAccessionSummaryReportEntity();
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=fsAccessionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = reportGenerator.generateReport(ScsbCommonConstants.ACCESSION_REPORT, ScsbCommonConstants.PRINCETON, ScsbCommonConstants.ACCESSION_SUMMARY_REPORT, ScsbCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testMatchingReportForFileSystemLCCN() {
        List<ReportEntity> reportEntityList =saveAccessionSummaryReportEntity();
        Mockito.when(reportDetailRepository.findByFileLikeAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=fsAccessionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = reportGenerator.generateReport(ScsbCommonConstants.ACCESSION_REPORT, ScsbCommonConstants.LCCN_CRITERIA, ScsbCommonConstants.ACCESSION_SUMMARY_REPORT, ScsbCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testMatchingReportForFTP() {
        List<ReportEntity> reportEntityList =saveAccessionSummaryReportEntity();
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface= s3AccessionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = reportGenerator.generateReport(ScsbCommonConstants.ACCESSION_REPORT, ScsbCommonConstants.PRINCETON, ScsbCommonConstants.ACCESSION_SUMMARY_REPORT, ScsbCommonConstants.FTP,getFromDate(new Date()),getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void getReportGenerators() {
        List<ReportGeneratorInterface> getReportGenerators=reportGenerator.getReportGenerators();
        assertNotNull(getReportGenerators);
    }

    @Test
    public void submitCollectionExceptionReportExport() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.add(getReportDataEntity("itemBarcode", "123456"));
        reportDataEntities.add(getReportDataEntity(ScsbCommonConstants.CUSTOMER_CODE, "PA"));
        reportDataEntities.add(getReportDataEntity(ScsbCommonConstants.OWNING_INSTITUTION, ScsbCommonConstants.PRINCETON));
        reportDataEntities.add(getReportDataEntity(ScsbCommonConstants.MESSAGE, "testsubmit"));
        Mockito.when(reportEntity.getReportDataEntities()).thenReturn(reportDataEntities);
        Mockito.when(reportEntity.getType()).thenReturn(ScsbCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT);
        Mockito.when(submitCollectionReprot.getFrom()).thenReturn(new Date());
        Mockito.when(submitCollectionReprot.getTo()).thenReturn(new Date());
        Mockito.when(submitCollectionReprot.getInstitutionName()).thenReturn(ScsbCommonConstants.PRINCETON);
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(reportEntity);
        Mockito.when(reportDetailRepository.findByInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntities);
        SubmitCollectionReport submitCollectionExceptionReportExport= reportGenerator.submitCollectionExceptionReportExport(submitCollectionReprot);
        assertNotNull(submitCollectionExceptionReportExport);
    }

    @Test
    public void submitCollectionExceptionReportGenerator() {
        Page<ReportEntity> reportEntityList=Mockito.mock(Page.class);
        List<ReportEntity> reportEntities=new ArrayList<>();
        reportEntities.add(reportEntity);
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportDataEntities.add(getReportDataEntity(ScsbCommonConstants.BARCODE, "123456"));
        reportDataEntities.add(getReportDataEntity(ScsbCommonConstants.CUSTOMER_CODE, "PA"));
        reportDataEntities.add(getReportDataEntity(ScsbCommonConstants.OWNING_INSTITUTION, ScsbCommonConstants.PRINCETON));
        reportDataEntities.add(getReportDataEntity(ScsbCommonConstants.MESSAGE, "testsubmit"));
        Mockito.when(reportEntity.getReportDataEntities()).thenReturn(reportDataEntities);
        Mockito.when(reportEntity.getType()).thenReturn(ScsbCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT);
        Mockito.when(reportEntityList.getContent()).thenReturn(reportEntities);
        Mockito.when(reportEntityList.getTotalPages()).thenReturn(1);
        Mockito.when(reportEntityList.getTotalElements()).thenReturn(1l);
        Mockito.when(submitCollectionReprot.getInstitutionName()).thenReturn(ScsbCommonConstants.PRINCETON);
        Mockito.when(submitCollectionReprot.getPageNumber()).thenReturn(1);
        Mockito.when(submitCollectionReprot.getPageSize()).thenReturn(1);
        Mockito.when(reportDetailRepository.findByInstitutionAndTypeandDateRange(Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        SubmitCollectionReport submitCollectionReport=  reportGenerator.submitCollectionExceptionReportGenerator(submitCollectionReprot);
        assertNotNull(submitCollectionReport);
    }

    private ReportDataEntity getReportDataEntity(String barcode, String s) {
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(barcode);
        reportDataEntity.setHeaderValue(s);
        return reportDataEntity;
    }

    @Test
    public void testReportDataEntity(){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setId(1);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");
        reportEntity.setFileName("Accession");
        reportEntity.setType("Accession");
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setId(1);
        reportDataEntity.setRecordNum("10");
        reportDataEntity.setHeaderName("ItemBarcode");
        reportDataEntity.setHeaderValue("3328456458454714");
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        assertNotNull(reportEntity.getId());
        assertNotNull(reportEntity.getFileName());
        assertNotNull(reportEntity.getReportDataEntities());
        assertNotNull(reportEntity.getType());
        assertNotNull(reportEntity.getCreatedDate());
        assertNotNull(reportEntity.getInstitutionName());
        assertNotNull(reportDataEntity.getRecordNum());
        assertNotNull(reportDataEntity.getId());
        assertNotNull(reportDataEntity.getHeaderName());
        assertNotNull(reportDataEntity.getHeaderValue());
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

    private List<ReportEntity> saveAccessionSummaryReportEntity(){
        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(ScsbCommonConstants.ACCESSION_REPORT);
        reportEntity.setType(ScsbCommonConstants.ACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity successBibCountReportDataEntity = getReportDataEntity(ScsbCommonConstants.BIB_SUCCESS_COUNT, String.valueOf(1));
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity successItemCountReportDataEntity = getReportDataEntity(ScsbCommonConstants.ITEM_SUCCESS_COUNT, String.valueOf(1));
        reportDataEntities.add(successItemCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = getReportDataEntity(ScsbCommonConstants.BIB_FAILURE_COUNT, String.valueOf(0));
        reportDataEntities.add(failedBibCountReportDataEntity);

        ReportDataEntity failedItemCountReportDataEntity = getReportDataEntity(ScsbCommonConstants.ITEM_FAILURE_COUNT, String.valueOf(0));
        reportDataEntities.add(failedItemCountReportDataEntity);

        ReportDataEntity reasonForBibFailureReportDataEntity = getReportDataEntity(ScsbConstants.FAILURE_BIB_REASON, "");
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity reasonForItemFailureReportDataEntity = getReportDataEntity(ScsbConstants.FAILURE_ITEM_REASON, "");
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        return reportEntityList;

    }

}