package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 19/1/17.
 */
public class GenerateReportControllerUT extends BaseControllerUT{

    @InjectMocks
    GenerateReportController generateReportController;

    @Mock
    ReportGenerator reportGenerator;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Mock
    DateUtil dateUtil;

    @Test
    public void testAccessionSummaryReportForFileSystem() throws Exception{
        List<ReportEntity> reportEntityList = saveSummaryReportEntity();
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(createdDate);
        solrIndexRequest.setReportType(RecapCommonConstants.ACCESSION_SUMMARY_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }
    @Test
    public void testOngoingAccessionSummaryReportForFileSystem() throws Exception{
        List<ReportEntity> reportEntityList = saveSummaryReportEntity();
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(createdDate);
        solrIndexRequest.setReportType(RecapConstants.ONGOING_ACCESSION_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }
    @Test
    public void testDeaccessionSummaryReportForFileSystem() throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setReportType(RecapCommonConstants.DEACCESSION_SUMMARY_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }
    @Test
    public void testSubmitRejectedSummaryReportForFileSystem() throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setReportType(RecapCommonConstants.SUBMIT_COLLECTION_REJECTION_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }
    @Test
    public void testSolrFailureSummaryReportForFileSystem() throws Exception{
        Date toDate = new Date();
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setToDate(toDate);
        solrIndexRequest.setReportType("Failure");
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }

    @Test
    public void testSubmitExceptionSummaryReportForFileSystem() throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setReportType(RecapCommonConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }

    @Test
    public void testSubmitSuccessSummaryReportForFileSystem() throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setReportType(RecapCommonConstants.SUBMIT_COLLECTION_SUCCESS_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }
    @Test
    public void testSubmitFailureSummaryReportForFileSystem() throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setReportType(RecapCommonConstants.SUBMIT_COLLECTION_FAILURE_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("test");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }

    @Test
    public void testSubmitSummaryReportForFileSystem() throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setReportType(RecapConstants.SUBMIT_COLLECTION_SUMMARY_REPORT);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setTransmissionType(RecapCommonConstants.FILE_SYSTEM);
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("");
        String reponse = generateReportController.generateReports(solrIndexRequest,bindingResult,model);
        assertNotNull(reponse);
    }

    private List<ReportEntity> saveSummaryReportEntity(){
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

        ReportDataEntity existsBibCountReportDataEntity = new ReportDataEntity();
        existsBibCountReportDataEntity.setHeaderName(RecapCommonConstants.NUMBER_OF_BIB_MATCHES);
        existsBibCountReportDataEntity.setHeaderValue(String.valueOf(0));
        reportDataEntities.add(existsBibCountReportDataEntity);

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