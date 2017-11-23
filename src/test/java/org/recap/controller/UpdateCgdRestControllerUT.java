package org.recap.controller;

import org.junit.Test;
import org.recap.RecapConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 3/1/17.
 */
public class UpdateCgdRestControllerUT extends BaseControllerUT {

    @Test
    public void updateCgdForItem() throws Exception {
        String itemBarcode = "1";
        String owningInstitution = "PUL";
        String oldCollectionGroupDesignation = "Private";
        String newCollectionGroupDesignation = "Open";
        String cgdChangeNotes = "Notes";
        String username = "guest";
        MvcResult savedResult = this.mockMvc.perform(get("/updateCgdService/updateCgd")
                .headers(getHttpHeaders())
                .param(RecapConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                .param(RecapConstants.OWNING_INST, owningInstitution)
                .param(RecapConstants.OLD_CGD, oldCollectionGroupDesignation)
                .param(RecapConstants.NEW_CGD, newCollectionGroupDesignation)
                .param(RecapConstants.CGD_CHANGE_NOTES, cgdChangeNotes)
                .param(RecapConstants.USER_NAME, username))
                .andExpect(status().isOk())
                .andReturn();

        String statusResponse = savedResult.getResponse().getContentAsString();
        assertNotNull(statusResponse);
    }
}
