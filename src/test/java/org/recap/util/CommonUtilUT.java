package org.recap.util;

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
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.search.resolver.impl.item.ItemCreatedByValueResolver;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.springframework.data.solr.core.SolrTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by AnithaV on 01/10/20.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SolrTemplate.class,SolrClient.class})
public class CommonUtilUT extends BaseTestCaseUT {

    @InjectMocks
    CommonUtil commonUtil;

    @Mock
    ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Before
    public  void setup(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getItemStatusMap(){
        List<ItemStatusEntity> itemStatusEntities = new ArrayList<>();
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode("SUCCESS");
        itemStatusEntity.setStatusDescription("AVAILABLE");
        itemStatusEntities.add(itemStatusEntity);
        Mockito.when(itemStatusDetailsRepository.findAll()).thenReturn(itemStatusEntities);
        commonUtil.getItemStatusMap();
        assertNotNull(commonUtil);
    }


    @Test
    public void getBibItemFromSolrFieldNames(){
        SolrDocument solrDocument=new SolrDocument();
        solrDocument.setField("_root_","root");
        Collection<String> fieldNames=new ArrayList<>();
        fieldNames.add("_root_");
        BibItem bibItems=new BibItem();
        bibItems.setRoot("_root_");
        BibItem bibItem= commonUtil.getBibItemFromSolrFieldNames(solrDocument,fieldNames,bibItems);
        assertEquals("root",bibItem.getRoot());
    }

    @Test
    public void getItem(){
        SolrDocument solrDocument = getEntries();
        List<HoldingsValueResolver> holdingsValueResolvers= commonUtil.getHoldingsValueResolvers();
        assertNotNull(holdingsValueResolvers);
        Item item= commonUtil.getItem(solrDocument);
        assertEquals("123",item.getRoot());

    }

    @Test
    public void getSolrDocumentsByDocType() throws IOException, SolrServerException {
        SolrDocumentList solrDocumentList =new SolrDocumentList();
        SolrDocument solrDocument = new SolrDocument();
        solrDocumentList.add(solrDocument);
        solrDocumentList.setNumFound(11l);
        SolrTemplate mocksolrTemplate1 = PowerMockito.mock(SolrTemplate.class);
        SolrClient solrClient=PowerMockito.mock(SolrClient.class);
        QueryResponse queryResponse=Mockito.mock(QueryResponse.class);
        PowerMockito.when(mocksolrTemplate1.getSolrClient()).thenReturn(solrClient);
        Mockito.when(solrClient.query(Mockito.any(SolrQuery.class))).thenReturn(queryResponse);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        SolrQuery solrQuery = new SolrQuery("testquery");
        solrQuery.setStart(1);
        solrQuery.setRows(1);
        SolrDocumentList solrDocumentListResult= commonUtil.getSolrDocumentsByDocType(solrQuery,mocksolrTemplate1);
        assertEquals(solrDocumentList,solrDocumentListResult);
    }

    private SolrDocument getEntries() {
        SolrDocument solrDocument=new SolrDocument();
        solrDocument.setField("_root_","123");
        solrDocument.setField("Availability_search","1");
        solrDocument.setField(RecapCommonConstants.IS_DELETED_ITEM,true);
        solrDocument.setField("CollectionGroupDesignation","CollectionGroupDesignation");
        solrDocument.setField("CustomerCode","123456");
        solrDocument.setField("DocType","DocType");
        solrDocument.setField("ItemOwningInstitution","ItemOwningInstitution");
        solrDocument.setField("ItemCreatedDate",new Date());
        solrDocument.setField("ItemId",1);
        solrDocument.setField("ItemLastUpdatedBy","ItemLastUpdatedBy");
        solrDocument.setField("ItemLastUpdatedDate",new Date());
        solrDocument.setField(RecapCommonConstants.HOLDINGS_ID,Arrays.asList(1));
        solrDocument.setField("VolumePartYear","true");
        solrDocument.setField("UseRestriction_display","true");
        solrDocument.setField("UseRestriction_search","true");
        solrDocument.setField("Availability_display","true");
        solrDocument.setField("CallNumber_display","true");
        solrDocument.setField("CallNumber_search","true");
        solrDocument.setField(RecapConstants.OWNING_INSTITUTION_ITEM_ID,"true");
        solrDocument.setField("Barcode","true");
        ItemCreatedByValueResolver ItemCreatedByValueResolver = new ItemCreatedByValueResolver();
        ItemCreatedByValueResolver.setValue(new Item(),"true");
        ItemCreatedByValueResolver.isInterested("ItemCreatedBy");
        return solrDocument;
    }


    private HoldingsEntity getHoldingsEntity() {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("12345");
        holdingsEntity.setDeleted(false);
        return holdingsEntity;
    }
    private BibliographicEntity getBibliographicEntity(){

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(123456);
        bibliographicEntity.setContent("Test".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1577261074");
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("34567");
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("843617540");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("123456");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }
}
