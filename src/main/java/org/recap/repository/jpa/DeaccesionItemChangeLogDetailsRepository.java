package org.recap.repository.jpa;

import org.recap.model.jpa.DeaccessionItemChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by akulak on 26/2/18 .
 */
@Repository
public interface DeaccesionItemChangeLogDetailsRepository extends JpaRepository<DeaccessionItemChangeLog,Integer> {


    /**
     * Finds DeaccessionItemChangeLogEntity by using record id and operation type,orders them by updated date description.
     *
     * @param recordId      the record id
     * @param operationType the operation type
     * @return the list
     */
    @Query(value = "select item from DeaccessionItemChangeLog item where item.recordId=:recordId and item.operationType=:operationType order by item.createdDate desc")
    List<DeaccessionItemChangeLog> findByRecordIdAndOperationTypeAndOrderByUpdatedDateDesc(@Param("recordId") Integer recordId, @Param("operationType") String operationType);


}
