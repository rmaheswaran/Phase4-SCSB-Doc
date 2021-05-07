package org.recap.matchingalgorithm.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ReportDataDetailsRepository;
import org.recap.service.ActiveMqQueuesInfo;
import org.recap.util.MatchingAlgorithmUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 7/7/17.
 */
public class MatchingAlgorithmUpdateCGDServiceUT extends BaseTestCaseUT {

    @InjectMocks
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    ReportDataDetailsRepository reportDataDetailsRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    MatchingCounter matchingCounter;

    @Mock
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
    ActiveMqQueuesInfo activeMqQueuesInfo;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;



        @Test
        public void updateCGDProcessForMVMs() throws SolrServerException, InterruptedException, IOException {
        ReflectionTestUtils.setField(matchingCounter,"pulSharedCount",0);
        ReflectionTestUtils.setField(matchingCounter,"culSharedCount",0);
        ReflectionTestUtils.setField(matchingCounter,"nyplSharedCount",0);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(getInstitutionEntities());
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(getCollectionGroupEntities());
        Mockito.when(activeMqQueuesInfo.getActivemqQueuesInfo(Mockito.anyString())).thenReturn(1).thenReturn(0);
            matchingAlgorithmUpdateCGDService.updateCGDProcessForMonographs(1);
            matchingAlgorithmUpdateCGDService.updateCGDProcessForMVMs(1);
            matchingAlgorithmUpdateCGDService.updateCGDProcessForSerials(1);
        assertNotNull(getCollectionGroupEntities());
    }

        @Test
    public void getItemsCountForSerialsMatching(){
        Mockito.when(reportDataDetailsRepository.getCountOfRecordNumForMatchingSerials(Mockito.anyString())).thenReturn(1l);
        List<ReportDataEntity> reportDataEntities=new ArrayList<>();
        ReportDataEntity reportDataEntity=new ReportDataEntity();
        reportDataEntity.setHeaderName(ScsbCommonConstants.ONGOING_MATCHING_ALGORITHM);
        reportDataEntity.setHeaderValue("123");
        reportDataEntities.add(reportDataEntity);
        Mockito.when(reportDataDetailsRepository.getReportDataEntityForMatchingSerials(Mockito.anyString(),Mockito.anyLong(),Mockito.anyLong())).thenReturn(reportDataEntities);
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(getBibliographicEntity());
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntities);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(getInstitutionEntities());
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(getCollectionGroupEntities());
        ReflectionTestUtils.setField(matchingCounter,"culCGDUpdatedSharedCount",0);
        matchingAlgorithmUpdateCGDService.getItemsCountForSerialsMatching(1);
        assertNotNull(bibliographicEntities);
    }

    private List<InstitutionEntity> getInstitutionEntities() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setId(1);
        List<InstitutionEntity> institutionEntityList = new ArrayList<>();
        institutionEntityList.add(institutionEntity);
        return institutionEntityList;
    }

    private List<CollectionGroupEntity> getCollectionGroupEntities() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntity.setId(1);
        List<CollectionGroupEntity> collectionGroupEntityList = new ArrayList<>();
        collectionGroupEntityList.add(collectionGroupEntity);
        return collectionGroupEntityList;
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("bibContent".getBytes());
        bibliographicEntity.setOwningInstitutionId(2);
        Random random = new Random();
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setInstitutionEntity(getInstitutionEntity());
        bibliographicEntity.setItemEntities(Arrays.asList(getItemEntity()));
        return bibliographicEntity;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setId(2);
        institutionEntity.setInstitutionName("CUL");
        institutionEntity.setInstitutionCode("CUL");
        return institutionEntity;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
        itemEntity.setOwningInstitutionId(2);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        String barcode = "1234";
        itemEntity.setBarcode(barcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("Available");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        return itemEntity;
    }

}
