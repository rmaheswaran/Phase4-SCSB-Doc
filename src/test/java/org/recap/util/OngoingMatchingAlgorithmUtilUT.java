package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingAlgorithmCGDProcessor;
import org.recap.matchingalgorithm.service.OngoingMatchingReportsService;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.solr.BibItem;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by angelind on 6/2/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SolrTemplate.class,SolrClient.class})
public class OngoingMatchingAlgorithmUtilUT extends BaseTestCaseUT {

    @InjectMocks
    OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Mock
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
     MatchingAlgorithmUtil mockedmatchingAlgorithmUtil;

    @Mock
    OngoingMatchingReportsService ongoingMatchingReportsService;

    @Mock
    SolrQueryBuilder solrQueryBuilder;

    @Mock
    CommonUtil commonUtil;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    MatchingAlgorithmCGDProcessor matchingAlgorithmCGDProcessor;

    @Mock
    UpdateCgdUtil updateCgdUtil;

    @Test
    public void fetchUpdatedRecordsAndStartProcessSingleMatch() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        QueryResponse queryResponse=Mockito.mock(QueryResponse.class);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"solrTemplate",mocksolrTemplate1);
        SolrQuery solrQuery = new SolrQuery("testquery");
        solrQuery.setStart(1);
        solrQuery.setRows(1);
        Date processDate = new Date();
        Date fromDate = getFromDate(processDate);
        Set<String> unMatchingTitleHeaderSet=new HashSet<>();
        unMatchingTitleHeaderSet.add("test");
        List<CollectionGroupEntity> collectionGroupEntities = getCollectionGroupEntities();
        List<InstitutionEntity> institutionEntities = getInstitutionEntities();
        List<Integer> itemIds=new ArrayList<>();
        itemIds.add(1);
        String[]  matchPoints={RecapCommonConstants.OCLC_NUMBER,RecapCommonConstants.ISBN_CRITERIA,RecapCommonConstants.ISSN_CRITERIA,RecapCommonConstants.LCCN_CRITERIA};
        for (String matchPoint:matchPoints) {
            SolrDocumentList solrDocumentList = getSolrDocumentsSingle(matchPoint);
            ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"matchingAlgorithmUtil",matchingAlgorithmUtil);
            ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"ongoingMatchingReportsService",ongoingMatchingReportsService);
            ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"solrQueryBuilder",solrQueryBuilder);
            ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"commonUtil",commonUtil);
            ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"collectionGroupDetailsRepository",collectionGroupDetailsRepository);
            PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
            Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
            Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
            Mockito.when(commonUtil.getBibItemFromSolrFieldNames(Mockito.any(SolrDocument.class),Mockito.anyCollection(),Mockito.any(BibItem.class))).thenReturn(getBibItemSingle("PUL",1,RecapCommonConstants.MONOGRAPH)).thenReturn(getBibItemSingle("CUL",2,RecapCommonConstants.MONOGRAPH)).thenReturn(getBibItemSingle("NYPL",3,RecapCommonConstants.MONOGRAPH));
            Mockito.doCallRealMethod().when(mockedmatchingAlgorithmUtil).populateMatchingCounter();
            Mockito.when(solrQueryBuilder.fetchCreatedOrUpdatedBibs(Mockito.anyString())).thenReturn("testquery");
            Mockito.when(solrQueryBuilder.solrQueryForOngoingMatching(RecapCommonConstants.OCLC_NUMBER,"test")).thenReturn("testquery");
            Mockito.when(matchingAlgorithmUtil.getMatchingAndUnMatchingBibsOnTitleVerification(Mockito.anyMap())).thenReturn(unMatchingTitleHeaderSet);
            Mockito.when(matchingAlgorithmUtil.buildReportEntity(Mockito.anyString())).thenReturn(buildReportEntity("test"));
            Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(collectionGroupEntities);
            Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
            ongoingMatchingAlgorithmUtil.updateCGDForItemInSolr(itemIds);
            String status = ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartProcess(fromDate, 1);
            assertEquals(RecapCommonConstants.SUCCESS, status);
            
        }
    }

    @Test
    public void fetchUpdatedRecordsAndStartProcessSingleMatchSerial() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        QueryResponse queryResponse=Mockito.mock(QueryResponse.class);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"solrTemplate",mocksolrTemplate1);
        SolrQuery solrQuery = new SolrQuery("testquery");
        solrQuery.setStart(1);
        solrQuery.setRows(1);
        Date processDate = new Date();
        Date fromDate = getFromDate(processDate);
        Set<String> unMatchingTitleHeaderSet=new HashSet<>();
        unMatchingTitleHeaderSet.add("test");
        SolrDocumentList solrDocumentList = getSolrDocumentsSingle(RecapCommonConstants.OCLC_NUMBER);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"matchingAlgorithmUtil",matchingAlgorithmUtil);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"ongoingMatchingReportsService",ongoingMatchingReportsService);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"solrQueryBuilder",solrQueryBuilder);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"collectionGroupDetailsRepository",collectionGroupDetailsRepository);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        Mockito.when(commonUtil.getBibItemFromSolrFieldNames(Mockito.any(SolrDocument.class),Mockito.anyCollection(),Mockito.any(BibItem.class))).thenReturn(getBibItemSingle("PUL",1,RecapCommonConstants.SERIAL)).thenReturn(getBibItemSingle("CUL",2,RecapCommonConstants.SERIAL)).thenReturn(getBibItemSingle("NYPL",3,RecapCommonConstants.SERIAL));
        Mockito.doCallRealMethod().when(mockedmatchingAlgorithmUtil).populateMatchingCounter();
        Mockito.when(solrQueryBuilder.fetchCreatedOrUpdatedBibs(Mockito.anyString())).thenReturn("testquery");
        Mockito.when(solrQueryBuilder.solrQueryForOngoingMatching(RecapCommonConstants.OCLC_NUMBER,"test")).thenReturn("testquery");
        Mockito.when(matchingAlgorithmUtil.getMatchingAndUnMatchingBibsOnTitleVerification(Mockito.anyMap())).thenReturn(unMatchingTitleHeaderSet);
        Mockito.when(matchingAlgorithmUtil.buildReportEntity(Mockito.anyString())).thenReturn(buildReportEntity("test"));
        String status = ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartProcess(fromDate, 1);
        assertEquals(RecapCommonConstants.SUCCESS, status);
    }

    @Test
    public void fetchUpdatedRecordsAndStartProcessMultiMatch() throws Exception {
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        QueryResponse queryResponse=Mockito.mock(QueryResponse.class);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"solrTemplate",mocksolrTemplate1);
        SolrQuery solrQuery = new SolrQuery("testquery");
        solrQuery.setStart(1);
        solrQuery.setRows(1);
        Date processDate = new Date();
        Date fromDate = getFromDate(processDate);
        SolrDocumentList solrDocumentList = getSolrDocumentsMulti();
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"matchingAlgorithmUtil",matchingAlgorithmUtil);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"ongoingMatchingReportsService",ongoingMatchingReportsService);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"solrQueryBuilder",solrQueryBuilder);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"commonUtil",commonUtil);
        ReflectionTestUtils.setField(ongoingMatchingAlgorithmUtil,"collectionGroupDetailsRepository",collectionGroupDetailsRepository);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        Mockito.when(commonUtil.getBibItemFromSolrFieldNames(Mockito.any(SolrDocument.class),Mockito.anyCollection(),Mockito.any(BibItem.class))).thenReturn(getBibItemSingle("PUL",1,RecapCommonConstants.SERIAL)).thenReturn(getBibItemSingle("CUL",2,RecapCommonConstants.SERIAL)).thenReturn(getBibItemSingle("NYPL",3,RecapCommonConstants.SERIAL));
        Mockito.doCallRealMethod().when(mockedmatchingAlgorithmUtil).populateMatchingCounter();
        Mockito.when(solrQueryBuilder.fetchCreatedOrUpdatedBibs(Mockito.anyString())).thenReturn("testquery");
        Mockito.when(solrQueryBuilder.solrQueryForOngoingMatching(RecapCommonConstants.OCLC_NUMBER,"test")).thenReturn("testquery");
        String status = ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartProcess(fromDate, 1);
        assertEquals(RecapCommonConstants.SUCCESS, status);

    }

    private List<InstitutionEntity> getInstitutionEntities() {
        List<InstitutionEntity> institutionEntities = new ArrayList<>();
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("Princeton");
        institutionEntity.setId(1);
        institutionEntities.add(institutionEntity);
        return institutionEntities;
    }

    private List<CollectionGroupEntity> getCollectionGroupEntities() {
        List<CollectionGroupEntity> collectionGroupEntities=new ArrayList<>();
        CollectionGroupEntity collectionGroupEntity=new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntity.setCollectionGroupDescription("Shared");
        collectionGroupEntities.add(collectionGroupEntity);
        return collectionGroupEntities;
    }

    public ReportEntity buildReportEntity(String fileName) {
        ReportEntity unMatchReportEntity = new ReportEntity();
        unMatchReportEntity.setType("TitleException");
        unMatchReportEntity.setCreatedDate(new Date());
        unMatchReportEntity.setInstitutionName(RecapCommonConstants.ALL_INST);
        unMatchReportEntity.setFileName(fileName);
        return unMatchReportEntity;
    }

    private BibItem getBibItemSingle(String inst,Integer id,String type) {
        BibItem bibItem= new BibItem();
        bibItem.setIsbn(Collections.singletonList("111"));
        bibItem.setIssn(Collections.singletonList("222"));
        bibItem.setLccn("333");
        bibItem.setOclcNumber(Collections.singletonList("1"));
        bibItem.setBibId(id);
        bibItem.setOwningInstitution(inst);
        bibItem.setOwningInstitutionBibId("987");
        bibItem.setTitleSubFieldA("a");
        bibItem.setMaterialType(type);
        bibItem.setLeaderMaterialType(type);
        return bibItem;
    }

    private SolrDocumentList getSolrDocumentsSingle(String matchPoint) {
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField(matchPoint,String.valueOf(1));
        solrDocument.setField(RecapConstants.MATERIAL_TYPE,RecapCommonConstants.MONOGRAPH);
        SolrDocument solrDocument1 = new SolrDocument();
        solrDocument1.setField(matchPoint,String.valueOf(2));
        solrDocument1.setField(RecapConstants.MATERIAL_TYPE,RecapCommonConstants.MONOGRAPH);
        SolrDocument solrDocument2 = new SolrDocument();
        solrDocument2.setField(matchPoint,String.valueOf(3));
        solrDocument2.setField(RecapConstants.MATERIAL_TYPE,RecapCommonConstants.MONOGRAPH);
        solrDocumentList.add(0,solrDocument);
        solrDocumentList.add(1,solrDocument1);
        solrDocumentList.add(2,solrDocument2);
        solrDocumentList.setNumFound(3);
        return solrDocumentList;
    }

    private SolrDocumentList getSolrDocumentsMulti() {
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField(RecapCommonConstants.OCLC_NUMBER,String.valueOf(1));
        solrDocument.setField(RecapCommonConstants.ISBN_CRITERIA,String.valueOf(11));
        solrDocument.setField(RecapCommonConstants.ISSN_CRITERIA,String.valueOf(111));
        solrDocument.setField(RecapCommonConstants.LCCN_CRITERIA,String.valueOf(1111));
        solrDocumentList.add(0,solrDocument);
        solrDocumentList.setNumFound(5);
        return solrDocumentList;
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }
}