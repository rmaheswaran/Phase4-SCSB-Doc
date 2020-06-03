package org.recap.model.matchingReports;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 28/6/17.
 */
@Data
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
}
