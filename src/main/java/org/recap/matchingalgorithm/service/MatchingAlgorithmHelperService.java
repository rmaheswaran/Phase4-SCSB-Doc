package org.recap.matchingalgorithm.service;

import com.google.common.collect.Lists;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.executors.SaveMatchingBibsCallable;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.service.ActiveMqQueuesInfo;
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


/**
 * Created by angelind on 11/7/16.
 */
@Service
public class MatchingAlgorithmHelperService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmHelperService.class);

    @Autowired
    private MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    private MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    private MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    @Resource(name = "recapSolrTemplate")
    private SolrTemplate solrTemplate;

    @Autowired
    private ProducerTemplate producerTemplate;

    private ExecutorService executorService;

    @Autowired
    private ActiveMqQueuesInfo activeMqQueuesInfo;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets matching bib details repository.
     *
     * @return the matching bib details repository
     */
    public MatchingBibDetailsRepository getMatchingBibDetailsRepository() {
        return matchingBibDetailsRepository;
    }

    /**
     * Gets matching match points details repository.
     *
     * @return the matching match points details repository
     */
    public MatchingMatchPointsDetailsRepository getMatchingMatchPointsDetailsRepository() {
        return matchingMatchPointsDetailsRepository;
    }

    /**
     * Gets matching algorithm util.
     *
     * @return the matching algorithm util
     */
    public MatchingAlgorithmUtil getMatchingAlgorithmUtil() {
        return matchingAlgorithmUtil;
    }

    /**
     * Gets solr query builder.
     *
     * @return the solr query builder
     */
    public SolrQueryBuilder getSolrQueryBuilder() {
        return solrQueryBuilder;
    }

    /**
     * Gets solr template.
     *
     * @return the solr template
     */
    public SolrTemplate getSolrTemplate() {
        return solrTemplate;
    }

    /**
     * Gets producer template.
     *
     * @return the producer template
     */
    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public ActiveMqQueuesInfo getActiveMqQueuesInfo() {
        return activeMqQueuesInfo;
    }

    /**
     * This method finds the matching records based on the match point field(OCLC,ISBN,ISSN,LCCN).
     *
     * @return the long
     * @throws Exception the exception
     */
    public long findMatchingAndPopulateMatchPointsEntities()  {
        List<String> matchingMatchPoints = RecapConstants.MATCHING_MATCH_POINTS;
        long count = matchingMatchPoints
                .stream()
                .mapToLong(this::loadAndSaveMatchingMatchPointEntities)
                .sum();
        logger.info("Total count in MatchPoints : {} " , count);
        drainAllQueueMsgs("saveMatchingMatchPointsQ");

        return count;
    }

    private long loadAndSaveMatchingMatchPointEntities(String matchPointFieldOclc) {
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities = null;
        try {
            matchingMatchPointsEntities = getMatchingAlgorithmUtil().getMatchingMatchPointsEntity(matchPointFieldOclc);
            getMatchingAlgorithmUtil().saveMatchingMatchPointEntities(matchingMatchPointsEntities);
        } catch (Exception exception) {
            logger.info("Exception in finding MatchPoints : {}",exception.getMessage());
        }
        return matchingMatchPointsEntities.size();
    }

    /**
     * This method is used to populate matching bib records in the database.
     *
     * @return the long
     * @throws IOException         the io exception
     * @throws SolrServerException the solr server exception
     */
    public long populateMatchingBibEntities() {
        long count = RecapConstants.MATCHING_MATCH_POINTS.stream().mapToLong(this::fetchAndSaveMatchingBibs).sum();
        drainAllQueueMsgs("saveMatchingBibsQ");
        return count;
    }

    private void drainAllQueueMsgs(String queueName) {
        Integer saveMatchingBibsQ = getActiveMqQueuesInfo().getActivemqQueuesInfo(queueName);
        if(saveMatchingBibsQ != null) {
            while (saveMatchingBibsQ != 0) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                saveMatchingBibsQ = getActiveMqQueuesInfo().getActivemqQueuesInfo(queueName);
            }
        }
    }
    /**
     * This method is used to populate reports for oclc and isbn matching combination based on the given batch size.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForOCLCandISBN(Integer batchSize,Map<String, Integer> institutionCounterMap) {

        List<Integer> multiMatchBibIdsForOCLCAndISBN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForOclcAndIsbn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISBN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        buildBibIdAndBibEntityMap(multipleMatchBibIds, oclcAndBibIdMap, bibEntityMap, getLogger(), RecapCommonConstants.MATCH_POINT_FIELD_OCLC, RecapCommonConstants.MATCH_POINT_FIELD_ISBN);

        Set<String> oclcNumberSet = new HashSet<>();
        for (Iterator<String> iterator = oclcAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String oclc = iterator.next();
            if(!oclcNumberSet.contains(oclc)) {
                StringBuilder oclcNumbers  = new StringBuilder();
                StringBuilder isbns = new StringBuilder();
                oclcNumberSet.add(oclc);
                Set<Integer> bibIds = oclcAndBibIdMap.get(oclc);
                Set<Integer> tempBibIds = new HashSet<>(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    oclcNumbers.append(StringUtils.isNotBlank(oclcNumbers.toString()) ? "," : "").append(matchingBibEntity.getOclc());
                    isbns.append(StringUtils.isNotBlank(isbns.toString()) ? "," : "").append(matchingBibEntity.getIsbn());
                    String[] oclcList = oclcNumbers.toString().split(",");
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapCommonConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapCommonConstants.OCLC_CRITERIA, RecapCommonConstants.ISBN_CRITERIA, oclcNumbers.toString(), isbns.toString(),institutionCounterMap);
            }
        }
        return institutionCounterMap;
    }

    /**
     * This method is used to populate reports for oclc and issn combination.
     *
     * @param batchSize the batch size
     * @param institutionCounterMap
     * @return the map
     */
    public Map<String,Integer> populateReportsForOCLCAndISSN(Integer batchSize, Map<String, Integer> institutionCounterMap) {
        List<Integer> multiMatchBibIdsForOCLCAndISSN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForOclcAndIssn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISSN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        buildBibIdAndBibEntityMap(multipleMatchBibIds, oclcAndBibIdMap, bibEntityMap, logger, RecapCommonConstants.MATCH_POINT_FIELD_OCLC, RecapCommonConstants.MATCH_POINT_FIELD_ISSN);

        Set<String> oclcNumberSet = new HashSet<>();
        for (Iterator<String> iterator = oclcAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String oclc = iterator.next();
            if(!oclcNumberSet.contains(oclc)) {
                StringBuilder oclcNumbers  = new StringBuilder();
                StringBuilder issns = new StringBuilder();
                oclcNumberSet.add(oclc);
                Set<Integer> bibIds = oclcAndBibIdMap.get(oclc);
                Set<Integer> tempBibIds = new HashSet<>(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    oclcNumbers.append(StringUtils.isNotBlank(oclcNumbers.toString()) ? "," : "").append(matchingBibEntity.getOclc());
                    issns.append(StringUtils.isNotBlank(issns.toString()) ? "," : "").append(matchingBibEntity.getIssn());
                    String[] oclcList = oclcNumbers.toString().split(",");
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapCommonConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapCommonConstants.OCLC_CRITERIA, RecapCommonConstants.ISSN_CRITERIA, oclcNumbers.toString(), issns.toString(),institutionCounterMap);
            }
        }
        return institutionCounterMap;
    }

    /**
     * This method is used to populate reports for oclc and lccn combination.
     *
     * @param batchSize the batch size
     * @param institutionCounterMap
     * @return the map
     */
    public Map<String,Integer> populateReportsForOCLCAndLCCN(Integer batchSize, Map<String, Integer> institutionCounterMap) {
        List<Integer> multiMatchBibIdsForOCLCAndLCCN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForOclcAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndLCCN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        buildBibIdAndBibEntityMap(multipleMatchBibIds, oclcAndBibIdMap, bibEntityMap, logger, RecapCommonConstants.MATCH_POINT_FIELD_OCLC, RecapCommonConstants.MATCH_POINT_FIELD_LCCN);

        Set<String> oclcNumberSet = new HashSet<>();
        for (Iterator<String> iterator = oclcAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String oclc = iterator.next();
            if(!oclcNumberSet.contains(oclc)) {
                StringBuilder oclcNumbers  = new StringBuilder();
                StringBuilder lccns = new StringBuilder();
                oclcNumberSet.add(oclc);
                Set<Integer> bibIds = oclcAndBibIdMap.get(oclc);
                Set<Integer> tempBibIds = new HashSet<>(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    oclcNumbers.append(StringUtils.isNotBlank(oclcNumbers.toString()) ? "," : "").append(matchingBibEntity.getOclc());
                    lccns.append(StringUtils.isNotBlank(lccns.toString()) ? "," : "").append(matchingBibEntity.getLccn());
                    String[] oclcList = oclcNumbers.toString().split(",");
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapCommonConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapCommonConstants.OCLC_CRITERIA, RecapCommonConstants.LCCN_CRITERIA, oclcNumbers.toString(), lccns.toString(),institutionCounterMap);
            }
        }
        return institutionCounterMap;
    }

    /**
     * This method is used to populate reports for isbn and issn combination.
     *
     * @param batchSize the batch size
     * @param institutionCounterMap
     * @return the map
     */
    public Map<String,Integer> populateReportsForISBNAndISSN(Integer batchSize, Map<String, Integer> institutionCounterMap) {
        List<Integer> multiMatchBibIdsForISBNAndISSN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForIsbnAndIssn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndISSN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        populateBibIds(isbnAndBibIdMap, bibEntityMap,multipleMatchBibIds,  RecapCommonConstants.MATCH_POINT_FIELD_ISSN);

        Set<String> isbnSet = new HashSet<>();
        for (Iterator<String> iterator = isbnAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String isbn = iterator.next();
            if(!isbnSet.contains(isbn)) {
                StringBuilder isbns  = new StringBuilder();
                StringBuilder issns = new StringBuilder();
                isbnSet.add(isbn);
                Set<Integer> bibIds = isbnAndBibIdMap.get(isbn);
                Set<Integer> tempBibIds = new HashSet<>(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    isbns.append(StringUtils.isNotBlank(isbns.toString()) ? "," : "").append(matchingBibEntity.getIsbn());
                    issns.append(StringUtils.isNotBlank(issns.toString()) ? "," : "").append(matchingBibEntity.getIssn());
                    String[] isbnList = isbns.toString().split(",");
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapCommonConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapCommonConstants.ISBN_CRITERIA, RecapCommonConstants.ISSN_CRITERIA, isbns.toString(), issns.toString(),institutionCounterMap);
            }
        }
        return institutionCounterMap;
    }

    /**
     * This method is used to populate reports for isbn and lccn combination.
     *
     * @param batchSize the batch size
     * @param institutionCounterMap
     * @return the map
     */
    public Map<String,Integer> populateReportsForISBNAndLCCN(Integer batchSize, Map<String, Integer> institutionCounterMap) {
        List<Integer> multiMatchBibIdsForISBNAndLCCN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForIsbnAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndLCCN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        populateBibIds(isbnAndBibIdMap, bibEntityMap,multipleMatchBibIds,  RecapCommonConstants.MATCH_POINT_FIELD_LCCN);

        Set<String> isbnSet = new HashSet<>();
        for (Iterator<String> iterator = isbnAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String isbn = iterator.next();
            if(!isbnSet.contains(isbn)) {
                StringBuilder isbns  = new StringBuilder();
                StringBuilder lccns = new StringBuilder();
                isbnSet.add(isbn);
                Set<Integer> bibIds = isbnAndBibIdMap.get(isbn);
                Set<Integer> tempBibIds = new HashSet<>(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    isbns.append(StringUtils.isNotBlank(isbns.toString()) ? "," : "").append(matchingBibEntity.getIsbn());
                    lccns.append(StringUtils.isNotBlank(lccns.toString()) ? "," : "").append(matchingBibEntity.getLccn());
                    String[] isbnList = isbns.toString().split(",");
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapCommonConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapCommonConstants.ISBN_CRITERIA, RecapCommonConstants.LCCN_CRITERIA, isbns.toString(), lccns.toString(),institutionCounterMap);
            }
        }
        return institutionCounterMap;
    }

    /**
     * This method is used to populate reports for issn and lccn combination.
     *
     * @param batchSize the batch size
     * @param institutionCounterMap
     * @return the map
     */
    public Map<String,Integer> populateReportsForISSNAndLCCN(Integer batchSize, Map<String, Integer> institutionCounterMap) {
        List<Integer> multiMatchBibIdsForISSNAndLCCN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForIssnAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISSNAndLCCN, batchSize);
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        buildBibIdAndBibEntityMap(multipleMatchBibIds, issnAndBibIdMap, bibEntityMap, logger, RecapCommonConstants.MATCH_POINT_FIELD_ISSN, RecapCommonConstants.MATCH_POINT_FIELD_LCCN);

        Set<String> issnSet = new HashSet<>();
        for (Iterator<String> iterator = issnAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String issn = iterator.next();
            if(!issnSet.contains(issn)) {
                StringBuilder issns  = new StringBuilder();
                StringBuilder lccns = new StringBuilder();
                issnSet.add(issn);
                Set<Integer> bibIds = issnAndBibIdMap.get(issn);
                Set<Integer> tempBibIds = new HashSet<>(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    issns.append(StringUtils.isNotBlank(issns.toString()) ? "," : "").append(matchingBibEntity.getIssn());
                    lccns.append(StringUtils.isNotBlank(lccns.toString()) ? "," : "").append(matchingBibEntity.getLccn());
                    String[] issnList = issns.toString().split(",");
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(issnAndBibIdMap, issnSet, issn, RecapCommonConstants.MATCH_POINT_FIELD_ISSN, issnList, bibEntityMap, issns));
                }
                getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapCommonConstants.ISSN_CRITERIA, RecapCommonConstants.LCCN_CRITERIA, issns.toString(), lccns.toString(),institutionCounterMap);
            }
        }
        return institutionCounterMap;
    }

    /**
     * This method is used to populate reports for single match.
     *
     * @param batchSize the batch size
     * @param institutionCounterMap
     * @return the map
     */
    public Map<String,Integer> populateReportsForSingleMatch(Integer batchSize, Map<String, Integer> institutionCounterMap) {
        getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapCommonConstants.MATCH_POINT_FIELD_OCLC,institutionCounterMap);
        getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapCommonConstants.MATCH_POINT_FIELD_ISBN, institutionCounterMap);
        getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapCommonConstants.MATCH_POINT_FIELD_ISSN, institutionCounterMap);
        getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapCommonConstants.MATCH_POINT_FIELD_LCCN, institutionCounterMap);
        Integer saveMatchingBibsQ = getActiveMqQueuesInfo().getActivemqQueuesInfo("updateMatchingBibEntityQ");
        if(saveMatchingBibsQ != null) {
            while (saveMatchingBibsQ != 0) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                saveMatchingBibsQ = getActiveMqQueuesInfo().getActivemqQueuesInfo("updateMatchingBibEntityQ");
            }
        }
        populateReportsForPendingMatches(batchSize,institutionCounterMap);
        return institutionCounterMap;
    }

    /**
     * Populate reports for pending matches map.
     *
     * @param batchSize the batch size
     * @param institutionCounterMap
     * @return the map
     */
    public Map<String,Integer> populateReportsForPendingMatches(Integer batchSize, Map<String, Integer> institutionCounterMap) {

        Page<MatchingBibEntity> matchingBibEntities = getMatchingBibDetailsRepository().findByStatus(PageRequest.of(0, batchSize), RecapConstants.PENDING);
        int totalPages = matchingBibEntities.getTotalPages();
        List<MatchingBibEntity> matchingBibEntityList = matchingBibEntities.getContent();
        Set<Integer> matchingBibIds = new HashSet<>();
        getMatchingAlgorithmUtil().processPendingMatchingBibs(matchingBibEntityList, matchingBibIds,institutionCounterMap);
        for(int pageNum=1; pageNum < totalPages; pageNum++) {
            matchingBibEntities = getMatchingBibDetailsRepository().findByStatus(PageRequest.of(pageNum, batchSize), RecapConstants.PENDING);
            matchingBibEntityList = matchingBibEntities.getContent();
            getMatchingAlgorithmUtil().processPendingMatchingBibs(matchingBibEntityList, matchingBibIds, institutionCounterMap);
        }

        getMatchingBibDetailsRepository().updateStatus(RecapCommonConstants.COMPLETE_STATUS, RecapConstants.PENDING);
        return institutionCounterMap;
    }

    /**
     * This method is used to save matching summary count.
     *
     * @param institutionCounterMap  institutionsMatchingCount
     */
    public void saveMatchingSummaryCount(Map<String, Integer> institutionCounterMap) {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setType("MatchingCount");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setFileName("MatchingSummaryCount");
        reportEntity.setInstitutionName(RecapCommonConstants.LCCN_CRITERIA);
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();


        List<String> allInstitutionCodeExceptHTC = institutionDetailsRepository.findAllInstitutionCodeExceptHTC();
        for (String institution : allInstitutionCodeExceptHTC) {
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(institution.toLowerCase()+"MatchingCount");
            reportDataEntity.setHeaderValue(String.valueOf(institutionCounterMap.get(institution)));
            reportDataEntities.add(reportDataEntity);
        }
        reportEntity.addAll(reportDataEntities);
        getProducerTemplate().sendBody("scsbactivemq:queue:saveMatchingReportsQ", Collections.singletonList(reportEntity));
    }

    /**
     * This method is used to fetch and save matching bibs.
     *
     * @param matchCriteria the match criteria
     * @return the integer
     */
    public Integer fetchAndSaveMatchingBibs(String matchCriteria) {
        long batchSize = 300;
        Integer size = 0;
        try {
            long countBasedOnCriteria = getMatchingMatchPointsDetailsRepository().countBasedOnCriteria(matchCriteria);
            SaveMatchingBibsCallable saveMatchingBibsCallable = new SaveMatchingBibsCallable();
            saveMatchingBibsCallable.setBibIdList(new HashSet<>());
            int totalPagesCount = (int) (countBasedOnCriteria / batchSize);
            ExecutorService executor = getExecutorService(50);
            List<Callable<Integer>> callables = new ArrayList<>();
            for (int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
                Callable callable = new SaveMatchingBibsCallable(getMatchingMatchPointsDetailsRepository(), matchCriteria, getSolrTemplate(),
                        getProducerTemplate(), getSolrQueryBuilder(), batchSize, pageNum, getMatchingAlgorithmUtil());
                callables.add(callable);
            }
            size = executeCallables(size, executor, callables);
        }
        catch (Exception exception){
            logger.info("Exception caught in saving Matching Bibs : {}",exception.getMessage());
        }
        return size;
    }

    private Integer executeCallables(Integer size, ExecutorService executorService, List<Callable<Integer>> callables) {
        List<Future<Integer>> futures = null;
        try {
            futures = getFutures(executorService, callables);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }

        if(futures != null) {
            for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
                Future future = iterator.next();
                try {
                    size += (Integer) future.get();
                } catch (InterruptedException e) {
                    logger.error(RecapCommonConstants.LOG_ERROR,e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    logger.error(RecapCommonConstants.LOG_ERROR,e);
                }
            }
        }
        return size;
    }

    private List<Future<Integer>> getFutures(ExecutorService executorService, List<Callable<Integer>> callables) throws InterruptedException {
        List<Future<Integer>> futures = executorService.invokeAll(callables);
        List<Future<Integer>> collectedFutures = futures.stream().map(future -> {
            try {
                future.get();
                return future;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).collect(Collectors.toList());
        logger.info("No of Futures Collected : {}", collectedFutures.size());
        return collectedFutures;
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }

    private void populateBibIds( Map<String, Set<Integer>> isbnAndBibIdMap ,  Map<Integer, MatchingBibEntity> bibEntityMap, List<List<Integer>> multipleMatchBibIds, String matchPoint) {
        buildBibIdAndBibEntityMap(multipleMatchBibIds, isbnAndBibIdMap, bibEntityMap, logger, RecapCommonConstants.MATCH_POINT_FIELD_ISBN, matchPoint);
    }

    private void buildBibIdAndBibEntityMap(List<List<Integer>> multipleMatchBibIds, Map<String, Set<Integer>> oclcAndBibIdMap, Map<Integer, MatchingBibEntity> bibEntityMap, Logger logger, String matchPointFieldOclc, String matchPointFieldLccn) {
        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST, multipleMatchBibIds.size());
        for (List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = getMatchingBibDetailsRepository().getMultiMatchBibEntitiesBasedOnBibIds(bibIds, matchPointFieldOclc, matchPointFieldLccn);
            if (CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                getMatchingAlgorithmUtil().populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, matchPointFieldOclc, bibEntityMap);
            }
        }
    }
}
