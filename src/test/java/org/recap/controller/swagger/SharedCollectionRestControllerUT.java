package org.recap.controller.swagger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.controller.SharedCollectionRestController;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.ItemAvailabityStatusRequest;
import org.recap.service.ItemAvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
public class SharedCollectionRestControllerUT extends BaseTestCaseUT {

    @InjectMocks
    private SharedCollectionRestController sharedCollectionRestController;

    @Mock
    ItemAvailabilityService itemAvailabilityService;

    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void itemAvailabilityStatus() throws Exception {
        ItemAvailabityStatusRequest itemAvailabityStatusRequest = new ItemAvailabityStatusRequest();
        String barcode = null;
        List<String>  barcodeList = new ArrayList<>();
        barcodeList.add(barcode);
        itemAvailabityStatusRequest.setBarcodes(barcodeList);
        ResponseEntity responseEntity = sharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatusRequest);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testBibAvailabilityStatus() throws Exception {
        BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest = new BibItemAvailabityStatusRequest();
        bibItemAvailabityStatusRequest.setBibliographicId("93540");
        bibItemAvailabityStatusRequest.setInstitutionId("PUL");
        ResponseEntity responseEntity = sharedCollectionRestController.bibAvailabilityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(responseEntity);
    }
}