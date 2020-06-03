package org.recap.model;

import lombok.Data;

/**
 * Created by akulak on 3/3/17.
 */
@Data
public class BibItemAvailabityStatusRequest {
    private String bibliographicId;
    private String institutionId;
}
