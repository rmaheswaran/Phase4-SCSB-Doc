package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
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
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.ReportDataDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Anitha on 10/10/20.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SolrTemplate.class,SolrClient.class})
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
public class MatchingAlgorithmUtilUT extends BaseTestCaseUT {

    @InjectMocks
    MatchingAlgorithmUtil mockMatchingAlgorithmUtil;

    @Mock
    private SolrQueryBuilder solrQueryBuilder;

    @Mock
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Value("${matching.report.header.value.length}")
    Integer matchingHeaderValueLength;

    @Mock
    ReportDataDetailsRepository reportDataDetailsRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil,"matchingHeaderValueLength",matchingHeaderValueLength);
    }

    @Test
    public void getSingleMatchBibsAndSaveReport() throws Exception {
        Map<String, Set<Integer>> criteriaMap = getStringSetMap();
        Set<String> criteriaValueSet =new HashSet<>();
        String[] criteriaValueList={"1","2","3"};
        Map<Integer, MatchingBibEntity> bibEntityMap=getIntegerMatchingBibEntityMap();
        StringBuilder matchPointValue=new StringBuilder();
        String[] recap={RecapCommonConstants.MATCH_POINT_FIELD_OCLC,RecapCommonConstants.MATCH_POINT_FIELD_ISSN,RecapCommonConstants.MATCH_POINT_FIELD_LCCN,""};
        for (String re:recap){
            Set<Integer> getBibIdsForCriteriaValue=mockMatchingAlgorithmUtil.getBibIdsForCriteriaValue(criteriaMap,criteriaValueSet,re,re,criteriaValueList,bibEntityMap,matchPointValue);
        }
        List<Integer> bibIds = Arrays.asList(4,5,6);
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        matchingBibEntities.addAll(Arrays.asList(getMatchingBibEntity(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,1,"PUL","Middleware for ReCAP"),getMatchingBibEntity(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,2,"CUL","Middleware for ReCAP"),getMatchingBibEntity(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,3,"NYPL","Middleware for ReCAP")));
        Mockito.when(matchingBibDetailsRepository.getSingleMatchBibIdsBasedOnMatching(Mockito.anyString())).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getBibEntityBasedOnBibIds(Mockito.anyList())).thenReturn(matchingBibEntities);
        Map countsMap= mockMatchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(1,RecapCommonConstants.MATCH_POINT_FIELD_ISBN);
        assertNotNull(countsMap);
    }

    @Test
    public void getMatchingMatchPointsEntity() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil,"solrTemplate",mocksolrTemplate1);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        QueryResponse queryResponse= Mockito.mock(QueryResponse.class);

        List<FacetField> facetFields=new ArrayList<>();
        FacetField facetField=new FacetField(RecapCommonConstants.MATCH_POINT_FIELD_ISBN);
        facetField.add(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,93930);
        FacetField facetField1=new FacetField(RecapCommonConstants.MATCH_POINT_FIELD_ISBN);
        facetField.add(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,93931);
        facetFields.add(facetField);
        facetFields.add(facetField1);
        Mockito.when(queryResponse.getFacetFields()).thenReturn(facetFields);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        List<MatchingMatchPointsEntity> countsMap= mockMatchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapCommonConstants.MATCH_POINT_FIELD_ISBN);
        assertNotNull(countsMap);
    }

    @Test
    public void updateMonographicSetRecords() throws Exception {
        List<Integer> nonMonographRecordNums=Arrays.asList(1,2,3);
        List<ReportDataEntity> reportDataEntitiesToUpdate=new ArrayList<>();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName("Test");
        reportDataEntity.setHeaderValue("Test");
        reportDataEntitiesToUpdate.add(reportDataEntity);
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities=new ArrayList<>();
        MatchingMatchPointsEntity matchingMatchPointsEntity=new MatchingMatchPointsEntity();
        matchingMatchPointsEntity.setCriteriaValue("test");
        matchingMatchPointsEntities.add(matchingMatchPointsEntity);
        List<ReportEntity> reportEntities=new ArrayList<>();
        ReportEntity reportEntity=new ReportEntity();
        reportEntities.add(reportEntity);
        Collection headerValues=new ArrayList();
        headerValues.add("test");
        ReflectionTestUtils.setField(mockMatchingAlgorithmUtil,"matchingHeaderValueLength",3);
        ReportDataEntity reportDataEntityEmpty=mockMatchingAlgorithmUtil.getReportDataEntityForCollectionValues(Arrays.asList(""),"test");
        ReportDataEntity reportDataEntity1=mockMatchingAlgorithmUtil.getReportDataEntityForCollectionValues(headerValues,"test");
        Mockito.when(reportDetailRepository.findByIdIn(Mockito.anyList())).thenReturn(reportEntities);
        Mockito.when(reportDataDetailsRepository.getReportDataEntityByRecordNumIn(Mockito.anyList(),Mockito.anyString())).thenReturn(reportDataEntitiesToUpdate);
        mockMatchingAlgorithmUtil.updateMonographicSetRecords(nonMonographRecordNums,1);
        mockMatchingAlgorithmUtil.saveMatchingMatchPointEntities(matchingMatchPointsEntities);
        mockMatchingAlgorithmUtil.updateExceptionRecords(Arrays.asList(1,2,3),1);
    }

    @Test
    public void populateAndSaveReportEntity() throws Exception {
        List<Integer> bibIds = Arrays.asList(1,2,3);
        Set<Integer> bibIdSet = new HashSet<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = getIntegerMatchingBibEntityMap();
        Map countsMap= mockMatchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet,matchingBibEntityMap, RecapCommonConstants.OCLC_CRITERIA, RecapCommonConstants.MATCH_POINT_FIELD_ISBN,
               "2939384", "883939");
        assertNotNull(countsMap);
    }

    @Test
    public void processPendingMatchingBibs() throws Exception {
        String[] recap={RecapCommonConstants.MATCH_POINT_FIELD_OCLC,RecapCommonConstants.MATCH_POINT_FIELD_ISSN,RecapCommonConstants.MATCH_POINT_FIELD_LCCN,RecapCommonConstants.MATCH_POINT_FIELD_ISBN};
        for (String re:recap) {
            SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
            SolrClient solrClient = PowerMockito.mock(SolrClient.class);
            QueryResponse queryResponse = Mockito.mock(QueryResponse.class);
            SolrDocumentList solrDocumentList = getSolrDocuments();
            List<MatchingBibEntity> bibEntities = new ArrayList<>();
            bibEntities.addAll(Arrays.asList(getMatchingBibEntity(re, 1, "PUL", "Middleware for 1"), getMatchingBibEntity(re, 2, "CUL", "Middleware for 2"), getMatchingBibEntity(re, 3, "NYPL", "Middleware for 3")));
            ReflectionTestUtils.setField(mockMatchingAlgorithmUtil, "solrTemplate", mocksolrTemplate1);
            PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
            Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
            Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
            Mockito.when(solrQueryBuilder.solrQueryForOngoingMatching(re, Arrays.asList("129393"))).thenReturn("test");
            Mockito.when(matchingBibDetailsRepository.findByMatchingAndBibIdIn(Mockito.anyString(), Mockito.anyList())).thenReturn(bibEntities);
            List<Integer> bibIds = Arrays.asList(4, 5, 6);
            Set<Integer> bibIdSet = new HashSet<>();
            bibIdSet.addAll(bibIds);
            List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
            matchingBibEntities.addAll(Arrays.asList(getMatchingBibEntity(re, 1, "PUL", "Middleware for ReCAP"), getMatchingBibEntity(re, 2, "CUL", "Middleware for ReCAP"), getMatchingBibEntity(re, 3, "NYPL", "Middleware for ReCAP")));
            Map countsMap = mockMatchingAlgorithmUtil.processPendingMatchingBibs(matchingBibEntities, bibIdSet);
            assertNotNull(countsMap);
        }
    }

    private SolrDocumentList getSolrDocuments() {
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField(RecapCommonConstants.BIB_ID,Integer.valueOf(1));
        SolrDocument solrDocument1 = new SolrDocument();
        solrDocument1.setField(RecapCommonConstants.BIB_ID,Integer.valueOf(2));
        SolrDocument solrDocument2 = new SolrDocument();
        solrDocument2.setField(RecapCommonConstants.BIB_ID,Integer.valueOf(3));
        solrDocumentList.add(0,solrDocument);
        solrDocumentList.add(1,solrDocument1);
        solrDocumentList.add(2,solrDocument2);
        solrDocumentList.setNumFound(4);
        return solrDocumentList;
    }

    private Map<Integer, MatchingBibEntity> getIntegerMatchingBibEntityMap() {
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        matchingBibEntityMap.put(1, getMatchingBibEntity(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,1,"PUL","Middleware for ReCAP"));
        matchingBibEntityMap.put(2, getMatchingBibEntity(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,2,"CUL","Middleware for ReCAP"));
        matchingBibEntityMap.put(3, getMatchingBibEntity(RecapCommonConstants.MATCH_POINT_FIELD_ISBN,3,"NYPL","Middleware for ReCAP"));
        return matchingBibEntityMap;
    }

    private Map<String, Set<Integer>> getStringSetMap() {
        Set<Integer> criteria=new HashSet<>();
        criteria.add(1);
        criteria.add(2);
        criteria.add(3);
        Map<String, Set<Integer>> criteriaMap=new HashMap<>();
        criteriaMap.put("1",criteria);
        criteriaMap.put("2",criteria);
        criteriaMap.put("3",criteria);
        return criteriaMap;
    }

    private MatchingBibEntity getMatchingBibEntity(String matching,Integer bib,String inst,String title) {
        MatchingBibEntity matchingBibEntity=new MatchingBibEntity();
        matchingBibEntity.setMatching(matching);
        matchingBibEntity.setBibId(bib);
        matchingBibEntity.setId(10);
        matchingBibEntity.setOwningInstitution(inst);
        matchingBibEntity.setOwningInstBibId("N1029");
        matchingBibEntity.setTitle(title);
        matchingBibEntity.setOclc("129393");
        matchingBibEntity.setIsbn("93930");
        matchingBibEntity.setIssn("12283");
        matchingBibEntity.setLccn("039329");
        matchingBibEntity.setMaterialType("monograph");
        matchingBibEntity.setRoot("31");
        matchingBibEntity.setStatus(RecapConstants.PENDING);
        return matchingBibEntity;
    }


}
