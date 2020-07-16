package org.recap.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 25/6/20.
 */
@Getter
@Setter
public class DocTypeAvailabilityResponse {
    private String itemBarcode;
    private String itemAvailabilityStatus;
    private String errorMessage;
}
