package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.DeaccessionItemChangeLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by akulak on 1/3/18 .
 */
public class DeaccessionItemChangeLogDetailsReppository extends BaseTestCase{

    @Autowired
    private DeaccesionItemChangeLogDetailsRepository deaccesionItemChangeLogDetailsRepository;

    @Test
    public void saveDeaccessionItemChangeLog(){
        DeaccessionItemChangeLog deaccessionItemChangeLog = getDeaccessionItemChangeLog();
        DeaccessionItemChangeLog saveddeaccessionItemChangeLog = deaccesionItemChangeLogDetailsRepository.save(deaccessionItemChangeLog);
        assertNotNull(saveddeaccessionItemChangeLog);
    }

    @Test
    public void checkfindByRecordId() throws Exception{
        DeaccessionItemChangeLog itemChangeLogEntity = getDeaccessionItemChangeLog();
        deaccesionItemChangeLogDetailsRepository.save(itemChangeLogEntity);
        List<DeaccessionItemChangeLog> byRecordId = deaccesionItemChangeLogDetailsRepository.findByRecordIdAndOperationTypeAndOrderByUpdatedDateDesc(itemChangeLogEntity.getRecordId(),"Deaccession");
        assertNotNull(byRecordId);
        for (DeaccessionItemChangeLog changeLogEntity : byRecordId) {
            if (itemChangeLogEntity.getOperationType().equalsIgnoreCase("Deaccession")){
                assertEquals("testing",changeLogEntity.getNotes());
            }
        }
    }

    private DeaccessionItemChangeLog getDeaccessionItemChangeLog() {
        DeaccessionItemChangeLog deaccessionItemChangeLog = new DeaccessionItemChangeLog();
        deaccessionItemChangeLog.setCreatedDate(new Date());
        deaccessionItemChangeLog.setNotes("testing");
        deaccessionItemChangeLog.setOperationType("Deaccession");
        deaccessionItemChangeLog.setRecordId(123);
        deaccessionItemChangeLog.setUpdatedBy("tstuser");
        return deaccessionItemChangeLog;
    }
}
