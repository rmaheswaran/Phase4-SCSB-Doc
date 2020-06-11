package org.recap.model.matchingReports;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 4/7/17.
 */
public class TitleExceptionReportUT extends BaseTestCase{

    @Test
    public void testTitleExceptionReport(){
        TitleExceptionReport titleExceptionReport = new TitleExceptionReport();
        titleExceptionReport.setOwningInstitution("PUL");
        titleExceptionReport.setBibId("1235");
        titleExceptionReport.setOwningInstitutionBibId("AD4526523563");
        titleExceptionReport.setMaterialType("Monograph");
        titleExceptionReport.setOclc("1225");
        titleExceptionReport.setIsbn("4565");
        titleExceptionReport.setIssn("1236");
        titleExceptionReport.setLccn("7412");
        titleExceptionReport.setTitleList(Arrays.asList("Test"));

        assertNotNull(titleExceptionReport.getOwningInstitution());
        assertNotNull(titleExceptionReport.getBibId());
        assertNotNull(titleExceptionReport.getOwningInstitutionBibId());
        assertNotNull(titleExceptionReport.getMaterialType());
        assertNotNull(titleExceptionReport.getOclc());
        assertNotNull(titleExceptionReport.getIsbn());
        assertNotNull(titleExceptionReport.getIssn());
        assertNotNull(titleExceptionReport.getLccn());
        assertNotNull(titleExceptionReport.getTitleList());
    }

}