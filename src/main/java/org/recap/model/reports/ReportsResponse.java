package org.recap.model.reports;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
@Getter
@Setter
public class ReportsResponse extends ReportsBase {

    private String message;
    private Integer incompletePageNumber = 0;
    private Integer incompletePageSize = 10;
}
