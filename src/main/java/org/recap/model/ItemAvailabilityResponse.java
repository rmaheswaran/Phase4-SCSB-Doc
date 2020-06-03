package org.recap.model;

import lombok.Data;

/**
 * Created by akulak on 3/3/17.
 */
@Data
public class ItemAvailabilityResponse {
    private String itemBarcode;
    private String itemAvailabilityStatus;
    private String errorMessage;
}
