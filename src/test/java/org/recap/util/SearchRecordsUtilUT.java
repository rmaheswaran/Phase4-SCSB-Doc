package org.recap.util;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.model.search.DataDumpSearchResult;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.BibSolrDocumentRepository;
import org.recap.repository.solr.main.DataDumpSolrDocumentRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 24/2/17.
 */
public class SearchRecordsUtilUT extends BaseTestCaseUT {

    @InjectMocks
    SearchRecordsUtil searchRecordsUtil;

    @Mock
    BibSolrDocumentRepository bibSolrDocumentRepository;

    @Mock
    DataDumpSolrDocumentRepository dataDumpSolrDocumentRepository;


    @Test
    public void searchRecords() throws Exception {
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        Map<String, Object> searchResponse=new HashMap<>();
        List<BibItem> bibItems = getBibItemList();
        searchResponse.put(RecapCommonConstants.SEARCH_SUCCESS_RESPONSE,bibItems);
        Mockito.when(bibSolrDocumentRepository.search(Mockito.any(SearchRecordsRequest.class))).thenReturn(searchResponse);
        List<SearchResultRow> searchRecords = searchRecordsUtil.searchRecords(searchRecordsRequest);
        assertNotNull(searchRecords);
    }

    @Test
    public void searchRecordselse() throws Exception {
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        Map<String, Object> searchResponse=new HashMap<>();
        List<BibItem> bibItems = new ArrayList<>();
        BibItem bibItem = new BibItem();
        bibItem.setBibId(1);
        bibItem.setTitle("Title1");
        bibItem.setAuthorDisplay("Author1");
        bibItem.setBarcode("BC234");
        bibItem.setDocType("Bib");
        bibItem.setImprint("sample imprint");
        List<String> isbnList = new ArrayList<>();
        isbnList.add("978-3-16-148410-0");
        bibItem.setIsbn(isbnList);
        bibItem.setLccn("sample lccn");
        bibItem.setPublicationPlace("Texas");
        bibItem.setPublisher("McGraw Hill");
        bibItem.setPublicationDate("1998");
        bibItem.setSubject("Physics");
        bibItem.setNotes("Notes");
        bibItem.setOwningInstitution("PUL");
        bibItem.setOwningInstitutionBibId("1");
        bibItem.setMaterialType(RecapCommonConstants.SERIAL);
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setItemId(1);
        item.setBarcode("BC234");
        item.setCallNumberSearch("123");
        item.setVolumePartYear("V1");
        item.setCustomerCode("NA");
        item.setAvailability(RecapCommonConstants.NOT_AVAILABLE);
        item.setAvailabilityDisplay(RecapCommonConstants.NOT_AVAILABLE);
        items.add(item);
        Item item1 = new Item();
        item1.setItemId(1);
        item1.setBarcode("BC234");
        item1.setCallNumberSearch("123");
        item1.setVolumePartYear("V1");
        item1.setCustomerCode("NA");
        item1.setAvailability(RecapCommonConstants.NOT_AVAILABLE);
        item1.setAvailabilityDisplay("1");
        items.add(item);
        items.add(item1);
        bibItem.setItems(items);
        bibItems.add(bibItem);
        searchResponse.put(RecapCommonConstants.SEARCH_SUCCESS_RESPONSE,bibItems);
        Mockito.when(bibSolrDocumentRepository.search(Mockito.any(SearchRecordsRequest.class))).thenReturn(searchResponse);
        List<SearchResultRow> searchRecords = searchRecordsUtil.searchRecords(searchRecordsRequest);
        assertNotNull(searchRecords);
    }

    @Test
    public void searchRecordsEmpty() throws Exception {
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        searchRecordsRequest.setAvailability(new ArrayList<>());
        searchRecordsRequest.setMaterialTypes(new ArrayList<>());
        searchRecordsRequest.setCollectionGroupDesignations(new ArrayList<>());
        searchRecordsRequest.setOwningInstitutions(new ArrayList<>());
        searchRecordsRequest.setUseRestrictions(new ArrayList<>());
        List<SearchResultRow> searchRecordsEmpty = searchRecordsUtil.searchRecords(searchRecordsRequest);
        assertNotNull(searchRecordsEmpty);
    }

    @Test
    public void searchRecordsError() throws Exception {
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        Map<String, Object> searchResponse=new HashMap<>();
        searchResponse.put(RecapCommonConstants.SEARCH_ERROR_RESPONSE,"testError");
        Mockito.when(bibSolrDocumentRepository.search(Mockito.any())).thenReturn(searchResponse);
        List<SearchResultRow> searchRecords = searchRecordsUtil.searchRecords(searchRecordsRequest);
        assertNotNull(searchRecords);
    }

    @Test
    public void searchRecordsForDataDump() throws Exception {
        SearchRecordsRequest searchRecordsRequest=new SearchRecordsRequest();
        Map<String, Object> searchResponse=new HashMap<>();
        searchResponse.put(RecapCommonConstants.SEARCH_ERROR_RESPONSE,"testError");
        Mockito.when(searchRecordsUtil.getDataDumpSolrDocumentRepository().search(Mockito.any())).thenReturn(searchResponse);
        List<DataDumpSearchResult> searchRecords = searchRecordsUtil.searchRecordsForDataDump(searchRecordsRequest);
        assertNotNull(searchRecords);
    }


    @Test
    public void testBuildResultsForDataDump(){
        List<DataDumpSearchResult> dataDumpSearchResults = searchRecordsUtil.buildResultsForDataDump(getBibItemList());
        assertNotNull(dataDumpSearchResults);
        assertNotNull(dataDumpSearchResults.get(0).getBibId());
        assertNotNull(dataDumpSearchResults.get(0).getItemIds());
    }

    public List<BibItem> getBibItemList(){
        List<BibItem> bibItems = new ArrayList<>();
        BibItem bibItem = new BibItem();
        bibItem.setBibId(1);
        bibItem.setTitle("Title1");
        bibItem.setAuthorDisplay("Author1");
        bibItem.setBarcode("BC234");
        bibItem.setDocType("Bib");
        bibItem.setImprint("sample imprint");
        List<String> isbnList = new ArrayList<>();
        isbnList.add("978-3-16-148410-0");
        bibItem.setIsbn(isbnList);
        bibItem.setLccn("sample lccn");
        bibItem.setPublicationPlace("Texas");
        bibItem.setPublisher("McGraw Hill");
        bibItem.setPublicationDate("1998");
        bibItem.setSubject("Physics");
        bibItem.setNotes("Notes");
        bibItem.setOwningInstitution("PUL");
        bibItem.setOwningInstitutionBibId("1");
        bibItem.setMaterialType(RecapCommonConstants.OTHER);
        List<Holdings> holdings = new ArrayList<>();
        Holdings holding= new Holdings();
        holding.setId("1");
        holding.setHoldingsId(1);
        holdings.add(holding);
        bibItem.setHoldingsList(holdings);
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setItemId(1);
        item.setBarcode("BC234");
        item.setCallNumberSearch("123");
        item.setVolumePartYear("V1");
        item.setCustomerCode("NA");
        item.setAvailability("Available");
        item.setHoldingsIdList(Arrays.asList(1));
        items.add(item);
        bibItem.setItems(items);
        bibItems.add(bibItem);
        return bibItems;
    }

}