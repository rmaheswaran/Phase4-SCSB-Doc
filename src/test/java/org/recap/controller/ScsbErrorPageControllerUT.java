package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


/**
 * Created by hemalathas on 23/2/17.
 */
public class ScsbErrorPageControllerUT extends BaseTestCaseUT {

    @InjectMocks
    ScsbErrorPageController scsbErrorPageController;

    @Test
    public void testErrorPage(){
        String response = scsbErrorPageController.recapErrorPage();
        String path = scsbErrorPageController.getErrorPath();
        assertNotNull(response);
        assertEquals("error",response);
        assertNotNull(path);
        assertEquals("/error",path);
    }

}