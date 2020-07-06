package org.recap.util;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.matchingalgorithm.service.OngoingMatchingReportsService;
import org.recap.model.solr.SolrIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by angelind on 6/2/17.
 */
public class OngoingMatchingAlgorithmUtilUT extends BaseTestCase{

    @Mock
    OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Autowired
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
     MatchingAlgorithmUtil mockedmatchingAlgorithmUtil;

    @Mock
    private SolrTemplate solrTemplate;

    @Mock
    OngoingMatchingReportsService ongoingMatchingReportsService;

    @Autowired
    DateUtil dateUtil;

    @Mock
    SolrQueryBuilder solrQueryBuilder;

    @Value("${matching.algorithm.bibinfo.batchsize}")
    private String batchSize;

    @Test
    public void processMatchingForBibTest() {
        SolrDocument solrDocument = new SolrDocument();
        Mockito.when(ongoingMatchingAlgorithmUtil.processMatchingForBib(solrDocument, new ArrayList<>())).thenReturn("Success");
        String status = ongoingMatchingAlgorithmUtil.processMatchingForBib(solrDocument, new ArrayList<>());
        assertEquals("Success", status);
    }
    @Ignore
    public void testfetchUpdatedRecordsAndStartProcess() throws Exception {
        Date processDate = new Date();
        Date fromDate = dateUtil.getFromDate(processDate);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(processDate);
        SolrDocument solrDocument = new SolrDocument();
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        solrDocumentList.add(solrDocument);
        List<Integer> bibIds = new ArrayList<>();
        Integer rows = Integer.valueOf(batchSize);
        Date date = solrIndexRequest.getCreatedDate();
        solrIndexRequest.setProcessType(RecapCommonConstants.ONGOING_MATCHING_ALGORITHM_JOB);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"matchingAlgorithmUtil",matchingAlgorithmUtil);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"ongoingMatchingReportsService",ongoingMatchingReportsService);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"solrQueryBuilder",solrQueryBuilder);
      //  Mockito.when(ongoingMatchingAlgorithmUtil.getSolrTemplate()).thenCallRealMethod();
        Mockito.when(ongoingMatchingAlgorithmUtil.getFormattedDateString(fromDate)).thenCallRealMethod();
        String formattedDate = ongoingMatchingAlgorithmUtil.getFormattedDateString(fromDate);
        Mockito.when(ongoingMatchingAlgorithmUtil.fetchDataForOngoingMatchingBasedOnDate(formattedDate, 1000,0)).thenCallRealMethod();
        Mockito.when(solrQueryBuilder.fetchCreatedOrUpdatedBibs(formattedDate)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(mockedmatchingAlgorithmUtil).populateMatchingCounter();
        Mockito.when(ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartProcess(fromDate, rows)).thenCallRealMethod();

        String status = ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartProcess(fromDate, rows);
        assertEquals("Success", status);
    }

}