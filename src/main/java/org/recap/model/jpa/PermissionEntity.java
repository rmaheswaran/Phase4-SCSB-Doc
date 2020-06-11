package org.recap.model.jpa;



import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Cacheable(true)
@Entity
@Table(name="permissions_t",schema="recap",catalog="")
@AttributeOverride(name = "id", column = @Column(name = "permission_id"))
@Getter
@Setter
public class PermissionEntity implements Serializable{
    @Id
    @Column(name="permission_id")
    private int permissionId;

    @Column(name="permission_name")
    private String permissionName;

    @Column(name="permission_description")
    private String permissionDesc;

    @ManyToMany(mappedBy ="permissions")
    private List<RoleEntity> roleEntityList;
}
