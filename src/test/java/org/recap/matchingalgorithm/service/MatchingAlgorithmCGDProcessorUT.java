package org.recap.matchingalgorithm.service;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingAlgorithmCGDProcessor;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MatchingAlgorithmCGDProcessorUT extends BaseTestCaseUT {

    @InjectMocks
    MatchingAlgorithmCGDProcessor matchingAlgorithmCGDProcessor;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    MatchingCounter matchingCounter;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(matchingCounter,"nyplCGDUpdatedOpenCount",0);
        ReflectionTestUtils.setField(matchingCounter,"nyplOpenCount",0);
        ReflectionTestUtils.setField(matchingCounter,"nyplSharedCount",0);
        ReflectionTestUtils.setField(matchingCounter,"pulCGDUpdatedOpenCount",0);
        ReflectionTestUtils.setField(matchingCounter,"pulOpenCount",0);
        ReflectionTestUtils.setField(matchingCounter,"pulSharedCount",0);
        ReflectionTestUtils.setField(matchingCounter,"culCGDUpdatedOpenCount",0);
        ReflectionTestUtils.setField(matchingCounter,"culOpenCount",0);
        ReflectionTestUtils.setField(matchingCounter,"culSharedCount",0);
        ReflectionTestUtils.setField(matchingCounter,"pulCGDUpdatedSharedCount",0);
        ReflectionTestUtils.setField(matchingCounter,"culCGDUpdatedSharedCount",0);
        ReflectionTestUtils.setField(matchingCounter,"nyplCGDUpdatedSharedCount",0);
    }

    @Test
    public void updateCGDProcess() throws Exception {
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.REPORTS_OPEN,2);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        String matchingType= RecapConstants.INITIAL_MATCHING_OPERATION_TYPE;
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"matchingType",matchingType);
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        matchingAlgorithmCGDProcessor.updateCGDProcess(itemEntityMap);
        assertNotNull(itemEntityMap);
    }

    @Test
    public void updateCGDProcess1() throws Exception {
        ItemEntity itemEntityPUL = getItemEntity(1);
        itemEntityPUL.setInitialMatchingDate(new Date());
        ItemEntity itemEntityCUL = getItemEntity(2);
        itemEntityCUL.setInitialMatchingDate(new Date());
        ItemEntity itemEntityNYPL = getItemEntity(3);
        itemEntityNYPL.setInitialMatchingDate(new Date());
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntityPUL);
        itemEntities.add(itemEntityCUL);
        itemEntities.add(itemEntityNYPL);
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);
        Map<Integer, ItemEntity> itemEntityMap=new HashMap<>();
        itemEntityMap.put(1,itemEntityPUL);
        itemEntityMap.put(2,itemEntityCUL);
        itemEntityMap.put(3,itemEntityNYPL);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.REPORTS_OPEN,2);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        String matchingType= RecapCommonConstants.ONGOING_MATCHING_ALGORITHM;
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"matchingType",matchingType);
        matchingAlgorithmCGDProcessor.updateCGDProcess(itemEntityMap);
        assertNotNull(itemEntityMap);
    }

    @Test
    public void updateCGDProcess2() throws Exception {
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.REPORTS_OPEN,2);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        String matchingType= RecapCommonConstants.ONGOING_MATCHING_ALGORITHM;
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"matchingType",matchingType);
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        assertNotNull(itemEntityMap);
    }

    @Test
    public void updateCGDProcess3() throws Exception {
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.REPORTS_OPEN,2);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        String matchingType= RecapConstants.INITIAL_MATCHING_OPERATION_TYPE;
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"matchingType",matchingType);
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        matchingAlgorithmCGDProcessor.updateCGDProcess(itemEntityMap);
        assertNotNull(itemEntityMap);
    }

    @Test
    public void updateCGDProcess4() throws Exception {
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.REPORTS_OPEN,2);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        String matchingType= RecapCommonConstants.ONGOING_MATCHING_ALGORITHM;
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"matchingType",matchingType);
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        matchingAlgorithmCGDProcessor.updateCGDProcess(itemEntityMap);
        assertNotNull(itemEntityMap);
    }

    @Test
    public void checkForMonographAndPopulateValues() throws Exception {
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);

        Set<String> materialTypeSet=new HashSet<>();
        List<Integer> bibIdList=new ArrayList<>();
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntity(3);
        List<ItemEntity> itemEntities1=new ArrayList<>();
        ItemEntity itemEntity=getItemEntity(3);
        itemEntity.setUseRestrictions(RecapCommonConstants.IN_LIBRARY_USE);
        itemEntities1.add(itemEntity);
        bibliographicEntity.setItemEntities(itemEntities1);
        List<HoldingsEntity> holdingsEntities=new ArrayList<>();
        HoldingsEntity holdingsEntity = getHoldingsEntity(3);
        holdingsEntities.add(holdingsEntity);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.SHARED_CGD,1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        boolean checkForMonographAndPopulateValues=matchingAlgorithmCGDProcessor.checkForMonographAndPopulateValues(materialTypeSet,itemEntityMap,bibIdList);
        assertEquals(true,checkForMonographAndPopulateValues);
    }

    @Test
    public void checkForMonographAndPopulateValuesElseNYPL() throws Exception {
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);

        Set<String> materialTypeSet=new HashSet<>();
        List<Integer> bibIdList=new ArrayList<>();
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntity(3);
        List<ItemEntity> itemEntities1=new ArrayList<>();
        ItemEntity itemEntity=getItemEntity(3);
        ItemEntity itemEntity1=getItemEntity(3);
        itemEntity1.setUseRestrictions(RecapCommonConstants.SUPERVISED_USE);
        itemEntity1.setCopyNumber(2);
        itemEntities1.add(itemEntity);
        itemEntities1.add(itemEntity1);
        bibliographicEntity.setItemEntities(itemEntities1);
        List<HoldingsEntity> holdingsEntities=new ArrayList<>();
        HoldingsEntity holdingsEntity = getHoldingsEntity(3);
        holdingsEntities.add(holdingsEntity);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.SHARED_CGD,1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        Map institutionMap=new HashMap();
        institutionMap.put("NYPL",3);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"institutionMap",institutionMap);
        boolean checkForMonographAndPopulateValues=matchingAlgorithmCGDProcessor.checkForMonographAndPopulateValues(materialTypeSet,itemEntityMap,bibIdList);
        assertEquals(true,checkForMonographAndPopulateValues);
    }

    @Test
    public void checkForMonographAndPopulateValuesElseCUL() throws Exception {
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);

        Set<String> materialTypeSet=new HashSet<>();
        List<Integer> bibIdList=new ArrayList<>();
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntity(2);
        List<HoldingsEntity> holdingsEntities=new ArrayList<>();
        HoldingsEntity holdingsEntity = getHoldingsEntity(2);
        HoldingsEntity holdingsEntity1 = getHoldingsEntity(2);
        holdingsEntities.add(holdingsEntity);
        holdingsEntities.add(holdingsEntity1);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        List<ItemEntity> itemEntities1=new ArrayList<>();
        ItemEntity itemEntity=getItemEntity(2);
        ItemEntity itemEntity1=getItemEntity(2);
        itemEntities1.add(itemEntity);
        itemEntities1.add(itemEntity1);
        bibliographicEntity.setItemEntities(itemEntities1);
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.SHARED_CGD,1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        Map institutionMap=new HashMap();
        institutionMap.put("CUL",2);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"institutionMap",institutionMap);
        boolean checkForMonographAndPopulateValues=matchingAlgorithmCGDProcessor.checkForMonographAndPopulateValues(materialTypeSet,itemEntityMap,bibIdList);
        assertEquals(true,checkForMonographAndPopulateValues);
    }

    @Test
    public void checkForMonographAndPopulateValuesElsePUL() throws Exception {
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(getItemEntity(1));
        itemEntities.add(getItemEntity(2));
        itemEntities.add(getItemEntity(3));
        Map<Integer, List<ItemEntity>> owningInstitutionMap=new HashMap<>();
        owningInstitutionMap.put(1,itemEntities);
        owningInstitutionMap.put(2,itemEntities);
        owningInstitutionMap.put(3,itemEntities);

        Set<String> materialTypeSet=new HashSet<>();
        List<Integer> bibIdList=new ArrayList<>();
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntity(1);
        List<HoldingsEntity> holdingsEntities=new ArrayList<>();
        HoldingsEntity holdingsEntity = getHoldingsEntity(1);
        holdingsEntities.add(holdingsEntity);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        List<ItemEntity> itemEntities1=new ArrayList<>();
        ItemEntity itemEntity=getItemEntity(1);
        ItemEntity itemEntity1=getItemEntity(1);
        itemEntities1.add(itemEntity);
        itemEntities1.add(itemEntity1);
        bibliographicEntity.setItemEntities(itemEntities1);
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.SHARED_CGD,1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        Map institutionMap=new HashMap();
        institutionMap.put("PUL",1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"institutionMap",institutionMap);
        boolean checkForMonographAndPopulateValues=matchingAlgorithmCGDProcessor.checkForMonographAndPopulateValues(materialTypeSet,itemEntityMap,bibIdList);
        assertEquals(false,checkForMonographAndPopulateValues);
    }

    @Test
    public void populateItemEntityMapPUL() throws Exception {
        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        List<Integer> bibIdList=new ArrayList<>();
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntity(1);
        List<HoldingsEntity> holdingsEntities=new ArrayList<>();
        HoldingsEntity holdingsEntity = getHoldingsEntity(1);
        holdingsEntities.add(holdingsEntity);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        List<ItemEntity> itemEntities1=new ArrayList<>();
        ItemEntity itemEntity=getItemEntity(2);
        ItemEntity itemEntity1=getItemEntity(2);
        itemEntities1.add(itemEntity);
        itemEntities1.add(itemEntity1);
        bibliographicEntity.setItemEntities(itemEntities1);
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.SHARED_CGD,1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        Map institutionMap=new HashMap();
        institutionMap.put("PUL",1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"institutionMap",institutionMap);
        matchingAlgorithmCGDProcessor.populateItemEntityMap(itemEntityMap,bibIdList);
        assertNotNull(itemEntityMap);
    }

    @Test
    public void populateItemEntityMapCUL() throws Exception {

        Map<Integer, ItemEntity> itemEntityMap = getIntegerItemEntityMap();
        List<Integer> bibIdList=new ArrayList<>();
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity=getBibliographicEntity(2);
        List<HoldingsEntity> holdingsEntities=new ArrayList<>();
        HoldingsEntity holdingsEntity = getHoldingsEntity(1);
        holdingsEntities.add(holdingsEntity);
        bibliographicEntity.setHoldingsEntities(holdingsEntities);
        List<ItemEntity> itemEntities1=new ArrayList<>();
        ItemEntity itemEntity=getItemEntity(1);
        ItemEntity itemEntity1=getItemEntity(1);
        itemEntities1.add(itemEntity);
        itemEntities1.add(itemEntity1);
        bibliographicEntity.setItemEntities(itemEntities1);
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.findByIdIn(Mockito.anyList())).thenReturn(bibliographicEntities);
        Map collectionGroupMap=new HashMap();
        collectionGroupMap.put(RecapCommonConstants.SHARED_CGD,1);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"collectionGroupMap",collectionGroupMap);
        Map institutionMap=new HashMap();
        institutionMap.put("CUL",2);
        ReflectionTestUtils.setField(matchingAlgorithmCGDProcessor,"institutionMap",institutionMap);
        matchingAlgorithmCGDProcessor.populateItemEntityMap(itemEntityMap,bibIdList);
        assertNotNull(itemEntityMap);
    }

    private BibliographicEntity getBibliographicEntity(int inst) {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("bibContent".getBytes());
        bibliographicEntity.setOwningInstitutionId(inst);
        Random random = new Random();
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setId(inst);
        if(inst==1) {
            institutionEntity.setInstitutionName("PUL");
            institutionEntity.setInstitutionCode("PUL");
        }else if(inst==2){
            institutionEntity.setInstitutionName("CUL");
            institutionEntity.setInstitutionCode("CUL");
        }else if(inst==3){
            institutionEntity.setInstitutionName("NYPL");
            institutionEntity.setInstitutionCode("NYPL");
        }
        bibliographicEntity.setInstitutionEntity(institutionEntity);
        return bibliographicEntity;
    }

    private HoldingsEntity getHoldingsEntity(int inst) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("holdingContent".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId("657");
        holdingsEntity.setOwningInstitutionId(inst);
        return holdingsEntity;
    }

    private ItemEntity getItemEntity(int inst) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
        itemEntity.setOwningInstitutionId(inst);
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
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("Available");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        return itemEntity;
    }

    private Map<Integer, ItemEntity> getIntegerItemEntityMap() {
        Map<Integer, ItemEntity> itemEntityMap=new HashMap<>();
        itemEntityMap.put(1,getItemEntity(1));
        itemEntityMap.put(2,getItemEntity(2));
        itemEntityMap.put(3,getItemEntity(3));
        return itemEntityMap;
    }
}
