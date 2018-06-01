package org.recap.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by akulak on 16/5/17.
 */
@RestController
@RequestMapping("/accessionReconcilationService")
public class AccessionReconcilationRestController {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     *
     * @param barcodesAndCustomerCodes
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    @RequestMapping(method = RequestMethod.POST,value = "/startAccessionReconcilation")
    public Map<String,String> startAccessionReconcilation(@RequestBody Map<String,String> barcodesAndCustomerCodes) throws IOException, SolrServerException {
        SolrClient solrClient = solrTemplate.getSolrClient();
        Set<String> barcodes = barcodesAndCustomerCodes.keySet();
        List<String> barcodesList= new ArrayList<>(barcodes);
        String splittedBarcodes = barcodesList.stream().map(String::trim).collect(Collectors.joining(","));
        Set<String> lasBarcodes = new HashSet<>(Arrays.asList(splittedBarcodes));
        SolrQuery solrQuery = getSolrQuery(splittedBarcodes, splittedBarcodes.length());
        QueryResponse queryResponse = solrClient.query(solrQuery, SolrRequest.METHOD.POST);
        if (lasBarcodes.size() != queryResponse.getFieldStatsInfo().get(RecapConstants.BARCODE).getCountDistinct()){
            getDifference(lasBarcodes, queryResponse,barcodesAndCustomerCodes);
        }
        return barcodesAndCustomerCodes;
    }

    /**
     *
     * @param lasBarcodes
     * @param queryResponse
     * @param barcodesAndCustomerCodes
     * @return
     */
    private Map<String, String> getDifference(Set<String> lasBarcodes, QueryResponse queryResponse, Map<String, String> barcodesAndCustomerCodes) {
        for (Object barcode : queryResponse.getFieldStatsInfo().get(RecapConstants.BARCODE).getDistinctValues()) {
            barcodesAndCustomerCodes.entrySet().removeIf(p -> p.getKey().contains(barcode.toString()));
        }
        return barcodesAndCustomerCodes;
    }

    /**
     * This method is used to build the solr query for the given barcode.
     * @param barcode
     * @param rows
     * @return
     */
    private SolrQuery getSolrQuery(String barcode,int rows) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(RecapConstants.DOC_TYPE_ITEM);
        solrQuery.setRows(rows);
        solrQuery.addFilterQuery(RecapConstants.BARCODE+":"+ StringEscapeUtils.escapeJava(barcode).replace(",","\" \""));
        solrQuery.setFields(RecapConstants.BARCODE);
        solrQuery.setGetFieldStatistics(true);
        solrQuery.setGetFieldStatistics(RecapConstants.BARCODE);
        solrQuery.addStatsFieldCalcDistinct(RecapConstants.BARCODE, true);
        return solrQuery;
    }

}

