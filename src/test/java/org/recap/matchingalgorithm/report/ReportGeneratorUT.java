package org.recap.matchingalgorithm.report;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.report.FSAccessionReportGenerator;
import org.recap.report.FTPAccessionReportGenerator;
import org.recap.report.FTPSubmitCollectionReportGenerator;
import org.recap.report.FTPSubmitCollectionSuccessReportGenerator;
import org.recap.report.ReportGenerator;
import org.recap.report.ReportGeneratorInterface;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 23/8/16.
 */
public class ReportGeneratorUT extends BaseTestCaseUT {

    @InjectMocks
    ReportGenerator reportGenerator;

    @InjectMocks
    FSAccessionReportGenerator fsAccessionReportGenerator;

    @InjectMocks
    FTPAccessionReportGenerator ftpAccessionReportGenerator;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Value("${scsb.collection.report.directory}")
    String reportsDirectory;

    @Mock
    DateUtil dateUtil;



    @Test
    public void testMatchingReportForFileSystem() throws Exception {
        List<ReportEntity> reportEntityList =saveAccessionSummaryReportEntity();
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=fsAccessionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = reportGenerator.generateReport(RecapCommonConstants.ACCESSION_REPORT, RecapCommonConstants.PRINCETON, RecapCommonConstants.ACCESSION_SUMMARY_REPORT, RecapCommonConstants.FILE_SYSTEM,getFromDate(new Date()),getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testMatchingReportForFTP() throws Exception {
        List<ReportEntity> reportEntityList =saveAccessionSummaryReportEntity();
        Mockito.when(reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(reportEntityList);
        List<ReportGeneratorInterface> reportGenerators=new ArrayList<>();
        ReportGeneratorInterface reportGeneratorInterface=ftpAccessionReportGenerator;
        reportGenerators.add(reportGeneratorInterface);
        ReflectionTestUtils.setField(reportGenerator,"reportGenerators",reportGenerators);
        String generatedReportFileName = reportGenerator.generateReport(RecapCommonConstants.ACCESSION_REPORT, RecapCommonConstants.PRINCETON, RecapCommonConstants.ACCESSION_SUMMARY_REPORT, RecapCommonConstants.FTP,getFromDate(new Date()),getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void getReportGenerators() {
        List<ReportGeneratorInterface> getReportGenerators=reportGenerator.getReportGenerators();
     assertNotNull(getReportGenerators);
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
        reportEntity.setFileName(RecapCommonConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapCommonConstants.ACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity successBibCountReportDataEntity = new ReportDataEntity();
        successBibCountReportDataEntity.setHeaderName(RecapCommonConstants.BIB_SUCCESS_COUNT);
        successBibCountReportDataEntity.setHeaderValue(String.valueOf(1));
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity successItemCountReportDataEntity = new ReportDataEntity();
        successItemCountReportDataEntity.setHeaderName(RecapCommonConstants.ITEM_SUCCESS_COUNT);
        successItemCountReportDataEntity.setHeaderValue(String.valueOf(1));
        reportDataEntities.add(successItemCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = new ReportDataEntity();
        failedBibCountReportDataEntity.setHeaderName(RecapCommonConstants.BIB_FAILURE_COUNT);
        failedBibCountReportDataEntity.setHeaderValue(String.valueOf(0));
        reportDataEntities.add(failedBibCountReportDataEntity);

        ReportDataEntity failedItemCountReportDataEntity = new ReportDataEntity();
        failedItemCountReportDataEntity.setHeaderName(RecapCommonConstants.ITEM_FAILURE_COUNT);
        failedItemCountReportDataEntity.setHeaderValue(String.valueOf(0));
        reportDataEntities.add(failedItemCountReportDataEntity);

        ReportDataEntity reasonForBibFailureReportDataEntity = new ReportDataEntity();
        reasonForBibFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_BIB_REASON);
        reasonForBibFailureReportDataEntity.setHeaderValue("");
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity reasonForItemFailureReportDataEntity = new ReportDataEntity();
        reasonForItemFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_ITEM_REASON);
        reasonForItemFailureReportDataEntity.setHeaderValue("");
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        return reportEntityList;

    }

}