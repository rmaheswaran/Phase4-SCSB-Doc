package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.model.request.ReplaceRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;


public class ResubmitRequestControllerUT extends BaseControllerUT {

    @InjectMocks
    ResubmitRequestController resubmitRequestController;


    @Value("${scsb.url}")
    private String scsbUrl;


    @Test
    public void resubmitRequestsFailure() throws Exception {
        ReplaceRequest replaceRequest=new ReplaceRequest();
        ReflectionTestUtils.setField(resubmitRequestController,"scsbUrl",scsbUrl);
        String responseMessage=resubmitRequestController.resubmitRequests(replaceRequest);
        assertNotNull(responseMessage);
    }
}
