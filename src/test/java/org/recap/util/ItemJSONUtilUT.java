package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.solr.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by premkb on 29/7/16.
 */
public class ItemJSONUtilUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(ItemJSONUtilUT.class);

    @InjectMocks
    ItemJSONUtil itemJSONUtil;

    @Mock
    private ProducerTemplate producerTemplate;

    @Test
    public void generateItemForIndex(){
        BibliographicEntity bibliographicEntity=getBibliographicEntity();
        ItemEntity itemEntity = getItemEntity(bibliographicEntity);
        Item item = itemJSONUtil.generateItemForIndex(itemEntity);
        assertNotNull(item);
        assertEquals("CU54519993",itemEntity.getBarcode());
        assertEquals("NA",itemEntity.getCustomerCode());
        assertEquals("In Library Use",itemEntity.getUseRestrictions());
        assertEquals("Bd. 1, Lfg. 7-10",itemEntity.getVolumePartYear());
        assertEquals("JFN 73-43",itemEntity.getCallNumber());
    }

    @Test
    public void saveExceptionReportForItem(){
        BibliographicEntity bibliographicEntity=null;
        ItemEntity itemEntity = getItemEntity(bibliographicEntity);
        Item item = itemJSONUtil.generateItemForIndex(itemEntity);
        assertNull(item);
    }

    private ItemEntity getItemEntity(BibliographicEntity bibliographicEntity) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setBarcode("CU54519993");
        itemEntity.setCustomerCode("NA");
        itemEntity.setUseRestrictions("In Library Use");
        itemEntity.setVolumePartYear("Bd. 1, Lfg. 7-10");
        itemEntity.setCallNumber("JFN 73-43");
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("Available");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setId(1);
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntity.setCollectionGroupDescription("collectionGroupEntity");
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        itemEntity.setBibliographicEntities(bibliographicEntities);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(1567));
        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        holdingsEntities.add(holdingsEntity);
        itemEntity.setHoldingsEntities(holdingsEntities);
        return itemEntity;
    }

    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(null);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("1");
        bibliographicEntity.setOwningInstitutionId(3);
        return bibliographicEntity;
    }
}
