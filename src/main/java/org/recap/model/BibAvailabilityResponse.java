package org.recap.model;

/**
 * Created by hemalathas on 28/9/17.
 */
public class BibAvailabilityResponse {

    private String itemBarcode;
    private String itemAvailabilityStatus;
    private String collectionGroupDesignation;
    private String errorMessage;

    /**
     * Gets item barcode.
     *
     * @return the item barcode
     */
    public String getItemBarcode() {
        return itemBarcode;
    }

    /**
     * Sets item barcode.
     *
     * @param itemBarcode the item barcode
     */
    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    /**
     * Gets item availability status.
     *
     * @return the item availability status
     */
    public String getItemAvailabilityStatus() {
        return itemAvailabilityStatus;
    }

    /**
     * Sets item availability status.
     *
     * @param itemAvailabilityStatus the item availability status
     */
    public void setItemAvailabilityStatus(String itemAvailabilityStatus) {
        this.itemAvailabilityStatus = itemAvailabilityStatus;
    }

    /**
     * Gets collection group designation.
     *
     * @return the collection group designation
     */
    public String getCollectionGroupDesignation() {
        return collectionGroupDesignation;
    }

    /**
     * Sets collection group designation.
     *
     * @param collectionGroupDesignation the collection group designation
     */
    public void setCollectionGroupDesignation(String collectionGroupDesignation) {
        this.collectionGroupDesignation = collectionGroupDesignation;
    }

    /**
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
