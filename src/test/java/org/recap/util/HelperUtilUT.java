package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class HelperUtilUT extends BaseTestCaseUT {

    @InjectMocks
    HelperUtil helperUtil;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Test
    public void saveItemChangeLogEntity() {
        List< ItemEntity > itemEntityList=new ArrayList<>();
        ItemEntity itemEntity=new ItemEntity();
        itemEntityList.add(itemEntity);
        helperUtil.saveItemChangeLogEntity("","",itemEntityList);
        String result=helperUtil.getJsonString("");
        assertNotNull(result);
    }

    @Test
    public void saveReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportDataEntity titleReportDataEntity = new ReportDataEntity();
        titleReportDataEntity.setHeaderName(RecapCommonConstants.TITLE);
        titleReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(titleReportDataEntity);
        helperUtil.saveReportEntity("PUL","test", RecapConstants.SUBMIT_COLLECTION_SUMMARY_REPORT,reportDataEntities);
        assertNotNull(titleReportDataEntity);
    }

}
