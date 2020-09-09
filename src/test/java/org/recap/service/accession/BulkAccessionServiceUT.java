package org.recap.service.accession;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marc4j.marc.Record;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.accession.AccessionSummary;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.accession.resolver.CULBibDataResolver;
import org.recap.service.accession.resolver.NYPLBibDataResolver;
import org.recap.service.accession.resolver.PULBibDataResolver;
import org.recap.service.partnerservice.PrincetonService;
import org.recap.util.AccessionHelperUtil;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Anitha V on 01/10/20.
 */


@PrepareForTest(AccessionHelperUtil.class)
@ExtendWith(MockitoExtension.class)
public class BulkAccessionServiceUT  extends BaseTestCase {

    @Mock
    BulkAccessionService mockBulkAccessionService;

    @Mock
    protected AccessionHelperUtil accessionHelperUtil;

    @Mock
    InstitutionDetailsRepository mockedInstitutionDetailsRepository;

    @Mock
    private PULBibDataResolver pulBibDataResolver;

    @Mock
    private PrincetonService mockedPrincetonService;

    @Mock
    private CULBibDataResolver culBibDataResolver;

    @Mock
    private NYPLBibDataResolver nyplBibDataResolver;

    @Mock
    MarcUtil mockedMarcUtil;

    @Mock
    private ProducerTemplate producerTemplate;

    @Mock
    private Exchange exchange;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testdoBulkAccession() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101062128309");
        accessionRequestList.add(accessionRequest);
        String itemBarcode="32101062128309";
        String customerCode = "PB";
        String institution = "PUL";
        AccessionSummary accessionSummary = new AccessionSummary("test");
        AccessionSummary accessionSummary1 = new AccessionSummary("test");
        Set<AccessionResponse> accessionResponses = new HashSet<>();
        Set<AccessionResponse> accessionResponses1 = new HashSet<>();
        List<Map<String, String>> responseMaps = new ArrayList<>();
        List<ReportDataEntity> reportDataEntitys = new ArrayList<>();
        List<ReportDataEntity> reportDataEntitys1 = new ArrayList<>();
        ReflectionTestUtils.setField(mockBulkAccessionService,"batchAccessionThreadSize",20);
        ReflectionTestUtils.setField(mockBulkAccessionService,"accessionHelperUtil",accessionHelperUtil);
        ReflectionTestUtils.setField(mockBulkAccessionService,"applicationContext",applicationContext);
        ReflectionTestUtils.setField(accessionHelperUtil,"institutionDetailsRepository",mockedInstitutionDetailsRepository);
        ReflectionTestUtils.setField(accessionHelperUtil,"accessionService",mockBulkAccessionService);
        ReflectionTestUtils.setField(pulBibDataResolver,"princetonService",mockedPrincetonService);
        Mockito.when(mockBulkAccessionService.getTrimmedAccessionRequests(accessionRequestList)).thenCallRealMethod();
        List<AccessionRequest> trimmedAccessionRequests = mockBulkAccessionService.getTrimmedAccessionRequests(accessionRequestList);
        Mockito.when(mockBulkAccessionService.getAccessionHelperUtil()).thenReturn(accessionHelperUtil);
        Mockito.when(mockBulkAccessionService.getPulBibDataResolver()).thenReturn(pulBibDataResolver);
        Mockito.when(mockBulkAccessionService.getCulBibDataResolver()).thenReturn(culBibDataResolver);
        Mockito.when(mockBulkAccessionService.getNyplBibDataResolver()).thenReturn(nyplBibDataResolver);
        Mockito.when(pulBibDataResolver.isInterested(institution)).thenCallRealMethod();
        Mockito.when(pulBibDataResolver.getBibData(itemBarcode, customerCode)).thenCallRealMethod();
        Mockito.when(mockBulkAccessionService.getPrincetonService()).thenReturn(mockedPrincetonService);
        String bibDataResponse = mockedPrincetonService.getBibData(itemBarcode);
        Mockito.when(mockedPrincetonService.getBibData(itemBarcode)).thenReturn(bibDataResponse);
        List<Record> records = mockedMarcUtil.readMarcXml(bibDataResponse);
        Mockito.when(pulBibDataResolver.unmarshal(itemBarcode)).thenReturn(records);
        Mockito.when(accessionHelperUtil.getInstitutionIdCodeMap().get(institution)).thenCallRealMethod();
        Mockito.when(mockBulkAccessionService.getInstitutionDetailsRepository()).thenCallRealMethod();
        Mockito.when(accessionHelperUtil.removeDuplicateRecord(trimmedAccessionRequests)).thenCallRealMethod();
        Mockito.when(mockBulkAccessionService.validateBarcodeOrCustomerCode(itemBarcode,customerCode)).thenCallRealMethod();
        Mockito.when(mockBulkAccessionService.getCustomerCodeDetailsRepository()).thenReturn(customerCodeDetailsRepository);
        Mockito.when(mockBulkAccessionService.getOwningInstitution(customerCode)).thenCallRealMethod();
        Mockito.when(accessionHelperUtil.processRecords(accessionResponses,responseMaps,accessionRequest,reportDataEntitys,institution,true)).thenCallRealMethod();
        List<ItemEntity> itemEntityList = new ArrayList<>();
        String itemAreadyAccessionedMessage= RecapConstants.ITEM_ALREADY_ACCESSIONED ;
        Mockito.when(accessionHelperUtil.getItemEntityList(itemBarcode,customerCode)).thenReturn(itemEntityList);
        Mockito.when(accessionHelperUtil.checkItemBarcodeAlreadyExist(itemEntityList)).thenCallRealMethod();
        Mockito.when(mockBulkAccessionService.getBibDataResolvers()).thenCallRealMethod();
        Mockito.when(accessionHelperUtil.isItemDeaccessioned(itemEntityList)).thenCallRealMethod();
        Mockito.when(mockBulkAccessionService.getProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(accessionHelperUtil.createReportDataEntityList(accessionRequest,itemAreadyAccessionedMessage)).thenCallRealMethod();
        reportDataEntitys1.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, itemAreadyAccessionedMessage));
        Mockito.doCallRealMethod().when(mockBulkAccessionService).saveReportEntity(institution,reportDataEntitys1);
        int requestedCount = accessionRequestList.size();
        int duplicateCount = requestedCount - trimmedAccessionRequests.size();
        accessionSummary1.setRequestedRecords(requestedCount);
        accessionSummary1.setDuplicateRecords(duplicateCount);
        Mockito.doCallRealMethod().when(mockBulkAccessionService).prepareSummary(accessionSummary1,accessionResponses1);
        Mockito.doCallRealMethod().when(mockBulkAccessionService).addCountToSummary(accessionSummary,itemAreadyAccessionedMessage);
        Mockito.when(mockBulkAccessionService.doAccession(accessionRequestList,accessionSummary,exchange)).thenCallRealMethod();
        List<AccessionResponse> accessionResponses2 = mockBulkAccessionService.doAccession(accessionRequestList,accessionSummary,exchange);
        assertNull(accessionResponses2);
    }



}

