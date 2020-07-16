package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.admin.SolrAdmin;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.HoldingsSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.recap.service.accession.SolrIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by premkb on 2/8/16.
 */
public class SolrIndexControllerUT extends BaseControllerUT{
    private static final Logger logger = LoggerFactory.getLogger(SolrIndexControllerUT.class);

    @InjectMocks
    SolrIndexController solrIndexController=new SolrIndexController();

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @Mock
    SolrIndexService solrIndexService;

    @Mock
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Mock
    BibSolrCrudRepository bibSolrCrudRepository;

    @Mock
    HoldingsSolrCrudRepository holdingsSolrCrudRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    ItemCrudRepository itemCrudRepository;

    @Mock
    SolrAdmin solrAdmin;

    @Before
    public void setUp()throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void solrIndexer()throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get("/")
                .param("model",String.valueOf(model)))
                .andReturn();
        String reponse = mvcResult.getResponse().getContentAsString();
        assertNotNull(reponse);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void fullIndex()throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setDocType("");
        solrIndexRequest.setDoClean(true);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        String response =solrIndexController.fullIndex(solrIndexRequest,bindingResult,model);
        assertNotNull(response);
        assertTrue(response.contains("Total number of records processed :"));
    }

    @Test
    public void fullIndex_Noinst()throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setDocType("");
        solrIndexRequest.setDoClean(true);
        String response =solrIndexController.fullIndex(solrIndexRequest,bindingResult,model);
        assertNotNull(response);
        assertTrue(response.contains("Total number of records processed :"));
    }

    @Test
    public void report()throws Exception{
        String response =solrIndexController.report("");
        assertNotNull(response);
        assertTrue(response.contains("Index process initiated!"));
    }

    @Test
    public void indexByOwningInstBibliographicIdList()throws Exception{
        Map<String,Object> requestParameters = new HashMap<>();
        requestParameters.put(RecapConstants.OWN_INST_BIBID_LIST, "[123/456/789]");
        requestParameters.put(RecapCommonConstants.OWN_INSTITUTION_ID,"1");
        String response =solrIndexController.indexByOwningInstBibliographicIdList(requestParameters);
        assertNotNull(response);
        assertTrue(response.contains(RecapCommonConstants.SUCCESS));
    }

    @Test
    public void partialIndex()throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setDocType("");
        solrIndexRequest.setDoClean(true);
        solrIndexRequest.setOwningInstitutionCode("PUL");
        solrIndexRequest.setCommitInterval(5000);
        String response =solrIndexController.partialIndex(solrIndexRequest,bindingResult,model);
        assertNotNull(response);
        assertTrue(response.contains("Total number of records processed :"));
    }

    @Test
    public void partialIndex_else()throws Exception{
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setDocType("");
        solrIndexRequest.setDoClean(true);
        String response =solrIndexController.partialIndex(solrIndexRequest,bindingResult,model);
        assertNotNull(response);
        assertTrue(response.contains("Total number of records processed :"));
    }

    @Test
    public void indexByBibliographicId()throws Exception{
        List<Integer> bibliographicIdList = new ArrayList<>();
        bibliographicIdList.add(947);
        bibliographicIdList.add(1215);
//        Mockito.when(solrIndexController.getSolrIndexService()).thenReturn(solrIndexService);
        Mockito.when(solrIndexController.getSolrIndexService().indexByBibliographicId(Mockito.any())).thenReturn(null);
        String response =solrIndexController.indexByBibliographicId(bibliographicIdList);
        assertNotNull(response);
        assertTrue(response.contains(RecapCommonConstants.SUCCESS));
    }
   /* @Test
    public void indexByBibliographicId_Exception()throws Exception{
        List<Integer> bibliographicIdList = new ArrayList<>();
        bibliographicIdList.add(947);
        bibliographicIdList.add(1215);
        Mockito.when(solrIndexController.getSolrIndexService().indexByBibliographicId(Mockito.anyInt())).thenReturn(null);
        String response =solrIndexController.indexByBibliographicId(bibliographicIdList);
        assertNotNull(response);
        assertTrue(response.contains(RecapCommonConstants.FAILURE));
    }*/

    private SolrIndexRequest getSolrIndexRequest(){
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setDocType("");
        return solrIndexRequest;
    }

}