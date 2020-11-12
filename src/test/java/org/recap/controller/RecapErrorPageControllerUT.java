package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


/**
 * Created by hemalathas on 23/2/17.
 */
public class RecapErrorPageControllerUT extends BaseTestCaseUT {

    @InjectMocks
    RecapErrorPageController recapErrorPageController;

    @Test
    public void testErrorPage(){
        String response = recapErrorPageController.recapErrorPage();
        String path = recapErrorPageController.getErrorPath();
        assertNotNull(response);
        assertEquals("error",response);
        assertNotNull(path);
        assertEquals("/error",path);
    }

}