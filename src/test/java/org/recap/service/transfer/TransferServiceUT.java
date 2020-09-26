package org.recap.service.transfer;

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
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.transfer.Destination;
import org.recap.model.transfer.HoldingTransferResponse;
import org.recap.model.transfer.HoldingsTransferRequest;
import org.recap.model.transfer.ItemDestination;
import org.recap.model.transfer.ItemSource;
import org.recap.model.transfer.ItemTransferRequest;
import org.recap.model.transfer.ItemTransferResponse;
import org.recap.model.transfer.Source;
import org.recap.model.transfer.TransferRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.accession.AccessionDAO;
import org.recap.service.accession.DummyDataService;
import org.recap.service.accession.SolrIndexService;
import org.recap.util.HelperUtil;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anithav on 30/06/20.
 */

public class TransferServiceUT extends BaseTestCaseUT {
    private static final Logger logger = LoggerFactory.getLogger(TransferServiceUT.class);

    @InjectMocks
    TransferService mockTransferService;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Mock
    MarcUtil marcUtil;

    @Mock
    HelperUtil helperUtil;

    @Value("${transfer.api.nonholdingid.institution}")
    String nonHoldingIdInstitutionForTransferApi;

    @Mock
    DummyDataService dummyDataService;

    @Mock
    AccessionDAO accessionDAO;

    @Mock
    SolrIndexService solrIndexService;

    @Mock
    ProducerTemplate producerTemplate;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mockTransferService, "nonHoldingIdInstitutionForTransferApi", nonHoldingIdInstitutionForTransferApi);
        ReflectionTestUtils.setField(helperUtil,"producerTemplate",producerTemplate);
    }

    @Test
    public void processItemTransfer() throws Exception{
        InstitutionEntity institutionEntity =getInstitutionEntity();
        TransferRequest transferRequest = getItemTransferRequest();
        HoldingsEntity holdingsEntity = getHoldingsEntity();
        BibliographicEntity bibliographicEntity = getbibliographicEntity();
        Mockito.when(dummyDataService.getHoldingsWithDummyDetails(Mockito.anyInt(), Mockito.any(), Mockito.anyString(),Mockito.anyString())).thenReturn(holdingsEntity);
        Mockito.when(mockTransferService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(1, "1421")).thenReturn(getBibliographicEntity());
        Mockito.when(mockTransferService.getHoldingsDetailsRepository().findByOwningInstitutionHoldingsIdAndOwningInstitutionId("1621",1)).thenReturn(getBibliographicEntity().getHoldingsEntities().get(0));
        Mockito.when(mockTransferService.getInstitutionDetailsRepository().findByInstitutionCode(Mockito.any())).thenReturn(institutionEntity);
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(getBibliographicEntity());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(bibliographicEntity);
        Mockito.when(mockTransferService.getInstitutionDetailsRepository().findById(Mockito.any())).thenReturn(Optional.of(institutionEntity));
        List<ItemTransferResponse> response= mockTransferService.processItemTransfer(transferRequest, institutionEntity);
        assertNotNull(response);
        assertEquals(RecapConstants.TRANSFER.SUCCESSFULLY_RELINKED,response.get(0).getMessage());
    }

    @Test
    public void processHoldingTransfer() throws Exception{
        InstitutionEntity institutionEntity = getInstitutionEntity();
        TransferRequest transferRequest = getHoldingTransferRequest();
        BibliographicEntity bibliographicEntity = getbibliographicEntity();
        Mockito.doNothing().when(helperUtil).saveReportEntity(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(),Mockito.anyList());
        Mockito.when(mockTransferService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(1,"1421")).thenReturn(getBibliographicEntity());
        Mockito.when(mockTransferService.getBibliographicDetailsRepository().findByOwningInstitutionIdAndOwningInstitutionBibId(Mockito.anyInt(),Mockito.anyString())).thenReturn(bibliographicEntity);
        Mockito.when(mockTransferService.getHoldingsDetailsRepository().findByOwningInstitutionHoldingsIdAndOwningInstitutionId("1621",1)).thenReturn(getBibliographicEntity().getHoldingsEntities().get(0));
        Mockito.when(mockTransferService.getInstitutionDetailsRepository().findByInstitutionCode(Mockito.any())).thenReturn(institutionEntity);
        Mockito.when(accessionDAO.saveBibRecord(getBibliographicEntity())).thenReturn(getBibliographicEntity());
        Mockito.when(accessionDAO.saveBibRecord(Mockito.any())).thenReturn(bibliographicEntity);
        Mockito.when(mockTransferService.processHoldingTransfer(transferRequest, institutionEntity)).thenCallRealMethod();
        List<HoldingTransferResponse> response= mockTransferService.processHoldingTransfer(transferRequest, institutionEntity);
        assertNotNull(response);
        assertEquals(RecapConstants.TRANSFER.SUCCESSFULLY_RELINKED,response.get(0).getMessage());
    }

    public HoldingsEntity getHoldingsEntity() throws Exception {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent((RecapConstants.DUMMY_HOLDING_CONTENT_XML).getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId("59753");
        holdingsEntity.setLastUpdatedBy("");
        return holdingsEntity;
    }

    public InstitutionEntity getInstitutionEntity() throws Exception {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("Princeton");
        institutionEntity.setInstitutionCode("PUL");
        return institutionEntity;
    }

    public BibliographicEntity getbibliographicEntity() throws Exception {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent((RecapConstants.DUMMY_BIB_CONTENT_XML).getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("");
        bibliographicEntity.setLastUpdatedBy("");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("53624");
        bibliographicEntity.setCatalogingStatus(RecapCommonConstants.INCOMPLETE_STATUS);
        bibliographicEntity.setItemEntities(new ArrayList<>());
        bibliographicEntity.setHoldingsEntities(getBibliographicEntity().getHoldingsEntities());
        bibliographicEntity.setBibliographicId(54);
        return bibliographicEntity;
    }

    public TransferRequest getHoldingTransferRequest() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setInstitution("PUL");
        HoldingsTransferRequest holdingsTransferRequestArrayList = new HoldingsTransferRequest();
        Source source = new Source();
        source.setOwningInstitutionBibId("1421");
        source.setOwningInstitutionHoldingsId("1621");
        holdingsTransferRequestArrayList.setSource(source);
        Destination destination = new Destination();
        destination.setOwningInstitutionBibId("53642");
        destination.setOwningInstitutionHoldingsId("1621");
        holdingsTransferRequestArrayList.setDestination(destination);
        List<HoldingsTransferRequest> HoldingsTransferRequest1 = new ArrayList<>();
        HoldingsTransferRequest1.add(holdingsTransferRequestArrayList);
        transferRequest.setHoldingTransfers(HoldingsTransferRequest1);
        return  transferRequest;
    }



    public TransferRequest getItemTransferRequest() throws Exception {
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
        return  transferRequest;
    }


    public BibliographicEntity getBibliographicEntity() throws Exception {
        Date today = new Date();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1421");
        bibliographicEntity.setBibliographicId(1);
        List<BibliographicEntity> bibliographicEntitylist = new LinkedList(Arrays.asList(bibliographicEntity));


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(today);
        holdingsEntity.setLastUpdatedDate(today);
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionHoldingsId("1621");
        holdingsEntity.setHoldingsId(1);
        List<HoldingsEntity> holdingsEntitylist = new LinkedList(Arrays.asList(holdingsEntity));


        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("6320902");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("32101086866140");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PA");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setItemId(1);
        List<ItemEntity> itemEntitylist = new LinkedList(Arrays.asList(itemEntity));

        holdingsEntity.setBibliographicEntities(bibliographicEntitylist);
        holdingsEntity.setItemEntities(itemEntitylist);
        bibliographicEntity.setHoldingsEntities(holdingsEntitylist);
        bibliographicEntity.setItemEntities(itemEntitylist);
        itemEntity.setHoldingsEntities(holdingsEntitylist);
        itemEntity.setBibliographicEntities(bibliographicEntitylist);

        return bibliographicEntity;
    }
}
