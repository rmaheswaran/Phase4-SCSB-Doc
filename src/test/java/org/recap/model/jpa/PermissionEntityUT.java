package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.PermissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 25/1/17.
 */
public class PermissionEntityUT extends BaseTestCase{

    @Autowired
    PermissionsRepository permissionsRepository;

    @Test
    public void savePermission(){
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setPermissionName("new admin");
        permissionEntity.setPermissionDesc("new admin");
        PermissionEntity savedPermissionEntity = permissionsRepository.save(permissionEntity);
        assertNotNull(savedPermissionEntity);
        assertNotNull(savedPermissionEntity.getId());
    }

    @Test
    public void testPermission(){
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setId(1);
        permissionEntity.setPermissionName("new admin");
        permissionEntity.setPermissionDesc("new admin");
        permissionEntity.setRoleEntityList(Arrays.asList(new RoleEntity()));
        assertNotNull(permissionEntity.getId());
        assertNotNull(permissionEntity.getPermissionDesc());
        assertNotNull(permissionEntity.getPermissionName());
        assertNotNull(permissionEntity.getRoleEntityList());
    }


}