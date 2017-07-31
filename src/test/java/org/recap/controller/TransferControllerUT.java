package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.transfer.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.transfer.TransferService;
import org.recap.util.HelperUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 27/7/17.
 */
public class TransferControllerUT extends BaseTestCase{

    @Mock
    private TransferController transferController;

    @Mock
    private TransferService transferService;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    private HelperUtil helperUtil;

    public ItemTransferRequest getItemTransferRequest(){
        ItemTransferRequest itemTransferRequest = new ItemTransferRequest();
        ItemSource itemSource = new ItemSource();
        itemSource.setOwningInstitutionItemId("AD223656465");
        itemSource.setOwningInstitutionBibId("1254685645");
        itemSource.setOwningInstitutionHoldingsId("45455435");
        ItemDestination itemDestination = new ItemDestination();
        itemDestination.setOwningInstitutionItemId("AD223656465");
        itemDestination.setOwningInstitutionBibId("1254685645");
        itemDestination.setOwningInstitutionHoldingsId("45455435");
        itemTransferRequest.setDestination(itemDestination);
        itemTransferRequest.setSource(itemSource);
        return itemTransferRequest;
    }

    public HoldingsTransferRequest getHoldingsTransferRequest(){
        HoldingsTransferRequest holdingsTransferRequest = new HoldingsTransferRequest();
        Source source = new Source();
        source.setOwningInstitutionBibId("121434554");
        source.setOwningInstitutionHoldingsId("45455435");
        Destination destination = new Destination();
        destination.setOwningInstitutionHoldingsId("45455435");
        destination.setOwningInstitutionBibId("14535314364");
        holdingsTransferRequest.setSource(source);
        holdingsTransferRequest.setDestination(destination);
        return holdingsTransferRequest;
    }

    @Test
    public void testTransferController() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setInstitution("PUL");
        transferRequest.setHoldingTransfers(Arrays.asList(getHoldingsTransferRequest()));
        transferRequest.setItemTransfers(Arrays.asList(getItemTransferRequest()));
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionId(1);
        Mockito.when(transferController.getHelperUtil()).thenReturn(helperUtil);
        Mockito.when(transferController.getTransferService()).thenReturn(transferService);
        Mockito.when(transferController.getTransferService().getInstitutionDetailsRepository()).thenReturn(institutionDetailsRepository);
        Mockito.when(transferController.getTransferService().getInstitutionDetailsRepository().findByInstitutionCode(Mockito.any())).thenReturn(institutionEntity);
        Mockito.when(transferService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(transferService.getHoldingsDetailsRepository()).thenReturn(holdingsDetailsRepository);
        Mockito.when(transferService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.any(), Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(transferService.getHoldingsDetailsRepository().findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.any(), Mockito.any())).thenReturn(getBibliographicEntity().getHoldingsEntities().get(0));
        Mockito.when(transferService.getHelperUtil()).thenReturn(helperUtil);
        Mockito.when(transferController.getTransferService().processHoldingTransfer(transferRequest, institutionEntity)).thenCallRealMethod();
        Mockito.when(transferController.getTransferService().processItemTransfer(transferRequest, institutionEntity)).thenCallRealMethod();
        Mockito.when(transferController.processTransfer(transferRequest)).thenCallRealMethod();
        TransferResponse transferResponse = transferController.processTransfer(transferRequest);
        assertNotNull(transferResponse);
        assertEquals(transferResponse.getMessage(),"Success");
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(transferController.getTransferService()).thenCallRealMethod();
        Mockito.when(transferController.getHelperUtil()).thenCallRealMethod();
        assertNotEquals(transferController.getHelperUtil(),helperUtil);
        assertNotEquals(transferController.getTransferService(),transferService);
    }

    @Test
    public void testHoldingtransferResponse(){
        HoldingTransferResponse holdingTransferResponse = new HoldingTransferResponse();
        HoldingTransferResponse holdingTransferResponse1 = new HoldingTransferResponse("success",new HoldingsTransferRequest(),true);
        holdingTransferResponse.setHoldingsTransferRequest(new HoldingsTransferRequest());
        assertNotNull(holdingTransferResponse.getHoldingsTransferRequest());
    }

    @Test
    public void testItemTransferResponse(){
        ItemTransferResponse itemTransferResponse = new ItemTransferResponse();
        ItemTransferResponse itemTransferResponse1 = new ItemTransferResponse("Success",new ItemTransferRequest(),true);
        itemTransferResponse.setItemTransferRequest(new ItemTransferRequest());
        assertNotNull(itemTransferResponse.getItemTransferRequest());
    }

    @Test
    public void testTransferResponse(){
        TransferResponse transferResponse = new TransferResponse();
        transferResponse.setHoldingTransferResponses(Arrays.asList(new HoldingTransferResponse()));
        transferResponse.setItemTransferResponses(Arrays.asList(new ItemTransferResponse()));
        assertNotNull(transferResponse.getHoldingTransferResponses());
        assertNotNull(transferResponse.getItemTransferResponses());
    }

    public BibliographicEntity getBibliographicEntity() throws Exception {
        Random random = new Random();
        Date today = new Date();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(today);
        holdingsEntity.setLastUpdatedDate(today);
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionHoldingsId("45455435");


        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("AD223656465");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("4123");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        return bibliographicEntity;
    }

}