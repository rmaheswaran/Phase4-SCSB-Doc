package org.recap.model.csv;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 30/9/16.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class SolrExceptionReportReCAPCSVRecord implements Serializable {

    @DataField(pos = 1, columnName = "Document Type")
    private String docType;
    @DataField(pos = 2, columnName = "Owning Institution")
    private String owningInstitution;
    @DataField(pos = 3, columnName = "Owning Institution BibId")
    private String owningInstitutionBibId;
    @DataField(pos = 4, columnName = "Bib Id")
    private String bibId;
    @DataField(pos = 5, columnName = "Holdings Id")
    private String holdingsId;
    @DataField(pos = 6, columnName = "Item Id")
    private String itemId;
    @DataField(pos = 7, columnName = "Exception Message")
    private String exceptionMessage;
}
