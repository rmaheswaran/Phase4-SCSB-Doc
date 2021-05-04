package org.recap.model.matchingreports;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class OngoingMatchingCGDReport implements Serializable {
    @DataField(pos = 1, columnName = "Item Barcode")
    private String itemBarcode;

    @DataField(pos = 2, columnName = "Old CGD")
    private String oldCgd;

    @DataField(pos = 3, columnName = "New CGD")
    private String newCgd;

    @DataField(pos = 4, columnName = "Date of Action")
    private String date;

    private String institution;
}
