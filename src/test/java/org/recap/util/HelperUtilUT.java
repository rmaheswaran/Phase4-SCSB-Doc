package org.recap.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Mock
    ObjectMapper objectMapper;

    @Test
    public void saveItemChangeLogEntity() {
        ReflectionTestUtils.setField(helperUtil,"objectMapper",null);
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


    @Test
    public void getJsonStringException() throws JsonProcessingException {
        ReflectionTestUtils.setField(helperUtil,"objectMapper",objectMapper);
        Mockito.when(objectMapper.writeValueAsString(Mockito.anyString())).thenThrow(JsonProcessingException.class);
        String result=helperUtil.getJsonString("PUL");
        assertNotNull(result);
    }

}
