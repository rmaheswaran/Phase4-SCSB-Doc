package org.recap.model.search;

import lombok.Data;

/**
 * Created by akulak on 23/12/16.
 */
@Data
public class DeaccessionItemResultsRow {
    private Integer itemId;
    private String deaccessionDate;
    private String title;
    private String deaccessionOwnInst;
    private String itemBarcode;
    private String cgd;
    private String deaccessionNotes;
    private String deaccessionCreatedBy;
}
