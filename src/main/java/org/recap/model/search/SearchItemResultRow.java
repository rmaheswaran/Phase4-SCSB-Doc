package org.recap.model.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by rajesh on 18-Jul-16.
 */
@Data
@EqualsAndHashCode(of = {"chronologyAndEnum"})
@ApiModel(value="SearchItemResultRow", description="Model for Displaying Item Result")
public class SearchItemResultRow implements Comparable<SearchItemResultRow> {
    @ApiModelProperty(name= "callNumber", value= "Call Number",position = 0)
    private String callNumber;
    @ApiModelProperty(name= "chronologyAndEnum", value= "Chronology And Enum",position = 1)
    private String chronologyAndEnum;
    @ApiModelProperty(name= "customerCode", value= "Customer Code",position = 2)
    private String customerCode;
    @ApiModelProperty(name= "barcode", value= "barcode",position = 3)
    private String barcode;
    @ApiModelProperty(name= "useRestriction", value= "use Restriction",position = 4)
    private String useRestriction;
    @ApiModelProperty(name= "collectionGroupDesignation", value= "collection Group Designation",position = 5)
    private String collectionGroupDesignation;
    @ApiModelProperty(name= "availability", value= "Availability",position = 6)
    private String availability;
    @ApiModelProperty(name= "selectedItem", value= "selected Item",position = 7)
    private boolean selectedItem = false;
    @ApiModelProperty(name= "itemId", value= "Item Id",position = 8)
    private Integer itemId;
    @ApiModelProperty(name= "owningInstitutionItemId", value= "Owning Institution Item Id",position = 9)
    private String owningInstitutionItemId;
    @ApiModelProperty(name= "owningInstitutionHoldingsId", value= "Owning Institution Holdings Id",position = 10)
    private String owningInstitutionHoldingsId;

    @Override
    public int compareTo(SearchItemResultRow searchItemResultRow) {
        String objChronologyAndEnum=null;
        String searchItemResultRowChronologyAndEnum=null;
        if(this != null && this.getChronologyAndEnum() !=null){
            objChronologyAndEnum=this.getChronologyAndEnum();
        }
        if(searchItemResultRow != null && searchItemResultRow.getChronologyAndEnum() !=null){
            searchItemResultRowChronologyAndEnum=searchItemResultRow.getChronologyAndEnum();
        }
        if(objChronologyAndEnum == null && searchItemResultRowChronologyAndEnum == null){
            return 0;
        }
        else if(objChronologyAndEnum == null){
            return -1;
        }
        else if(searchItemResultRowChronologyAndEnum == null){
            return 1;
        }
        else {
            return objChronologyAndEnum.compareTo(searchItemResultRowChronologyAndEnum);
        }
    }
}

