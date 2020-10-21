package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.JobParamDataEntity;
import org.recap.model.jpa.JobParamEntity;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.JobParamDetailRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anitha on 10/10/20.
 */

public class GenerateReportRestControllerUT extends BaseControllerUT {

    @InjectMocks
    GenerateReportRestController generateReportRestController;

    @Mock
    JobParamDetailRepository jobParamDetailRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    ReportGenerator reportGenerator;


    @Test
    public void generateReportsJob() throws Exception{
        JobParamEntity jobParamEntity=new JobParamEntity() ;
        List<JobParamDataEntity> jobParamDataEntities=new ArrayList<>();
        jobParamDataEntities.add(getJobParamDataEntity(RecapConstants.TRANSMISSION_TYPE));
        jobParamDataEntities.add(getJobParamDataEntity(RecapConstants.REPORT_TYPE));
        jobParamDataEntities.add(getJobParamDataEntity(RecapConstants.JOB_PARAM_DATA_FILE_NAME));
        jobParamEntity.setJobParamDataEntities(jobParamDataEntities);
        Mockito.when(jobParamDetailRepository.findByJobName(Mockito.anyString())).thenReturn(jobParamEntity);
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(getInstitutionEntity("HTC",4));
        institutionEntities.add(getInstitutionEntity("CUL",2));
        Mockito.when(institutionDetailsRepository.findByInstitutionCodeNotIn(Mockito.anyList())).thenReturn(institutionEntities);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setProcessType("test");
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(RecapConstants.SUBMIT_COLLECTION_SUMMARY_REPORT);
        String reponse =generateReportRestController.generateReportsJob(solrIndexRequest);
        assertEquals("Report generated Successfully in FTP",reponse);
    }

    @Test
    public void generateReportsJobFail() throws Exception{
        JobParamEntity jobParamEntity=new JobParamEntity() ;
        List<JobParamDataEntity> jobParamDataEntities=new ArrayList<>();
        jobParamDataEntities.add(getJobParamDataEntity(RecapConstants.TRANSMISSION_TYPE));
        jobParamDataEntities.add(getJobParamDataEntity(RecapConstants.REPORT_TYPE));
        jobParamDataEntities.add(getJobParamDataEntity(RecapConstants.JOB_PARAM_DATA_FILE_NAME));
        jobParamEntity.setJobParamDataEntities(jobParamDataEntities);
        Mockito.when(jobParamDetailRepository.findByJobName(Mockito.anyString())).thenReturn(jobParamEntity);
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(getInstitutionEntity("HTC",4));
        institutionEntities.add(getInstitutionEntity("CUL",2));
        Mockito.when(institutionDetailsRepository.findByInstitutionCodeNotIn(Mockito.anyList())).thenReturn(institutionEntities);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setProcessType("test");
        Mockito.when(reportGenerator.generateReport(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn("");
        String reponse =generateReportRestController.generateReportsJob(solrIndexRequest);
        assertEquals("There is no report to generate or Report Generation Failed",reponse);
    }

    @Test
    public void generateSubmitCollectionReport() throws Exception{
        List<Integer> reportRecordNumberList= Arrays.asList(1,2,3);
        Mockito.when(reportGenerator.generateReportBasedOnReportRecordNum(Mockito.anyList(),Mockito.anyString(),Mockito.anyString())).thenReturn("test");
        String reponse =generateReportRestController.generateSubmitCollectionReport(reportRecordNumberList);
        assertEquals("test",reponse);
    }

    private InstitutionEntity getInstitutionEntity(String name, int id) {
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionName("HTC");
        institutionEntity.setInstitutionCode(String.valueOf(id));
        institutionEntity.setId(id);
        return institutionEntity;
    }

    private JobParamDataEntity getJobParamDataEntity(String name) {
        JobParamDataEntity jobParamDataEntity=new JobParamDataEntity();
        jobParamDataEntity.setParamName(name);
        jobParamDataEntity.setParamValue("1");
        return jobParamDataEntity;
    }

}
