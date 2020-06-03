package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by chenchulakshmig on 30/9/16.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class DeAccessionSummaryRecord implements Serializable {

    @DataField(pos = 1, columnName = "Date of DeAccession")
    private String dateOfDeAccession;

    @DataField(pos = 2, columnName = "Owning Institution")
    private String owningInstitution;

    @DataField(pos = 3, columnName = "Barcode")
    private String barcode;

    @DataField(pos = 4, columnName = "Owning Inst Bib ID")
    private String owningInstitutionBibId;

    @DataField(pos = 5, columnName = "Title")
    private String title;

    @DataField(pos = 6, columnName = "CGD")
    private String collectionGroupCode;

    @DataField(pos = 7, columnName = "Item DeAccession Status")
    private String status;

    @DataField(pos = 8, columnName = "Reason for failure")
    private String reasonForFailure;
}
