package org.recap.model.transfer;


import lombok.Getter;
import lombok.Setter;

/**
 * Created by sheiks on 13/07/17.
 */
@Getter
@Setter
public class ItemTransferResponse {
    private String message;
    private boolean success;
    private ItemTransferRequest itemTransferRequest;

    public ItemTransferResponse() {
    }

    public ItemTransferResponse(String message, ItemTransferRequest itemTransferRequest, boolean success) {
        this.message = message;
        this.itemTransferRequest = itemTransferRequest;
        this.success = success;
    }
}
