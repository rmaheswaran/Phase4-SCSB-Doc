package org.recap.model.matchingReports;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 28/6/17.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class MatchingSummaryReport implements Serializable {

    @DataField(pos = 1, columnName = "Institution")
    private String institution;

    @DataField(pos = 2, columnName = "Total Bibs")
    private String totalBibs;

    @DataField(pos = 3, columnName = "Total Items")
    private String totalItems;

    @DataField(pos = 4, columnName = "Shared Items Before Matching")
    private String sharedItemsBeforeMatching;

    @DataField(pos = 5, columnName = "Shared Items After Matching")
    private String sharedItemsAfterMatching;

    @DataField(pos = 6, columnName = "Difference of Shared Items")
    private String sharedItemsDiff;

    @DataField(pos = 7, columnName = "Open Items Before Matching")
    private String openItemsBeforeMatching;

    @DataField(pos = 8, columnName = "Open Items After Matching")
    private String openItemsAfterMatching;

    @DataField(pos = 9, columnName = "Difference of Open Items")
    private String openItemsDiff;

    /**
     * Gets institution.
     *
     * @return the institution
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * Sets institution.
     *
     * @param institution the institution
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    /**
     * Gets total bibs.
     *
     * @return the total bibs
     */
    public String getTotalBibs() {
        return totalBibs;
    }

    /**
     * Sets total bibs.
     *
     * @param totalBibs the total bibs
     */
    public void setTotalBibs(String totalBibs) {
        this.totalBibs = totalBibs;
    }

    /**
     * Gets total items.
     *
     * @return the total items
     */
    public String getTotalItems() {
        return totalItems;
    }

    /**
     * Sets total items.
     *
     * @param totalItems the total items
     */
    public void setTotalItems(String totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Gets shared items before matching.
     *
     * @return the shared items before matching
     */
    public String getSharedItemsBeforeMatching() {
        return sharedItemsBeforeMatching;
    }

    /**
     * Sets shared items before matching.
     *
     * @param sharedItemsBeforeMatching the shared items before matching
     */
    public void setSharedItemsBeforeMatching(String sharedItemsBeforeMatching) {
        this.sharedItemsBeforeMatching = sharedItemsBeforeMatching;
    }

    /**
     * Gets open items before matching.
     *
     * @return the open items before matching
     */
    public String getOpenItemsBeforeMatching() {
        return openItemsBeforeMatching;
    }

    /**
     * Sets open items before matching.
     *
     * @param openItemsBeforeMatching the open items before matching
     */
    public void setOpenItemsBeforeMatching(String openItemsBeforeMatching) {
        this.openItemsBeforeMatching = openItemsBeforeMatching;
    }

    /**
     * Gets shared items after matching.
     *
     * @return the shared items after matching
     */
    public String getSharedItemsAfterMatching() {
        return sharedItemsAfterMatching;
    }

    /**
     * Sets shared items after matching.
     *
     * @param sharedItemsAfterMatching the shared items after matching
     */
    public void setSharedItemsAfterMatching(String sharedItemsAfterMatching) {
        this.sharedItemsAfterMatching = sharedItemsAfterMatching;
    }

    /**
     * Gets open items after matching.
     *
     * @return the open items after matching
     */
    public String getOpenItemsAfterMatching() {
        return openItemsAfterMatching;
    }

    /**
     * Sets open items after matching.
     *
     * @param openItemsAfterMatching the open items after matching
     */
    public void setOpenItemsAfterMatching(String openItemsAfterMatching) {
        this.openItemsAfterMatching = openItemsAfterMatching;
    }

    /**
     * Gets shared items diff.
     *
     * @return the shared items diff
     */
    public String getSharedItemsDiff() {
        return sharedItemsDiff;
    }

    /**
     * Sets shared items diff.
     *
     * @param sharedItemsDiff the shared items diff
     */
    public void setSharedItemsDiff(String sharedItemsDiff) {
        this.sharedItemsDiff = sharedItemsDiff;
    }

    /**
     * Gets open items diff.
     *
     * @return the open items diff
     */
    public String getOpenItemsDiff() {
        return openItemsDiff;
    }

    /**
     * Sets open items diff.
     *
     * @param openItemsDiff the open items diff
     */
    public void setOpenItemsDiff(String openItemsDiff) {
        this.openItemsDiff = openItemsDiff;
    }
}
