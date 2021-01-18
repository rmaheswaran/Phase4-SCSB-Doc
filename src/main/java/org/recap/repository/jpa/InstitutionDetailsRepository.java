package org.recap.repository.jpa;

import org.recap.model.jpa.InstitutionEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface InstitutionDetailsRepository extends BaseRepository<InstitutionEntity> {
    /**
     * Finds institution entity by using institution code.
     *
     * @param institutionCode the institution code
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionCode(String institutionCode);

    /**
     * Finds institution entity by using institution name.
     *
     * @param institutionName the institution name
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionName(String institutionName);

    /**
     * Finds institution entity which is not in the given list of institution code.
     *
     * @param institutionCodes list of institution codes
     * @return the list of institution entities
     */
    List<InstitutionEntity> findByInstitutionCodeNotIn(List<String> institutionCodes);

    @Query(value = "select INSTITUTION_CODE from institution_t where INSTITUTION_CODE != 'HTC';",nativeQuery = true)
    List<String> findAllInstitutionCodeExceptHTC();
}
