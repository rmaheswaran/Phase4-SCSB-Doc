package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by hemalathas on 20/12/16.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class SubmitCollectionReportRecord implements Serializable {

    @DataField(pos = 1, columnName = "Item Barcode")
    private String itemBarcode;

    @DataField(pos = 2, columnName = "Customer Code")
    private String customerCode;

    @DataField(pos = 3, columnName = "Owning Institution")
    private String owningInstitution;

    @DataField(pos = 4, columnName = "Report Type")
    private String reportType;

    @DataField(pos = 5, columnName = "Message")
    private String message;
}
