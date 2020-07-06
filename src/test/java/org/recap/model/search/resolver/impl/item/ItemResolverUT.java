package org.recap.model.search.resolver.impl.item;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.solr.Item;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
/**
 * Created by Anithav on 16/06/20.
 */

public class ItemResolverUT extends BaseTestCase {
    Item item = new Item();
    Date date = new Date();

    @Test
    public void testIsDeletedItemValueResolver() throws Exception {
        IsDeletedItemValueResolver isDeletedItemValueResolver = new IsDeletedItemValueResolver();
        isDeletedItemValueResolver.setValue(item,true);
        isDeletedItemValueResolver.isInterested(RecapCommonConstants.IS_DELETED_ITEM);
        assertNotNull(isDeletedItemValueResolver.getClass());
    }
    @Test
    public void testCollectionGroupDesignationValueResolver() throws Exception {
        CollectionGroupDesignationValueResolver CollectionGroupDesignationValueResolver = new CollectionGroupDesignationValueResolver();
        CollectionGroupDesignationValueResolver.setValue(item,"test");
        CollectionGroupDesignationValueResolver.isInterested("CollectionGroupDesignation");
        assertNotNull(CollectionGroupDesignationValueResolver.getClass());
    }
    @Test
    public void testCustomerCodeValueResolver() throws Exception {
        CustomerCodeValueResolver CustomerCodeValueResolver = new CustomerCodeValueResolver();
        CustomerCodeValueResolver.setValue(item,"test");
        CustomerCodeValueResolver.isInterested("CustomerCode");
        assertNotNull(CustomerCodeValueResolver.getClass());
    }
    @Test
    public void testDocTypeValueResolver() throws Exception {
        DocTypeValueResolver DocTypeValueResolver = new DocTypeValueResolver();
        DocTypeValueResolver.setValue(item,"true");
        DocTypeValueResolver.isInterested("DocType");
        assertNotNull(DocTypeValueResolver.getClass());
    }
    @Test
    public void testItemOwningInstitutionValueResolver() throws Exception {
        ItemOwningInstitutionValueResolver ItemOwningInstitutionValueResolver = new ItemOwningInstitutionValueResolver();
        ItemOwningInstitutionValueResolver.setValue(item,"true");
        ItemOwningInstitutionValueResolver.isInterested("ItemOwningInstitution");
        assertNotNull(ItemOwningInstitutionValueResolver.getClass());
    }
    @Test
    public void testItemCreatedDateValueResolver() throws Exception {
        ItemCreatedDateValueResolver ItemCreatedDateValueResolver = new ItemCreatedDateValueResolver();
        ItemCreatedDateValueResolver.setValue(item,date);
        ItemCreatedDateValueResolver.isInterested("ItemCreatedDate");
        assertNotNull(ItemCreatedDateValueResolver.getClass());
    }
    @Test
    public void testItemIdValueResolver() throws Exception {
        ItemIdValueResolver ItemIdValueResolver = new ItemIdValueResolver();
        ItemIdValueResolver.setValue(item,1);
        ItemIdValueResolver.isInterested("ItemId");
        assertNotNull(ItemIdValueResolver.getClass());
    }
    @Test
    public void testItemLastUpdatedByValueResolver() throws Exception {
        ItemLastUpdatedByValueResolver ItemLastUpdatedByValueResolver = new ItemLastUpdatedByValueResolver();
        ItemLastUpdatedByValueResolver.setValue(item,"true");
        ItemLastUpdatedByValueResolver.isInterested("ItemLastUpdatedBy");
        assertNotNull(ItemLastUpdatedByValueResolver.getClass());
    }
    @Test
    public void testItemLastUpdatedDateValueResolver() throws Exception {
        ItemLastUpdatedDateValueResolver ItemLastUpdatedDateValueResolver = new ItemLastUpdatedDateValueResolver();
        ItemLastUpdatedDateValueResolver.setValue(item,date);
        ItemLastUpdatedDateValueResolver.isInterested("ItemLastUpdatedDate");
        assertNotNull(ItemLastUpdatedDateValueResolver.getClass());
    }
    @Test
    public void testHoldingsIdsValueResolver() throws Exception {
        HoldingsIdsValueResolver HoldingsIdsValueResolver = new HoldingsIdsValueResolver();
        HoldingsIdsValueResolver.setValue(item, Arrays.asList(1));
        HoldingsIdsValueResolver.isInterested(RecapCommonConstants.HOLDINGS_ID);
        assertNotNull(HoldingsIdsValueResolver.getClass());
    }
    @Test
    public void testItemCreatedByValueResolver() throws Exception {
        ItemCreatedByValueResolver ItemCreatedByValueResolver = new ItemCreatedByValueResolver();
        ItemCreatedByValueResolver.setValue(item,"true");
        ItemCreatedByValueResolver.isInterested("ItemCreatedBy");
        assertNotNull(ItemCreatedByValueResolver.getClass());
    }
    @Test
    public void testVolumePartYearValueResolver() throws Exception {
        VolumePartYearValueResolver VolumePartYearValueResolver = new VolumePartYearValueResolver();
        VolumePartYearValueResolver.setValue(item,"true");
        VolumePartYearValueResolver.isInterested("VolumePartYear");
        assertNotNull(VolumePartYearValueResolver.getClass());
    }
    @Test
    public void testUseRestrictionDisplayValueResolver() throws Exception {
        UseRestrictionDisplayValueResolver UseRestrictionDisplayValueResolver = new UseRestrictionDisplayValueResolver();
        UseRestrictionDisplayValueResolver.setValue(item,"true");
        UseRestrictionDisplayValueResolver.isInterested("UseRestriction_display");
        assertNotNull(UseRestrictionDisplayValueResolver.getClass());
    }
    @Test
    public void testItemBibIdValueResolver() throws Exception {
        ItemBibIdValueResolver ItemBibIdValueResolver = new ItemBibIdValueResolver();
        ItemBibIdValueResolver.setValue(item,Arrays.asList(1));
        ItemBibIdValueResolver.isInterested("ItemBibId");
        assertNotNull(ItemBibIdValueResolver.getClass());
    }
    @Test
    public void testCallNumberSearchValueResolver() throws Exception {
        CallNumberSearchValueResolver CallNumberSearchValueResolver = new CallNumberSearchValueResolver();
        CallNumberSearchValueResolver.setValue(item,"true");
        CallNumberSearchValueResolver.isInterested("CallNumber_search");
        assertNotNull(CallNumberSearchValueResolver.getClass());
    }
    @Test
    public void testOwningInstitutionItemIdValueResolver() throws Exception {
        OwningInstitutionItemIdValueResolver OwningInstitutionItemIdValueResolver = new OwningInstitutionItemIdValueResolver();
        OwningInstitutionItemIdValueResolver.setValue(item,"true");
        OwningInstitutionItemIdValueResolver.isInterested(RecapConstants.OWNING_INSTITUTION_ITEM_ID);
        assertNotNull(OwningInstitutionItemIdValueResolver.getClass());
    }
    @Test
    public void testCallNumberDisplayValueResolver() throws Exception {
        CallNumberDisplayValueResolver CallNumberDisplayValueResolver = new CallNumberDisplayValueResolver();
        CallNumberDisplayValueResolver.setValue(item,"true");
        CallNumberDisplayValueResolver.isInterested("CallNumber_display");
        assertNotNull(CallNumberDisplayValueResolver.getClass());
    }
    @Test
    public void testBarcodeValueResolver() throws Exception {
        BarcodeValueResolver BarcodeValueResolver = new BarcodeValueResolver();
        BarcodeValueResolver.setValue(item,"true");
        BarcodeValueResolver.isInterested("Barcode");
        assertNotNull(BarcodeValueResolver.getClass());
    }
    @Test
    public void testAvailabilityDisplayValueResolverr() throws Exception {
        AvailabilityDisplayValueResolver AvailabilityDisplayValueResolver = new AvailabilityDisplayValueResolver();
        AvailabilityDisplayValueResolver.setValue(item,"true");
        AvailabilityDisplayValueResolver.isInterested("Availability_display");
        assertNotNull(AvailabilityDisplayValueResolver.getClass());
    }
    @Test
    public void testIdValueResolver() throws Exception {
        IdValueResolver IdValueResolver = new IdValueResolver();
        IdValueResolver.setValue(item,"true");
        IdValueResolver.isInterested("id");
        assertNotNull(IdValueResolver.getClass());
    }
    @Test
    public void testItemRootValueResolver() throws Exception {
        ItemRootValueResolver ItemRootValueResolver = new ItemRootValueResolver();
        ItemRootValueResolver.setValue(item,"true");
        ItemRootValueResolver.isInterested("_root_");
        assertNotNull(ItemRootValueResolver.getClass());
    }
    @Test
    public void testUseRestrictionSearchValueResolver() throws Exception {
        UseRestrictionSearchValueResolver UseRestrictionSearchValueResolver = new UseRestrictionSearchValueResolver();
        UseRestrictionSearchValueResolver.setValue(item,"true");
        UseRestrictionSearchValueResolver.isInterested("UseRestriction_search");
        assertNotNull(UseRestrictionSearchValueResolver.getClass());
    }
    @Test
    public void testAvailabilitySearchValueResolver() throws Exception {
        AvailabilitySearchValueResolver AvailabilitySearchValueResolver = new AvailabilitySearchValueResolver();
        AvailabilitySearchValueResolver.setValue(item,"true");
        AvailabilitySearchValueResolver.isInterested("Availability_search");
        assertNotNull(AvailabilitySearchValueResolver.getClass());
    }

}
