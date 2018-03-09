package org.recap.repository.jpa;

import org.recap.model.jpa.ItemChangeLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by rajeshbabuk on 18/10/16.
 */
public interface ItemChangeLogDetailsRepository extends JpaRepository<ItemChangeLogEntity, Integer> {

    /**
     * Gets record ids based on the operation type .
     *
     * @param pageable      the pageable
     * @param operationType the operation type
     * @return the record id by operation type
     */
    @Query(value = "select icl.recordId from ItemChangeLogEntity icl where icl.operationType=?1")
     Page<Integer> getRecordIdByOperationType(Pageable pageable, String operationType);
}
