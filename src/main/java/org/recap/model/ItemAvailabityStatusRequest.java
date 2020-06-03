package org.recap.model;

import lombok.Data;

import java.util.List;

/**
 * Created by akulak on 3/3/17.
 */
@Data
public class ItemAvailabityStatusRequest {
    private List<String> barcodes;
}
