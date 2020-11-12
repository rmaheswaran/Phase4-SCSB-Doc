package org.recap.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

public class ActiveMqQueuesInfoUT extends BaseTestCaseUT {

    @InjectMocks
    ActiveMqQueuesInfo activeMqQueuesInfo;

    @Value("${activemq.jolokia.api.url}")
    private String ACTIVE_MQ_API_URL;

    @Value("${activemq.jolokia.api.queue.size.attribute}")
    private String SEARCH_ATTRIBUTE;

    @Value("${activemq.web.console.url}")
    private String serviceUrl;

    @Value("${activemq.credentials}")
    private String activemqCredentials;

    @Test
    public void getActivemqQueuesInfo(){
        ReflectionTestUtils.setField(activeMqQueuesInfo,"serviceUrl",serviceUrl);
        ReflectionTestUtils.setField(activeMqQueuesInfo,"activemqCredentials",activemqCredentials);
        ReflectionTestUtils.setField(activeMqQueuesInfo,"ACTIVE_MQ_API_URL",ACTIVE_MQ_API_URL);
        ReflectionTestUtils.setField(activeMqQueuesInfo,"SEARCH_ATTRIBUTE",SEARCH_ATTRIBUTE);
        int queueSizeCount= activeMqQueuesInfo.getActivemqQueuesInfo("test");
        assertEquals(0,queueSizeCount);
    }
}
