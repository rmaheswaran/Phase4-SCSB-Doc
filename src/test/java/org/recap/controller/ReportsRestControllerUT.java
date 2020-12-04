package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.model.reports.ReportDataRequest;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.util.ReportsServiceUtil;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
public class ReportsRestControllerUT extends BaseControllerUT {

    @InjectMocks
    ReportsRestController mockReportsRestController;

    @Mock
    ReportsServiceUtil reportsServiceUtil;

    @Test
    public void accessionDeaccessionCounts() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate("09/27/2016");
        reportsRequest.setAccessionDeaccessionToDate("01/27/2017");
        reportsRequest.setOwningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"));
        reportsRequest.setCollectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"));
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/accessionDeaccessionCounts")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportsRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void cgdItemCounts() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setOwningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"));
        reportsRequest.setCollectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"));
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/cgdItemCounts")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportsRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void deaccessionResults() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate("09/27/2016");
        reportsRequest.setAccessionDeaccessionToDate("01/27/2017");
        reportsRequest.setDeaccessionOwningInstitution("PUL");
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/deaccessionResults")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportsRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void testIncompleteRecords() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate("09/27/2016");
        reportsRequest.setAccessionDeaccessionToDate("01/27/2017");
        reportsRequest.setDeaccessionOwningInstitution("PUL");
        reportsRequest.setExport(true);
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/incompleteRecords")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportsRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void testGenerateCsvReports() throws Exception {
        ReportDataRequest reportDataRequest = new ReportDataRequest();
        reportDataRequest.setFileName("Accession");
        reportDataRequest.setTransmissionType("FTP");
        reportDataRequest.setReportType("Summary");
        reportDataRequest.setInstitutionCode("PUL");
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/generateCsvReport")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportDataRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void deaccessionResultsException() throws Exception {
        ReportsRequest reportsRequest=new ReportsRequest();
        Mockito.when(reportsServiceUtil.populateDeaccessionResults(Mockito.any())).thenReturn(new ReportsResponse());
        ReportsResponse incompleteRecords=mockReportsRestController.deaccessionResults(reportsRequest);
        assertNotNull(incompleteRecords);
    }

    @Test
    public void accessionDeaccessionCountsException() throws Exception {
        ReportsRequest reportsRequest=new ReportsRequest();
        Mockito.when(reportsServiceUtil.populateAccessionDeaccessionItemCounts(Mockito.any())).thenThrow(NullPointerException.class);
        ReportsResponse incompleteRecords=mockReportsRestController.accessionDeaccessionCounts(reportsRequest);
        assertNotNull(incompleteRecords);
    }

    @Test
    public void cgdItemCountsException() throws Exception {
        ReportsRequest reportsRequest=new ReportsRequest();
        Mockito.when(reportsServiceUtil.populateCgdItemCounts(Mockito.any())).thenThrow(NullPointerException.class);
        ReportsResponse incompleteRecords=mockReportsRestController.cgdItemCounts(reportsRequest);
        assertNotNull(incompleteRecords);
    }

    @Test
    public void testIncompleteRecordsException() throws Exception {
        ReportsRequest reportsRequest=new ReportsRequest();
        Mockito.when(reportsServiceUtil.populateIncompleteRecordsReport(Mockito.any())).thenThrow(NullPointerException.class);
        ReportsResponse incompleteRecords=mockReportsRestController.incompleteRecords(reportsRequest);
        assertNotNull(incompleteRecords);
    }
}
