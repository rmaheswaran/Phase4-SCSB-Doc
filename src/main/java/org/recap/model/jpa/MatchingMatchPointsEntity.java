package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by angelind on 27/10/16.
 */
@Entity
@Table(name = "MATCHING_MATCHPOINTS_T", schema = "RECAP", catalog = "")
@Getter
@Setter
public class MatchingMatchPointsEntity extends AbstractEntity<Integer> {
    @Column(name = "MATCH_CRITERIA")
    private String matchCriteria;

    @Column(name = "CRITERIA_VALUE")
    private String criteriaValue;

    @Column(name = "CRITERIA_VALUE_COUNT")
    private Integer criteriaValueCount;
}
