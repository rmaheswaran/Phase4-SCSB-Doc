package org.recap.util;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.IncompleteReportBibDetails;
import org.recap.model.jpa.DeaccessionItemChangeLog;
import org.recap.model.reports.ReportsInstitutionForm;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.DeaccesionItemChangeLogDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
@Service
public class ReportsServiceUtil {

    @Resource(name = "recapSolrTemplate")
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    @Autowired
    private DeaccesionItemChangeLogDetailsRepository deaccesionItemChangeLogDetailsRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private DateUtil dateUtil;


    /**
     * This method populates accession and deaccession item counts from solr for report screen in UI.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
    public ReportsResponse populateAccessionDeaccessionItemCounts(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setReportsInstitutionFormList(new ArrayList<>());
        String solrFormattedDate = getSolrFormattedDates(reportsRequest.getAccessionDeaccessionFromDate(), reportsRequest.getAccessionDeaccessionToDate());
        populateAccessionCounts(reportsRequest, reportsResponse, solrFormattedDate);
        populateDeaccessionCounts(reportsRequest, reportsResponse, solrFormattedDate);
        return reportsResponse;
    }

    /**
     * This method populates cgd item counts from solr for report screen in UI.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
    public ReportsResponse populateCgdItemCounts(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setReportsInstitutionFormList(new ArrayList<>());
        for (String owningInstitution : reportsRequest.getOwningInstitutions()) {
            ReportsInstitutionForm reportsInstitutionForm = new ReportsInstitutionForm();
            reportsInstitutionForm.setInstitution(owningInstitution);
            for (String collectionGroupDesignation : reportsRequest.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForCGDReports(owningInstitution, collectionGroupDesignation);
                long numFound = getNumFound(query);
                if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_OPEN)) {
                    reportsInstitutionForm.setOpenCgdCount(numFound);
                } else if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_SHARED)) {
                    reportsInstitutionForm.setSharedCgdCount(numFound);
                } else if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_PRIVATE)) {
                    reportsInstitutionForm.setPrivateCgdCount(numFound);
                }
            }
            reportsResponse.getReportsInstitutionFormList().add(reportsInstitutionForm);
        }
        return reportsResponse;
    }

    private long getNumFound(SolrQuery query) throws SolrServerException, IOException {
        query.setRows(0);
        query.setGetFieldStatistics(true);
        query.setGetFieldStatistics(RecapConstants.DISTINCT_VALUES_FALSE);
        query.addStatsFieldCalcDistinct(RecapCommonConstants.BARCODE, true);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
        return queryResponse.getFieldStatsInfo().get(RecapCommonConstants.BARCODE).getCountDistinct();
    }

    /**
     * This method gets deaccession information results from solr and populate them in report screen (UI).
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
    public ReportsResponse populateDeaccessionResults(ReportsRequest reportsRequest) throws Exception {
        List<Item> itemList = new ArrayList<>();
        List<Integer> itemIdList = new ArrayList<>();
        List<Integer> bibIdList = new ArrayList<>();
        ReportsResponse reportsResponse = new ReportsResponse();
        String date = getSolrFormattedDates(reportsRequest.getAccessionDeaccessionFromDate(), reportsRequest.getAccessionDeaccessionToDate());
        SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccesionReportInformation(date, reportsRequest.getDeaccessionOwningInstitution(), true);
        query.setRows(reportsRequest.getPageSize());
        query.setStart(reportsRequest.getPageNumber() * reportsRequest.getPageSize());
        query.set(RecapConstants.GROUP, true);
        query.set(RecapConstants.GROUP_FIELD, RecapCommonConstants.BARCODE);
        query.setGetFieldStatistics(true);
        query.setGetFieldStatistics(RecapConstants.DISTINCT_VALUES_FALSE);
        query.addStatsFieldCalcDistinct(RecapCommonConstants.BARCODE, true);
        query.setSort(RecapConstants.ITEM_LAST_UPDATED_DATE, SolrQuery.ORDER.desc);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(query);
        List<GroupCommand> values = queryResponse.getGroupResponse().getValues();
        for (GroupCommand groupCommand : values) {
            List<Group> groupList = groupCommand.getValues();
            for (Group group : groupList) {
                SolrDocumentList result = group.getResult();
                for (SolrDocument solrDocument : result) {
                    boolean isDeletedItem = (boolean) solrDocument.getFieldValue(RecapCommonConstants.IS_DELETED_ITEM);
                    if (isDeletedItem) {
                        Item item = commonUtil.getItem(solrDocument);
                        itemList.add(item);
                        itemIdList.add(item.getItemId());
                        bibIdList.add(item.getItemBibIdList().get(0));
                    }
                }

            }
        }
        long numFound = queryResponse.getFieldStatsInfo().get(RecapCommonConstants.BARCODE).getCountDistinct();
        reportsResponse.setTotalRecordsCount(String.valueOf(numFound));
        int totalPagesCount = (int) Math.ceil((double) numFound / (double) reportsRequest.getPageSize());
        if (totalPagesCount == 0) {
            reportsResponse.setTotalPageCount(1);
        } else {
            reportsResponse.setTotalPageCount(totalPagesCount);
        }
        String bibIdJoin = StringUtils.join(bibIdList, ",");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(RecapConstants.BIB_DOC_TYPE);
        solrQuery.addFilterQuery(RecapConstants.SOLR_BIB_ID + StringEscapeUtils.escapeJava(bibIdJoin).replace(",", "\" \""));
        solrQuery.setFields(RecapCommonConstants.BIB_ID, RecapConstants.TITLE_DISPLAY);
        solrQuery.setRows(Integer.MAX_VALUE);
        QueryResponse response = solrTemplate.getSolrClient().query(solrQuery);
        Map<Integer, String> map = new HashMap<>();
        SolrDocumentList list = response.getResults();
        for (Iterator<SolrDocument> iterator = list.iterator(); iterator.hasNext(); ) {
            SolrDocument solrDocument = iterator.next();
            map.put((Integer) solrDocument.getFieldValue(RecapCommonConstants.BIB_ID), (String) solrDocument.getFieldValue(RecapConstants.TITLE_DISPLAY));
        }
        SimpleDateFormat simpleDateFormat = getSimpleDateFormatForReports();
        List<DeaccessionItemResultsRow> deaccessionItemResultsRowList = new ArrayList<>();
        for (Item item : itemList) {
            DeaccessionItemResultsRow deaccessionItemResultsRow = new DeaccessionItemResultsRow();
            deaccessionItemResultsRow.setItemId(item.getItemId());
            String deaccessionDate = simpleDateFormat.format(item.getItemLastUpdatedDate());
            if (map.containsKey(item.getItemBibIdList().get(0))) {
                deaccessionItemResultsRow.setTitle(map.get(item.getItemBibIdList().get(0)));
            }
            deaccessionItemResultsRow.setDeaccessionDate(deaccessionDate);
            deaccessionItemResultsRow.setDeaccessionOwnInst(item.getOwningInstitution());
            deaccessionItemResultsRow.setItemBarcode(item.getBarcode());
            List<DeaccessionItemChangeLog> itemChangeLogEntityList = deaccesionItemChangeLogDetailsRepository.findByRecordIdAndOperationTypeAndOrderByUpdatedDateDesc(item.getItemId(), RecapCommonConstants.REPORTS_DEACCESSION);
            if (CollectionUtils.isNotEmpty(itemChangeLogEntityList)) {
                DeaccessionItemChangeLog itemChangeLogEntity = itemChangeLogEntityList.get(0);
                deaccessionItemResultsRow.setDeaccessionNotes(itemChangeLogEntity.getNotes());
            }
            deaccessionItemResultsRow.setDeaccessionCreatedBy(item.getItemLastUpdatedBy());
            deaccessionItemResultsRow.setCgd(item.getCollectionGroupDesignation());
            deaccessionItemResultsRowList.add(deaccessionItemResultsRow);
        }
        reportsResponse.setDeaccessionItemResultsRows(deaccessionItemResultsRowList);
        return reportsResponse;
    }

    /**
     * This method is used to populate incomplete records report.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     * @throws Exception the exception
     */
    public ReportsResponse populateIncompleteRecordsReport(ReportsRequest reportsRequest) throws Exception {
        ReportsResponse reportsResponse = new ReportsResponse();
        List<Integer> bibIdList = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        SolrQuery solrQuery;
        QueryResponse queryResponse;
        solrQuery = solrQueryBuilder.buildSolrQueryForIncompleteReports(reportsRequest.getIncompleteRequestingInstitution());
        if (!reportsRequest.isExport()) {
            solrQuery.setStart(reportsRequest.getIncompletePageSize() * reportsRequest.getIncompletePageNumber());
            solrQuery.setRows(reportsRequest.getIncompletePageSize());
        }
        solrQuery.set(RecapConstants.GROUP, true);
        solrQuery.set(RecapConstants.GROUP_FIELD, RecapCommonConstants.BARCODE);
        solrQuery.setGetFieldStatistics(true);
        solrQuery.setGetFieldStatistics(RecapConstants.DISTINCT_VALUES_FALSE);
        solrQuery.addStatsFieldCalcDistinct(RecapCommonConstants.BARCODE, true);
        solrQuery.setSort(RecapConstants.ITEM_CREATED_DATE, SolrQuery.ORDER.desc);
        queryResponse = solrTemplate.getSolrClient().query(solrQuery);
        long numFound = queryResponse.getFieldStatsInfo().get(RecapCommonConstants.BARCODE).getCountDistinct();
        if (reportsRequest.isExport()) {
            solrQuery = solrQueryBuilder.buildSolrQueryForIncompleteReports(reportsRequest.getIncompleteRequestingInstitution());
            solrQuery.setStart(0);
            solrQuery.setRows((int) numFound);
            solrQuery.set(RecapConstants.GROUP, true);
            solrQuery.set(RecapConstants.GROUP_FIELD, RecapCommonConstants.BARCODE);
            solrQuery.setGetFieldStatistics(true);
            solrQuery.setGetFieldStatistics(RecapConstants.DISTINCT_VALUES_FALSE);
            solrQuery.addStatsFieldCalcDistinct(RecapCommonConstants.BARCODE, true);
            solrQuery.setSort(RecapConstants.ITEM_CREATED_DATE, SolrQuery.ORDER.desc);
            queryResponse = solrTemplate.getSolrClient().query(solrQuery);
        }

        List<GroupCommand> values = queryResponse.getGroupResponse().getValues();
        for (GroupCommand groupCommand : values) {
            List<Group> groupList = groupCommand.getValues();
            for (Group group : groupList) {
                SolrDocumentList result = group.getResult();
                for (SolrDocument itemDocument : result) {
                    Item item = commonUtil.getItem(itemDocument);
                    itemList.add(item);
                    bibIdList.add(item.getItemBibIdList().get(0));
                }
            }

        }
        if (bibIdList.size() > 0) {
            Map<Integer, IncompleteReportBibDetails> bibDetailsMap = new HashMap<>();
            List<List<Integer>> partionedBibIdList = Lists.partition(bibIdList, 1000);
            for (List<Integer> bibIds : partionedBibIdList) {
                bibDetailsMap = getBibDetailsIncompleteReport(bibIds, bibDetailsMap);
            }
            List<IncompleteReportResultsRow> incompleteReportResultsRows = new ArrayList<>();
            for (Item item : itemList) {
                IncompleteReportResultsRow incompleteReportResultsRow = new IncompleteReportResultsRow();
                incompleteReportResultsRow.setOwningInstitution(item.getOwningInstitution());
                IncompleteReportBibDetails incompleteReportBibDetails = bibDetailsMap.get(item.getItemBibIdList().get(0));
                if (incompleteReportBibDetails != null) {
                    incompleteReportResultsRow.setTitle(incompleteReportBibDetails.getTitle());
                    incompleteReportResultsRow.setAuthor(incompleteReportBibDetails.getAuthorDisplay());
                }
                incompleteReportResultsRow.setCreatedDate(getFormattedDates(item.getItemCreatedDate()));
                incompleteReportResultsRow.setCustomerCode(item.getCustomerCode());
                incompleteReportResultsRow.setBarcode(item.getBarcode());
                incompleteReportResultsRows.add(incompleteReportResultsRow);
            }
            int totalPagesCount = (int) Math.ceil((double) numFound / (double) reportsRequest.getIncompletePageSize());
            reportsResponse.setIncompleteTotalPageCount(totalPagesCount);
            reportsResponse.setIncompleteTotalRecordsCount(String.valueOf(numFound));
            reportsResponse.setIncompleteReportResultsRows(incompleteReportResultsRows);
        }
        return reportsResponse;
    }

    private Map<Integer, IncompleteReportBibDetails> getBibDetailsIncompleteReport(List<Integer> bibIdList, Map<Integer, IncompleteReportBibDetails> bibDetailsMap) throws SolrServerException, IOException {
        SolrQuery bibDetailsQuery = solrQueryBuilder.buildSolrQueryToGetBibDetails(bibIdList, Integer.MAX_VALUE);
        QueryResponse bibDetailsResponse = solrTemplate.getSolrClient().query(bibDetailsQuery, SolrRequest.METHOD.POST);
        SolrDocumentList bibDocumentList = bibDetailsResponse.getResults();
        for (SolrDocument bibDetail : bibDocumentList) {
            IncompleteReportBibDetails incompleteReportBibDetails = new IncompleteReportBibDetails();
            incompleteReportBibDetails.setTitle((String) bibDetail.getFieldValue(RecapConstants.TITLE_DISPLAY));
            incompleteReportBibDetails.setAuthorDisplay((String) bibDetail.getFieldValue(RecapConstants.AUTHOR_DISPLAY));
            bibDetailsMap.put((Integer) bibDetail.getFieldValue(RecapCommonConstants.BIB_ID), incompleteReportBibDetails);
        }
        return bibDetailsMap;
    }

    private String getFormattedDates(Date gotDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapCommonConstants.SIMPLE_DATE_FORMAT_REPORTS);
        return simpleDateFormat.format(gotDate);

    }


    /**
     * This method gets the accession count from solr
     * @param reportsRequest
     * @param reportsResponse
     * @param solrFormattedDate
     * @throws Exception
     */
    private void populateAccessionCounts(ReportsRequest reportsRequest, ReportsResponse reportsResponse, String solrFormattedDate) throws Exception {
        for (String owningInstitution : reportsRequest.getOwningInstitutions()) {
            ReportsInstitutionForm reportsInstitutionForm = getReportInstitutionFormByInstitution(owningInstitution, reportsResponse.getReportsInstitutionFormList());
            reportsInstitutionForm.setInstitution(owningInstitution);
            for (String collectionGroupDesignation : reportsRequest.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForAccessionReports(solrFormattedDate, owningInstitution, false, collectionGroupDesignation);
                long numFound = getNumFound(query);
                reportsInstitutionForm.setAccessionPrivateCount(numFound);
                if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_OPEN)) {
                    reportsInstitutionForm.setAccessionOpenCount(numFound);
                } else if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_SHARED)) {
                    reportsInstitutionForm.setAccessionSharedCount(numFound);
                } else if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_PRIVATE)) {
                    reportsInstitutionForm.setAccessionPrivateCount(numFound);
                }
            }
            reportsResponse.getReportsInstitutionFormList().add(reportsInstitutionForm);
        }
    }

    /**
     * This method gets the deaccession count from the solr
     * @param reportsRequest
     * @param reportsResponse
     * @param solrFormattedDate
     * @throws Exception
     */
    private void populateDeaccessionCounts(ReportsRequest reportsRequest, ReportsResponse reportsResponse, String solrFormattedDate) throws Exception {
        for (String owningInstitution : reportsRequest.getOwningInstitutions()) {
            ReportsInstitutionForm reportsInstitutionForm = getReportInstitutionFormByInstitution(owningInstitution, reportsResponse.getReportsInstitutionFormList());
            reportsInstitutionForm.setInstitution(owningInstitution);
            for (String collectionGroupDesignation : reportsRequest.getCollectionGroupDesignations()) {
                SolrQuery query = solrQueryBuilder.buildSolrQueryForDeaccessionReports(solrFormattedDate, owningInstitution, true, collectionGroupDesignation);
                long numFound = getNumFound(query);
                if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_OPEN)) {
                    reportsInstitutionForm.setDeaccessionOpenCount(numFound);
                } else if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_SHARED)) {
                    reportsInstitutionForm.setDeaccessionSharedCount(numFound);
                } else if (collectionGroupDesignation.equalsIgnoreCase(RecapCommonConstants.REPORTS_PRIVATE)) {
                    reportsInstitutionForm.setDeaccessionPrivateCount(numFound);
                }
            }
        }
    }

    /**
     * This mehtod will return the form for matched owning institution or creates new form and returns it.
     * @param owningInstitution
     * @param reportsInstitutionFormList
     * @return
     */
    private ReportsInstitutionForm getReportInstitutionFormByInstitution(String owningInstitution, List<ReportsInstitutionForm> reportsInstitutionFormList) {
        if (!reportsInstitutionFormList.isEmpty()) {
            for (ReportsInstitutionForm reportsOwningInstitutionForm : reportsInstitutionFormList) {
                if (StringUtils.isNotBlank(owningInstitution) && owningInstitution.equalsIgnoreCase(reportsOwningInstitutionForm.getInstitution())) {
                    return reportsOwningInstitutionForm;
                }
            }
        }
        return new ReportsInstitutionForm();
    }

    private String getSolrFormattedDates(String requestedFromDate, String requestedToDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = getSimpleDateFormatForReports();
        Date fromDate = simpleDateFormat.parse(requestedFromDate);
        Date toDate = simpleDateFormat.parse(requestedToDate);
        Date fromDateTime = dateUtil.getFromDate(fromDate);
        Date toDateTime = dateUtil.getToDate(toDate);
        String formattedFromDate = getFormattedDateString(fromDateTime);
        String formattedToDate = getFormattedDateString(toDateTime);
        return formattedFromDate + " TO " + formattedToDate;
    }

    private SimpleDateFormat getSimpleDateFormatForReports() {
        return new SimpleDateFormat(RecapCommonConstants.SIMPLE_DATE_FORMAT_REPORTS);
    }

    private String getFormattedDateString(Date inputDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapCommonConstants.DATE_FORMAT_YYYYMMDDHHMM);
        String utcStr;
        String dateString = simpleDateFormat.format(inputDate);
        Date date = simpleDateFormat.parse(dateString);
        DateFormat format = new SimpleDateFormat(RecapCommonConstants.UTC_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(RecapCommonConstants.UTC));
        utcStr = format.format(date);
        return utcStr;
    }
}
