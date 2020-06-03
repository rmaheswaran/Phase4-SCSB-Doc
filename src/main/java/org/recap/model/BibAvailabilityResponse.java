package org.recap.model;

import lombok.Data;

/**
 * Created by hemalathas on 28/9/17.
 */
@Data
public class BibAvailabilityResponse {
    private String itemBarcode;
    private String itemAvailabilityStatus;
    private String collectionGroupDesignation;
    private String errorMessage;
}
