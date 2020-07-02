package org.recap.camel.processor;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 9/1/17.
 */
public class MatchingAlgorithmProcessorUT extends BaseTestCase{

    @Autowired
    MatchingAlgorithmProcessor matchingAlgorithmProcessor;

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void updateItemEntityTest() throws Exception {
        Integer itemId = saveBibHoldingAndItem();
        ItemEntity itemEntity = itemDetailsRepository.findByItemId(itemId);
        itemEntity.setCollectionGroupId(2);
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("Test");
        matchingAlgorithmProcessor.updateItemEntity(Arrays.asList(itemEntity));
        ItemEntity byItemId = itemDetailsRepository.findByItemId(itemId);
        assertTrue(byItemId.getCollectionGroupId().equals(2));
    }

    public Integer saveBibHoldingAndItem() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        Date date = new Date();
        bibliographicEntity.setCreatedDate(date);
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(date);
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("01010");

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(date);
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(date);
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("02020");

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(date);
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(date);
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("1231");
        itemEntity.setOwningInstitutionItemId("03030");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedEntity);
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getItemEntities());
        ItemEntity savedItemEntity = savedEntity.getItemEntities().get(0);
        assertNotNull(savedItemEntity);
        assertNotNull(savedItemEntity.getItemId());
        return savedItemEntity.getItemId();
    }
    @Test
    public void updateMatchingBibEntityTest() throws Exception {
        Map matchingBibMap = new HashMap();
        List<Integer> bibIds = new ArrayList<>();
        bibIds.add(1);
        matchingBibMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.COMPLETE_STATUS);
        matchingBibMap.put(RecapConstants.MATCHING_BIB_IDS, bibIds);
        matchingAlgorithmProcessor.updateMatchingBibEntity(matchingBibMap);
    }

    private MatchingBibEntity saveMatchingBibEntity(String matchingCriteria) {
        MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
        matchingBibEntity.setBibId(1);
        matchingBibEntity.setOwningInstitution("NYPL");
        matchingBibEntity.setOwningInstBibId("N1029");
        matchingBibEntity.setTitle("Middleware for ReCAP");
        matchingBibEntity.setOclc("129393");
        matchingBibEntity.setIsbn("93930");
        matchingBibEntity.setIssn("12283");
        matchingBibEntity.setLccn("039329");
        matchingBibEntity.setMaterialType("monograph");
        matchingBibEntity.setMatching(matchingCriteria);
        matchingBibEntity.setRoot("31");
        matchingBibEntity.setStatus(RecapConstants.PENDING);
        return matchingBibDetailsRepository.save(matchingBibEntity);
    }
}