package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by premkb on 6/2/17.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class OngoingAccessionReportRecord implements Serializable {

    @DataField(pos = 1, columnName = "Customer Code")
    private String customerCode;

    @DataField(pos = 2, columnName = "Item Barcode")
    private String itemBarcode;

    @DataField(pos = 3, columnName = "Message")
    private String message;
}
