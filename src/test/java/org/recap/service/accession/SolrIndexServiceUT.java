package org.recap.service.accession;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.service.accession.SolrIndexService;
import org.recap.util.BibJSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by rajeshbabuk on 10/11/16.
 */
public class SolrIndexServiceUT extends BaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(SolrIndexServiceUT.class);


    @Mock
    SolrIndexService solrIndexService;

    @Mock
    BibSolrCrudRepository mockedBibSolrCrudRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    SolrTemplate mockedSolrTemplate;

    @Mock
    BibliographicDetailsRepository mockedBibliographicDetailsRepository;

    @Mock
    HoldingsDetailsRepository mockedHoldingsDetailsRepository;

    @Mock
    BibJSONUtil mockedBibJSONUtil;

    @Resource(name = "recapSolrTemplate")
    private SolrTemplate solrTemplate;

    @Value("${solr.parent.core}")
    String solrCore;


    public SolrIndexService getSolrIndexService() {
        return solrIndexService;
    }

    public BibSolrCrudRepository getMockedBibSolrCrudRepository() {
        return mockedBibSolrCrudRepository;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public SolrTemplate getSolrTemplate() {
        return mockedSolrTemplate;
    }

    public BibliographicDetailsRepository getBibliographicDetailsRepository() {
        return mockedBibliographicDetailsRepository;
    }

    public HoldingsDetailsRepository getHoldingsDetailsRepository() {
        return mockedHoldingsDetailsRepository;
    }


    @Test
    public void indexByBibliographicId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        Integer bibliographicId = savedBibliographicEntity.getBibliographicId();
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        ReflectionTestUtils.setField(solrIndexService,"solrCore","recap");
        Mockito.when(solrIndexService.getSolrTemplate()).thenReturn(mockedSolrTemplate);
        Mockito.when(solrIndexService.getBibJSONUtil()).thenReturn(mockedBibJSONUtil);
        Mockito.when(solrIndexService.getProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(solrIndexService.getBibliographicDetailsRepository()).thenReturn(mockedBibliographicDetailsRepository);
        Mockito.when(solrIndexService.getHoldingsDetailsRepository()).thenReturn(mockedHoldingsDetailsRepository);
        Mockito.when(solrIndexService.getBibliographicDetailsRepository().findByBibliographicId(bibliographicId)).thenReturn(savedBibliographicEntity);
        Mockito.when(solrIndexService.indexBibliographicEntity(savedBibliographicEntity)).thenCallRealMethod();
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.setField("id", "123");
        solrInputDocument.setField("Title_search", "Title1");
        solrInputDocument.setField("Author_search", "Author1");
        Mockito.when(solrIndexService.getBibJSONUtil().generateBibAndItemsForIndex(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(solrInputDocument);
        Mockito.when(solrIndexService.indexByBibliographicId(bibliographicId)).thenCallRealMethod();
        SolrInputDocument solrInputDocument1 = solrIndexService.indexByBibliographicId(bibliographicId);
        assertNotNull(solrInputDocument1);
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(solrIndexService.getBibJSONUtil()).thenCallRealMethod();
        Mockito.when(solrIndexService.getBibliographicDetailsRepository()).thenCallRealMethod();
        Mockito.when(solrIndexService.getHoldingsDetailsRepository()).thenCallRealMethod();
        Mockito.when(solrIndexService.getProducerTemplate()).thenCallRealMethod();
        Mockito.when(solrIndexService.getSolrTemplate()).thenCallRealMethod();
        Mockito.when(solrIndexService.getLogger()).thenCallRealMethod();
        assertNotEquals(mockedBibJSONUtil,solrIndexService.getBibJSONUtil());
        assertNotEquals(mockedBibliographicDetailsRepository,solrIndexService.getBibliographicDetailsRepository());
        assertNotEquals(mockedHoldingsDetailsRepository,solrIndexService.getHoldingsDetailsRepository());
        assertNotEquals(mockedSolrTemplate,solrIndexService.getSolrTemplate());
        assertNotEquals(producerTemplate,solrIndexService.getProducerTemplate());
        assertNotEquals(logger,solrIndexService.getLogger());
    }

   public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        UpdateResponse updateResponse = mockedSolrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        mockedSolrTemplate.commit(solrCore);
    }

    @Test
    public void indexByOwnInstBibId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        List<BibliographicEntity> bibliographicEntityList =new ArrayList<>();
        bibliographicEntityList.add(bibliographicEntity);
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        Integer owningInstitutionId = savedBibliographicEntity.getOwningInstitutionId();
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        List<String> owningInstBibIdList=new ArrayList<>();
        Integer bibliographicId = savedBibliographicEntity.getBibliographicId();
        owningInstBibIdList.add("777");
        ReflectionTestUtils.setField(solrIndexService,"solrCore","recap");
        ReflectionTestUtils.setField(solrIndexService,"submitCollectionOwnInstBibIdListPartitionSize",5000);
        ReflectionTestUtils.setField(solrIndexService,"bibliographicDetailsRepository",mockedBibliographicDetailsRepository);
        Mockito.when(solrIndexService.getSolrTemplate()).thenReturn(mockedSolrTemplate);
        Mockito.when(solrIndexService.getBibJSONUtil()).thenReturn(mockedBibJSONUtil);
        Mockito.when(solrIndexService.getProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(solrIndexService.getBibliographicDetailsRepository()).thenReturn(mockedBibliographicDetailsRepository);
        Mockito.when(solrIndexService.getHoldingsDetailsRepository()).thenReturn(mockedHoldingsDetailsRepository);
        Mockito.when(solrIndexService.indexBibliographicEntity(savedBibliographicEntity)).thenCallRealMethod();
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.setField("id", "123");
        solrInputDocument.setField("Title_search", "Title1");
        solrInputDocument.setField("Author_search", "Author1");
        Mockito.when(solrIndexService.getBibliographicIdForIndexing(owningInstBibIdList,owningInstitutionId)).thenCallRealMethod();
        Mockito.when(solrIndexService.getBibliographicDetailsRepository().findByOwningInstitutionBibIdInAndOwningInstitutionId(Mockito.anyList(),Mockito.anyInt())).thenReturn(bibliographicEntityList);
        Mockito.doCallRealMethod().when(solrIndexService).indexByOwnInstBibId(owningInstBibIdList,owningInstitutionId);
        solrIndexService.indexByOwnInstBibId(owningInstBibIdList,owningInstitutionId);
    }

    @Test
    public void deleteByDocId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        Integer bibliographicId = savedBibliographicEntity.getBibliographicId();
        solrIndexService.indexByBibliographicId(bibliographicId);
        Mockito.when(mockedBibSolrCrudRepository.findByBibId(bibliographicId)).thenReturn(new Bib());
        Bib bib = mockedBibSolrCrudRepository.findByBibId(bibliographicId);
        assertNotNull(bib);
        solrIndexService.deleteByDocId("BibId",String.valueOf(bibliographicId));
        Mockito.when(mockedBibSolrCrudRepository.findByBibId(bibliographicId)).thenReturn(null);
        Bib bib1 = mockedBibSolrCrudRepository.findByBibId(bibliographicId);
        assertNull(bib1);

    }

    public BibliographicEntity getBibEntityWithHoldingsAndItem() throws Exception {
        Random random = new Random();
        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("777");
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setBibliographicId(333);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(String.valueOf(random.nextInt()));
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        //itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("PUL-BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("PUL-HoldingsContent.xml");
        return new File(resource.toURI());
    }
}
