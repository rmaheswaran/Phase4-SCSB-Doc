package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;


/**
 * Created by rajeshbabuk on 18/10/16.
 */
@Entity
@Table(name = "customer_code_t", schema = "recap", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "CUSTOMER_CODE_ID"))
@Getter
@Setter
public class CustomerCodeEntity extends CustomerCodeAbstractEntity {
}
