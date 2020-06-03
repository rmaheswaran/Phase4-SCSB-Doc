package org.recap.model.transfer;

import lombok.Data;

import java.util.List;

/**
 * Created by sheiks on 12/07/17.
 */
@Data
public class TransferResponse {
    private String message;
    private List<HoldingTransferResponse> holdingTransferResponses;
    private List<ItemTransferResponse> itemTransferResponses;
}
