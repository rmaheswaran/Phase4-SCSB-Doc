package org.recap.model.matchingReports;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 22/6/17.
 */
@Data
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class MatchingSerialAndMVMReports implements Serializable{

    @DataField(pos = 1, columnName = "OwningInstitutionId")
    private String owningInstitutionId;

    @DataField(pos = 2, columnName = "Title")
    private String title;

    @DataField(pos = 3, columnName = "Summary Holdings")
    private String summaryHoldings;

    @DataField(pos = 4, columnName = "Volume Part Year")
    private String volumePartYear;

    @DataField(pos = 5, columnName = "Use Restriction")
    private String useRestriction;

    @DataField(pos = 6, columnName = "BibId")
    private String BibId;

    @DataField(pos = 7, columnName = "OwningInstitutionBibId")
    private String owningInstitutionBibId;

    @DataField(pos = 8, columnName = "Barcode")
    private String barcode;
}
