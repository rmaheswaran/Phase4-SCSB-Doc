package org.recap.model.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by sheiks on 13/07/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTransferResponse {
    private String message;
    private boolean success;
    private ItemTransferRequest itemTransferRequest;
}
