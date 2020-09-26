package org.recap.matchingalgorithm.service;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.RouteController;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.CsvUtil;
import org.recap.util.DateUtil;
import org.recap.util.SolrQueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anitha on 10/10/20.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SolrTemplate.class, SolrClient.class})
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
public class OngoingMatchingReportsServiceUT extends BaseTestCaseUT{

    @InjectMocks
    OngoingMatchingReportsService ongoingMatchingReportsService;

    @Mock
    OngoingMatchingReportsService mockOngoingMatchingReportsService;

    @Mock
    SolrQueryBuilder solrQueryBuilder;

    @Mock
    CamelContext camelContext;

    @Mock
    RouteController routeController;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    DateUtil dateUtil;

    @Mock
    CsvUtil csvUtil;

    @Value("${ongoing.matching.report.directory}")
    String matchingReportsDirectory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(ongoingMatchingReportsService,"matchingReportsDirectory",matchingReportsDirectory);
    }

    @Test
    public void generateTitleExceptionReport() throws Exception {
        List<ReportEntity> reportEntities=new ArrayList<>();
        List<ReportDataEntity> reportDataEntities=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntity.setHeaderName("Title");
        reportDataEntity.setHeaderValue("test");
        reportDataEntities.add(reportDataEntity);
        ReportEntity reportEntity=new ReportEntity();
        reportEntity.addAll(reportDataEntities);
        reportEntities.add(reportEntity);
        File file=new File("Test");
        Page<ReportEntity> page=Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(reportEntities);
        Mockito.when(page.getTotalPages()).thenReturn(2);
        Mockito.when(mockOngoingMatchingReportsService.getCamelContext()).thenReturn(camelContext);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.doNothing().when(routeController).startRoute(Mockito.anyString());
        Mockito.when(mockOngoingMatchingReportsService.getReportDetailRepository()).thenReturn(reportDetailRepository);
        Mockito.when(mockOngoingMatchingReportsService.getDateUtil()).thenReturn(dateUtil);
        Mockito.when(mockOngoingMatchingReportsService.getCsvUtil()).thenReturn(csvUtil);
        Mockito.when(mockOngoingMatchingReportsService.getMatchingReportsDirectory()).thenReturn(matchingReportsDirectory);
        Mockito.when(reportDetailRepository.findByFileAndTypeAndDateRangeWithPaging(Mockito.any(),Mockito.anyString(),Mockito.anyString(),Mockito.any(),Mockito.any())).thenReturn(page);
        Mockito.when(csvUtil.createTitleExceptionReportFile(Mockito.anyString(),Mockito.anyInt(),Mockito.anyList())).thenReturn(file);
        String file1=ongoingMatchingReportsService.generateTitleExceptionReport(new Date(), 1);
        assertEquals(String.valueOf(file),file1);
    }

        @Test
        public void generateSerialAndMVMsReportException() throws IOException, SolrServerException {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        QueryResponse queryResponse= Mockito.mock(QueryResponse.class);
        ReflectionTestUtils.setField(ongoingMatchingReportsService,"solrTemplate",mocksolrTemplate1);
        SolrQuery solrQuery = new SolrQuery("testquery");
        solrQuery.setStart(1);
        solrQuery.setRows(1);
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("",String.valueOf(1));
        solrDocument.setField(RecapConstants.MATERIAL_TYPE, RecapCommonConstants.MONOGRAPH);
        solrDocumentList.add(solrDocument);
        Mockito.when(mockOngoingMatchingReportsService.getSolrTemplate()).thenReturn(mocksolrTemplate1);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        List<Integer> serialMvmBibIds= Arrays.asList(1);
       ongoingMatchingReportsService.generateSerialAndMVMsReport(serialMvmBibIds);
    }

    @Test
    public void generateSerialAndMVMsReport() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        QueryResponse queryResponse= Mockito.mock(QueryResponse.class);
        ReflectionTestUtils.setField(ongoingMatchingReportsService,"solrTemplate",mocksolrTemplate1);
        ReflectionTestUtils.setField(ongoingMatchingReportsService,"solrQueryBuilder",solrQueryBuilder);
        SolrQuery solrQuery = new SolrQuery("testquery");
        solrQuery.setStart(1);
        solrQuery.setRows(1);
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField(RecapCommonConstants.DOCTYPE,RecapCommonConstants.ITEM);
        solrDocument.setField(RecapCommonConstants.IS_DELETED_ITEM,false);
        solrDocument.setField(RecapConstants.ITEM_CATALOGING_STATUS,RecapCommonConstants.COMPLETE_STATUS);
        solrDocument.setField(RecapCommonConstants.HOLDINGS_ID,Arrays.asList(1));
        solrDocumentList.add(solrDocument);
        solrDocumentList.setNumFound(11);
        Mockito.when(mockOngoingMatchingReportsService.getSolrTemplate()).thenReturn(mocksolrTemplate1);
        Mockito.when(mockOngoingMatchingReportsService.getSolrQueryBuilder()).thenReturn(solrQueryBuilder);
        Mockito.when(mockOngoingMatchingReportsService.getCamelContext()).thenReturn(camelContext);
        Mockito.when(mockOngoingMatchingReportsService.getProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        Mockito.doNothing().when(routeController).startRoute(Mockito.anyString());
        Mockito.when(solrQueryBuilder.getSolrQueryForBibItem(Mockito.anyString())).thenReturn(solrQuery);
        Mockito.doNothing().when(producerTemplate).sendBodyAndHeader(String.valueOf(Mockito.anyInt()),Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        List<Integer> serialMvmBibIds= Arrays.asList(1);
        ongoingMatchingReportsService.generateSerialAndMVMsReport(serialMvmBibIds);
    }


    @Test
    public void generateSerialAndMVMsReportForHolding() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        QueryResponse queryResponse= Mockito.mock(QueryResponse.class);
        ReflectionTestUtils.setField(ongoingMatchingReportsService,"solrTemplate",mocksolrTemplate1);
        ReflectionTestUtils.setField(ongoingMatchingReportsService,"solrQueryBuilder",solrQueryBuilder);
        SolrQuery solrQuery = new SolrQuery("testquery");
        solrQuery.setStart(1);
        solrQuery.setRows(1);
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField(RecapCommonConstants.DOCTYPE,RecapCommonConstants.HOLDINGS);
        solrDocument.setField(RecapCommonConstants.HOLDING_ID,345);
        solrDocument.setField(RecapConstants.SUMMARY_HOLDINGS,"45");
        solrDocumentList.add(solrDocument);
        solrDocumentList.setNumFound(11);
        Mockito.when(mockOngoingMatchingReportsService.getSolrTemplate()).thenReturn(mocksolrTemplate1);
        Mockito.when(mockOngoingMatchingReportsService.getSolrQueryBuilder()).thenReturn(solrQueryBuilder);
        Mockito.when(solrQueryBuilder.getSolrQueryForBibItem(Mockito.anyString())).thenReturn(solrQuery);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        List<Integer> serialMvmBibIds= Arrays.asList(1);
        ongoingMatchingReportsService.generateSerialAndMVMsReport(serialMvmBibIds);
    }
}
