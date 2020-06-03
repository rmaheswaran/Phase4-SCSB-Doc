package org.recap.model.transfer;

import lombok.Data;

/**
 * Created by sheiks on 12/07/17.
 */
@Data
public class HoldingsTransferRequest {
    private Source source;
    private Destination destination;
}
