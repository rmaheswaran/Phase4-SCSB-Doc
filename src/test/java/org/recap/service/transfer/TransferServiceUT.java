package org.recap.service.transfer;

import org.junit.Ignore;
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
import org.recap.util.HelperUtil;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Anithav on 30/06/20.
 */

public class TransferServiceUT extends BaseTestCase {
    private static final Logger logger = LoggerFactory.getLogger(TransferServiceUT.class);



    @Mock
    private TransferService transferService;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    MarcUtil marcUtil;

    @Mock
    HelperUtil helperUtil;


    @Test
    public void processItemTransfer() throws Exception{
        Random random = new Random();
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("Princeton");
        institutionEntity.setInstitutionCode("PUL");
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setInstitution("PUL");
        HoldingsTransferRequest holdingsTransferRequestArrayList = new HoldingsTransferRequest();
        Source source = new Source();
        ItemSource itemSource = new ItemSource();
        itemSource.setOwningInstitutionItemId("6320902");
        itemSource.setOwningInstitutionBibId("1421");
        itemSource.setOwningInstitutionHoldingsId("1621");
        ItemDestination itemDestination = new ItemDestination();
        itemDestination.setOwningInstitutionHoldingsId("59753");
        itemDestination.setOwningInstitutionBibId("53642");
        itemDestination.setOwningInstitutionItemId("6320902");
        source.setOwningInstitutionBibId("1421");
        source.setOwningInstitutionHoldingsId("1621");
        holdingsTransferRequestArrayList.setSource(source);
        Destination destination = new Destination();
        destination.setOwningInstitutionBibId("53642");
        destination.setOwningInstitutionHoldingsId("59753");
        holdingsTransferRequestArrayList.setDestination(destination);
        ItemTransferRequest itemTransferRequestRequestArrayList = new ItemTransferRequest();
        itemTransferRequestRequestArrayList.setSource(itemSource);
        itemTransferRequestRequestArrayList.setDestination(itemDestination);
        List<HoldingsTransferRequest> HoldingsTransferRequest1 = new ArrayList<>();
        HoldingsTransferRequest1.add(holdingsTransferRequestArrayList);
        List<ItemTransferRequest> ItemTransferRequest1 = new ArrayList<>();
        ItemTransferRequest1.add(itemTransferRequestRequestArrayList);
        transferRequest.setHoldingTransfers(HoldingsTransferRequest1);
        transferRequest.setItemTransfers(ItemTransferRequest1);
        ReflectionTestUtils.setField(transferService,"institutionDetailsRepository",institutionDetailsRepository);
        ReflectionTestUtils.setField(transferService,"bibliographicDetailsRepository",bibliographicDetailsRepository);
        ReflectionTestUtils.setField(transferService,"nonHoldingIdInstitutionForTransferApi","NYPL");
        Mockito.when(transferService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(transferService.getHoldingsDetailsRepository()).thenReturn(holdingsDetailsRepository);
        Mockito.when(transferService.getInstitutionDetailsRepository()).thenReturn(institutionDetailsRepository);
        Mockito.when(transferService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.any(), Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(transferService.getHoldingsDetailsRepository().findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.any(), Mockito.any())).thenReturn(getBibliographicEntity().getHoldingsEntities().get(0));
        Mockito.when(transferService.getInstitutionDetailsRepository().findByInstitutionCode(Mockito.any())).thenReturn(institutionEntity);
        Mockito.when(transferService.getInstitutionDetailsRepository().findById(Mockito.any())).thenReturn(Optional.of(institutionEntity));
        Mockito.when(transferService.getHelperUtil()).thenReturn(helperUtil);
        Mockito.when(transferService.processItemTransfer(transferRequest, institutionEntity)).thenCallRealMethod();
        List<ItemTransferResponse> response= transferService.processItemTransfer(transferRequest, institutionEntity);
        assertNotNull(response);
    }

   // @Test
    @Ignore
    public void processHoldingTransfer() throws Exception{
        Random random = new Random();
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("Princeton");
        institutionEntity.setInstitutionCode("PUL");
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setInstitution("PUL");
        HoldingsTransferRequest holdingsTransferRequestArrayList = new HoldingsTransferRequest();
        Source source = new Source();
        ItemSource itemSource = new ItemSource();
        itemSource.setOwningInstitutionItemId("6320902");
        itemSource.setOwningInstitutionBibId("1421");
        itemSource.setOwningInstitutionHoldingsId("59753");
        ItemDestination itemDestination = new ItemDestination();
        itemDestination.setOwningInstitutionHoldingsId("59753");
        itemDestination.setOwningInstitutionBibId("53642");
        itemDestination.setOwningInstitutionItemId("6320902");
        source.setOwningInstitutionBibId("1421");
        source.setOwningInstitutionHoldingsId("59753");
        holdingsTransferRequestArrayList.setSource(source);
        Destination destination = new Destination();
        destination.setOwningInstitutionBibId("53642");
        destination.setOwningInstitutionHoldingsId("59753");
        holdingsTransferRequestArrayList.setDestination(destination);
        ItemTransferRequest itemTransferRequestRequestArrayList = new ItemTransferRequest();
        itemTransferRequestRequestArrayList.setSource(itemSource);
        itemTransferRequestRequestArrayList.setDestination(itemDestination);
        List<HoldingsTransferRequest> HoldingsTransferRequest1 = new ArrayList<>();
        HoldingsTransferRequest1.add(holdingsTransferRequestArrayList);
        List<ItemTransferRequest> ItemTransferRequest1 = new ArrayList<>();
        ItemTransferRequest1.add(itemTransferRequestRequestArrayList);
        transferRequest.setHoldingTransfers(HoldingsTransferRequest1);
        transferRequest.setItemTransfers(ItemTransferRequest1);
        ReflectionTestUtils.setField(transferService,"institutionDetailsRepository",institutionDetailsRepository);
        ReflectionTestUtils.setField(transferService,"bibliographicDetailsRepository",bibliographicDetailsRepository);
        ReflectionTestUtils.setField(transferService,"nonHoldingIdInstitutionForTransferApi","NYPL");
        Mockito.when(transferService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(transferService.getHoldingsDetailsRepository()).thenReturn(holdingsDetailsRepository);
        Mockito.when(transferService.getInstitutionDetailsRepository()).thenReturn(institutionDetailsRepository);
        Mockito.when(transferService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.any(), Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(transferService.getHoldingsDetailsRepository().findByOwningInstitutionHoldingsIdAndOwningInstitutionId(Mockito.any(), Mockito.any())).thenReturn(getBibliographicEntity().getHoldingsEntities().get(0));
        Mockito.when(transferService.getInstitutionDetailsRepository().findByInstitutionCode(Mockito.any())).thenReturn(institutionEntity);
        Mockito.when(transferService.getInstitutionDetailsRepository().findById(Mockito.any())).thenReturn(Optional.of(institutionEntity));
        Mockito.when(transferService.getHelperUtil()).thenReturn(helperUtil);
        Mockito.when(transferService.processHoldingTransfer(transferRequest, institutionEntity)).thenCallRealMethod();
        List<HoldingTransferResponse> response= transferService.processHoldingTransfer(transferRequest, institutionEntity);
        assertNotNull(response);
        assertEquals("TEST",response);
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
        bibliographicEntity.setOwningInstitutionBibId("1421");


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(today);
        holdingsEntity.setLastUpdatedDate(today);
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionHoldingsId("59753");


        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("6320902");
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
