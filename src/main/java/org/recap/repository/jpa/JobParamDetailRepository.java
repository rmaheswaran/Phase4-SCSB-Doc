package org.recap.repository.jpa;

import org.recap.model.jpa.JobParamEntity;
/**
 * Created by angelind on 2/5/2017.
 */
public interface JobParamDetailRepository extends BaseRepository<JobParamEntity> {

    /**
     * Finds job param entity by using job name.
     *
     * @param jobName the job name
     * @return the job param entity
     */
    JobParamEntity findByJobName(String jobName);

}
