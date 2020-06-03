package org.recap.model.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by sheiks on 12/07/17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoldingTransferResponse {
    private String message;
    private boolean success;
    private HoldingsTransferRequest holdingsTransferRequest;
}
