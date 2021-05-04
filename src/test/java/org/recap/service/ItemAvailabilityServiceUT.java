package org.recap.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.BibAvailabilityResponse;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 23/2/17.
 */
public class ItemAvailabilityServiceUT extends BaseTestCaseUT {

    @InjectMocks
    ItemAvailabilityService itemAvailabilityService;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Test
    public void testItemAvailabilityService() throws Exception {
        Mockito.when(itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalse(Mockito.anyString())).thenReturn("Available");
        String response = itemAvailabilityService.getItemStatusByBarcodeAndIsDeletedFalse("32101045675921");
        assertNotNull(response);
        assertEquals("Available",response);
    }

    @Test
    public void testGetItemStatusByBarcodeAndIsDeletedFalseList() throws Exception {
        List<String> barcodeList = Arrays.asList("32101045675921", "32101099791665", "32101086866140", "CU73995576","6668877","12346754");
        Mockito.when(itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalseList(Mockito.anyList())).thenReturn(saveBibSingleHoldingsSingleItem().getItemEntities());
        List<ItemAvailabilityResponse> itemAvailabilityResponses = itemAvailabilityService.getItemStatusByBarcodeAndIsDeletedFalseList(barcodeList);
        assertNotNull(itemAvailabilityResponses);
    }

    @Test
    public void testGetBibItemAvailabilityStatus() throws Exception {
        BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest = new BibItemAvailabityStatusRequest();
        bibItemAvailabityStatusRequest.setBibliographicId("93540");
        bibItemAvailabityStatusRequest.setInstitutionId("PUL");
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(Mockito.anyString())).thenReturn(institutionEntity);
        Mockito.when(bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(saveBibSingleHoldingsSingleItem());
        List<BibAvailabilityResponse> bibAvailabilityResponses = itemAvailabilityService.getBibItemAvailabilityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(bibAvailabilityResponses);
        assertEquals("Available",bibAvailabilityResponses.get(0).getItemAvailabilityStatus());
    }

    @Test
    public void testGetBibItemAvailabilityStatusException() throws Exception {
        BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest = new BibItemAvailabityStatusRequest();
        bibItemAvailabityStatusRequest.setBibliographicId("93540");
        bibItemAvailabityStatusRequest.setInstitutionId("PUL");
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(Mockito.anyString())).thenThrow(NullPointerException.class);
        List<BibAvailabilityResponse> bibAvailabilityResponses = itemAvailabilityService.getBibItemAvailabilityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(bibAvailabilityResponses);
    }

    @Test
    public void testGetBibItemAvailabilityStatusSCSB() throws Exception {
        BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest = new BibItemAvailabityStatusRequest();
        bibItemAvailabityStatusRequest.setBibliographicId("93540");
        bibItemAvailabityStatusRequest.setInstitutionId("SCSB");
        List<BibAvailabilityResponse> bibAvailabilityResponses = itemAvailabilityService.getBibItemAvailabilityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(bibAvailabilityResponses);
        assertEquals(RecapConstants.BIB_ITEM_DOESNOT_EXIST,bibAvailabilityResponses.get(0).getErrorMessage());
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity=new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("Princeton");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setId(93540);
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(5);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("12346754");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        ItemStatusEntity itemStatusEntity=new ItemStatusEntity();
        itemStatusEntity.setStatusDescription("test");
        itemStatusEntity.setStatusCode("Available");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setCollectionGroupEntity(getCollectionGroupEntity());
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        return bibliographicEntity;
    }

    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity=new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupDescription("Private");
        collectionGroupEntity.setId(3);
        collectionGroupEntity.setCollectionGroupCode("Private");
        return collectionGroupEntity;
    }

}
