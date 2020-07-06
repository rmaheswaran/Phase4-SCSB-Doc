package org.recap.model.solr;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
@Ignore
public class HoldingsAT extends BaseTestCase {

    @Value("${solr.parent.core}")
    String solrCore;

    @Before
    public void setUp() throws Exception {
        assertNotNull(holdingsSolrCrudRepository);
        holdingsSolrCrudRepository.deleteAll();
    }

    @Test
    public void indexHoldings() throws Exception {
        Holdings holdings = new Holdings();
        holdings.setHoldingsId(1001);
        holdings.setDocType("Holdings");
        holdings.setRoot("Holdings");
        holdings.setId("1");
        holdings.setHoldingsCreatedBy("1");
        holdings.setHoldingsCreatedDate(new Date());
        holdings.setHoldingsLastUpdatedBy("1");
        holdings.setHoldingsLastUpdatedDate(new Date());
        holdings.setDeletedHoldings(true);
        holdings.setSummaryHoldings("Test Summary Holdings Info");
        holdings.setOwningInstitution("NYPL");
        holdings.setOwningInstitutionHoldingsId("1");

        Holdings indexedHoldings = holdingsSolrCrudRepository.save(holdings);
        solrTemplate.softCommit(solrCore);

        Holdings fetchedHoldings = holdingsSolrCrudRepository.findByHoldingsId(indexedHoldings.getHoldingsId());
        assertNotNull(fetchedHoldings);
        assertNotNull(holdings.getRoot());
        assertNotNull(holdings.getId());
        assertNotNull(holdings.getHoldingsCreatedBy());
        assertNotNull(holdings.getHoldingsCreatedDate());
        assertNotNull(holdings.getHoldingsLastUpdatedBy());
        assertNotNull(holdings.getHoldingsLastUpdatedDate());
        assertNotNull(holdings.isDeletedHoldings());
        assertNotNull(holdings.getOwningInstitutionHoldingsId());
        assertEquals(fetchedHoldings.getHoldingsId(), new Integer(1001));
        assertEquals(fetchedHoldings.getDocType(), "Holdings");
        assertEquals(fetchedHoldings.getSummaryHoldings(), "Test Summary Holdings Info");
        assertEquals(fetchedHoldings.getOwningInstitution(), "NYPL");
    }
}
