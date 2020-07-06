package org.recap.model.search.resolver.impl.bib;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.model.solr.BibItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Anithav on 16/06/20.
 */
public class BibResolverUT extends BaseTestCase {

    @Test
    public void testIsDeletedBibValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.IsDeletedBibValueResolver isDeletedBibValueResolver = new IsDeletedBibValueResolver();
        isDeletedBibValueResolver.setValue(bibItem(),true);
        isDeletedBibValueResolver.isInterested(RecapCommonConstants.IS_DELETED_BIB);
        assertNotNull(isDeletedBibValueResolver.getClass());
    }

    @Test
    public void testLeaderMaterialTypeValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.LeaderMaterialTypeValueResolver leaderMaterialTypeValueResolver = new LeaderMaterialTypeValueResolver();
        leaderMaterialTypeValueResolver.setValue(bibItem(),"test");
        leaderMaterialTypeValueResolver.isInterested("LeaderMaterialType");
        assertNotNull(leaderMaterialTypeValueResolver.getClass());
    }
    @Test
    public void testDocTypeValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.DocTypeValueResolver docTypeValueResolver = new DocTypeValueResolver();
        docTypeValueResolver.setValue(bibItem(),"test");
        docTypeValueResolver.isInterested("DocType");
        assertNotNull(docTypeValueResolver.getClass());
    }
    @Test
    public void testAuthorSearchValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.AuthorSearchValueResolver authorSearchValueResolver = new org.recap.model.search.resolver.impl.bib.AuthorSearchValueResolver();
        List value = new ArrayList();
        value.add("test");
        authorSearchValueResolver.setValue(bibItem(),value);
        authorSearchValueResolver.isInterested("Author_search");
        assertNotNull(authorSearchValueResolver.getClass());
    }
    @Test
    public void testOwningInstitutionValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.OwningInstitutionValueResolver owningInstitutionValueResolver = new OwningInstitutionValueResolver();
        owningInstitutionValueResolver.setValue(bibItem(),"test");
        owningInstitutionValueResolver.isInterested("BibOwningInstitution");
        assertNotNull(owningInstitutionValueResolver.getClass());
    }
    @Test
    public void testImprintValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.ImprintValueResolver imprintValueResolver = new ImprintValueResolver();
        imprintValueResolver.setValue(bibItem(),"test");
        imprintValueResolver.isInterested("Imprint");
        assertNotNull(imprintValueResolver.getClass());
    }
    @Test
    public void testBibLastUpdatedByValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.BibLastUpdatedByValueResolver bibLastUpdatedByValueResolver = new BibLastUpdatedByValueResolver();
        bibLastUpdatedByValueResolver.setValue(bibItem(),"test");
        bibLastUpdatedByValueResolver.isInterested("BibLastUpdatedBy");
        assertNotNull(bibLastUpdatedByValueResolver.getClass());
    }
    @Test
    public void testPublisherValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.PublisherValueResolver publisherValueResolver = new PublisherValueResolver();
        publisherValueResolver.setValue(bibItem(),"test");
        publisherValueResolver.isInterested("Publisher");
        assertNotNull(publisherValueResolver.getClass());
    }
    @Test
    public void testBibLastUpdatedDateValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.BibLastUpdatedDateValueResolver bibLastUpdatedDateValueResolver = new BibLastUpdatedDateValueResolver();
        Date date= new Date();
        bibLastUpdatedDateValueResolver.setValue(bibItem(),date);
        bibLastUpdatedDateValueResolver.isInterested("BibLastUpdatedDate");
        assertNotNull(bibLastUpdatedDateValueResolver.getClass());
    }
    @Test
    public void testNotesValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.NotesValueResolver notesValueResolver = new NotesValueResolver();
        notesValueResolver.setValue(bibItem(),"test");
        notesValueResolver.isInterested("notes");
        assertNotNull(notesValueResolver.getClass());
    }
    @Test
    public void testBibCreatedByValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.BibCreatedByValueResolver bibCreatedByValueResolver = new BibCreatedByValueResolver();
        bibCreatedByValueResolver.setValue(bibItem(),"test");
        bibCreatedByValueResolver.isInterested("BibCreatedBy");
        assertNotNull(bibCreatedByValueResolver.getClass());
    }
    @Test
    public void testPublicationDateValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.PublicationDateValueResolver publicationDateValueResolver = new PublicationDateValueResolver();
        publicationDateValueResolver.setValue(bibItem(),"test");
        publicationDateValueResolver.isInterested("PublicationDate");
        assertNotNull(publicationDateValueResolver.getClass());
    }
    @Test
    public void testTitleSearchValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.TitleSearchValueResolver titleSearchValueResolver = new TitleSearchValueResolver();
        titleSearchValueResolver.setValue(bibItem(),"test");
        titleSearchValueResolver.isInterested("Title_search");
        assertNotNull(titleSearchValueResolver.getClass());
    }
    @Test
    public void testTitleSortValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.TitleSortValueResolver titleSortValueResolver = new TitleSortValueResolver();
        titleSortValueResolver.setValue(bibItem(),"test");
        titleSortValueResolver.isInterested("Title_sort");
        assertNotNull(titleSortValueResolver.getClass());
    }
    @Test
    public void testLCCNValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.LCCNValueResolver LCCNValueResolver = new LCCNValueResolver();
        LCCNValueResolver.setValue(bibItem(),"test");
        LCCNValueResolver.isInterested("LCCN");
        assertNotNull(LCCNValueResolver.getClass());
    }
    @Test
    public void testIdValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.IdValueResolver idValueResolver = new org.recap.model.search.resolver.impl.bib.IdValueResolver();
        idValueResolver.setValue(bibItem(),"test");
        idValueResolver.isInterested("id");
        assertNotNull(idValueResolver.getClass());
    }
    @Test
    public void testOwningInstitutionBibIdValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.OwningInstitutionBibIdValueResolver owningInstitutionBibIdValueResolver = new OwningInstitutionBibIdValueResolver();
        owningInstitutionBibIdValueResolver.setValue(bibItem(),"test");
        owningInstitutionBibIdValueResolver.isInterested(RecapCommonConstants.OWNING_INSTITUTION_BIB_ID);
        assertNotNull(owningInstitutionBibIdValueResolver.getClass());
    }
    @Test
    public void testBibIdValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.BibIdValueResolver bibIdValueResolver = new org.recap.model.search.resolver.impl.bib.BibIdValueResolver();
        bibIdValueResolver.setValue(bibItem(),1);
        bibIdValueResolver.isInterested("BibId");
        assertNotNull(bibIdValueResolver.getClass());
    }
    @Test
    public void testISBNValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.ISBNValueResolver ISBNValueResolver = new ISBNValueResolver();
        List<String> value = new ArrayList<String>();
        value.add("test");
        ISBNValueResolver.setValue(bibItem(),value);
        ISBNValueResolver.isInterested("ISBN");
        assertNotNull(ISBNValueResolver.getClass());
    }
    @Test
    public void testTitleDisplayValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.TitleDisplayValueResolver titleDisplayValueResolver = new TitleDisplayValueResolver();
        titleDisplayValueResolver.setValue(bibItem(),"test");
        titleDisplayValueResolver.isInterested("Title_display");
        assertNotNull(titleDisplayValueResolver.getClass());
    }
    @Test
    public void testAuthorDisplayValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.AuthorDisplayValueResolver authorDisplayValueResolver = new AuthorDisplayValueResolver();
        authorDisplayValueResolver.setValue(bibItem(),"test");
        authorDisplayValueResolver.isInterested("Author_display");
        assertNotNull(authorDisplayValueResolver.getClass());
    }
    @Test
    public void testBibCreatedDateValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.BibCreatedDateValueResolver bibCreatedDateValueResolver = new BibCreatedDateValueResolver();
        Date date= new Date();
        bibCreatedDateValueResolver.setValue(bibItem(),date);
        bibCreatedDateValueResolver.isInterested("BibCreatedDate");
        assertNotNull(bibCreatedDateValueResolver.getClass());
    }
    @Test
    public void testSubjectValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.SubjectValueResolver subjectValueResolver = new SubjectValueResolver();
        subjectValueResolver.setValue(bibItem(),"test");
        subjectValueResolver.isInterested("Subject");
        assertNotNull(subjectValueResolver.getClass());
    }
    @Test
    public void testOCLCValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.OCLCValueResolver OCLCValueResolver = new OCLCValueResolver();
        List<String> value = new ArrayList<String>();
        value.add("test");
        OCLCValueResolver.setValue(bibItem(),value);
        OCLCValueResolver.isInterested("OCLCNumber");
        assertNotNull(OCLCValueResolver.getClass());
    }
    @Test
    public void testMaterialTypeValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.MaterialTypeValueResolver materialTypeValueResolver = new MaterialTypeValueResolver();
        materialTypeValueResolver.setValue(bibItem(),"test");
        materialTypeValueResolver.isInterested("MaterialType");
        assertNotNull(materialTypeValueResolver.getClass());
    }
    @Test
    public void testPublicationPlaceValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.PublicationPlaceValueResolver publicationPlaceValueResolver = new PublicationPlaceValueResolver();
        publicationPlaceValueResolver.setValue(bibItem(),"test");
        publicationPlaceValueResolver.isInterested("PublicationPlace");
        assertNotNull(publicationPlaceValueResolver.getClass());
    }
    @Test
    public void testRootValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.RootValueResolver rootValueResolver = new org.recap.model.search.resolver.impl.bib.RootValueResolver();
        rootValueResolver.setValue(bibItem(),"test");
        rootValueResolver.isInterested("_root_");
        assertNotNull(rootValueResolver.getClass());
    }
    @Test
    public void testISSNValueResolver() throws Exception {
        org.recap.model.search.resolver.impl.bib.ISSNValueResolver ISSNValueResolver = new ISSNValueResolver();
        List<String> value = new ArrayList<String>();
        value.add("test");
        ISSNValueResolver.setValue(bibItem(),value);
        ISSNValueResolver.isInterested("ISSN");
        assertNotNull(ISSNValueResolver.getClass());
    }

    /*@Test
    public void testTitleSubFieldAValueResolver() throws Exception {
        TitleSubFieldAValueResolver titleSubFieldAValueResolver = new TitleSubFieldAValueResolver();
        titleSubFieldAValueResolver.setValue(bibItem(),"test");
        titleSubFieldAValueResolver.isInterested("Title_subfield_a");
        assertNotNull(titleSubFieldAValueResolver.getClass());
    }*/


    public BibItem bibItem() throws Exception{
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
        return bibItem;

    }

}
