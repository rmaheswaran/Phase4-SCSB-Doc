package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.junit.Test;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.OngoingAccessionReportRecord;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 22/2/17.
 */
public class OngoingAccessionReportGeneratorUT extends BaseTestCase{

    @Test
    public void testOngoingAccessionReportGeneration(){
        OngoingAccessionReportGenerator ongoingAccessionReportGenerator = new OngoingAccessionReportGenerator();
        ReportEntity reportEntity = getReportEntity();
        OngoingAccessionReportRecord ongoingAccessionReportRecord = ongoingAccessionReportGenerator.prepareOngoingAccessionReportRecord(reportEntity);
        assertNotNull(ongoingAccessionReportRecord);
        assertEquals(ongoingAccessionReportRecord.getCustomerCode(),"PB");
        assertEquals(ongoingAccessionReportRecord.getItemBarcode(),"123");
        assertEquals(ongoingAccessionReportRecord.getMessage(),"Test");
        Method getterMethodExp= ongoingAccessionReportGenerator.getGetterMethod("Barcode");
        Method getterMethod= ongoingAccessionReportGenerator.getGetterMethod("itemBarcode");
        Method setterMethodExp= ongoingAccessionReportGenerator.getSetterMethod("Barcode");
        assertNull(getterMethodExp);
        assertNull(setterMethodExp);
        assertNotNull(getterMethod);
    }



    private ReportEntity getReportEntity(){
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapCommonConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ONGOING_ACCESSION_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity successBibCountReportDataEntity = new ReportDataEntity();
        successBibCountReportDataEntity.setHeaderName("customerCode");
        successBibCountReportDataEntity.setHeaderValue("PB");
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity existsBibCountReportDataEntity = new ReportDataEntity();
        existsBibCountReportDataEntity.setHeaderName("itemBarcode");
        existsBibCountReportDataEntity.setHeaderValue("123");
        reportDataEntities.add(existsBibCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = new ReportDataEntity();
        failedBibCountReportDataEntity.setHeaderName("message");
        failedBibCountReportDataEntity.setHeaderValue("Test");
        reportDataEntities.add(failedBibCountReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        return reportEntity;

    }


}