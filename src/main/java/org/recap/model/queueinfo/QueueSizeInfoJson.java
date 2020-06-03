package org.recap.model.queueinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by akulak on 20/10/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueSizeInfoJson {
    private String value;
}
