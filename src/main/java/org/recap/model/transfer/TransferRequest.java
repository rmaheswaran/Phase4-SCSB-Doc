package org.recap.model.transfer;

import lombok.Data;

import java.util.List;

/**
 * Created by sheiks on 12/07/17.
 */
@Data
public class TransferRequest {
    private String institution;
    private List<HoldingsTransferRequest> holdingTransfers;
    private List<ItemTransferRequest> itemTransfers;
}
