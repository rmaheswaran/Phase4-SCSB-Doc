package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.JobEntity;
import org.recap.repository.jpa.JobDetailsRepository;

import static org.junit.Assert.assertEquals;

/**
 * Created by rajeshbabuk on 20/4/17.
 */
public class UpdateJobControllerUT extends BaseTestCaseUT {

    @InjectMocks
    UpdateJobController updateJobController;

    @Mock
    JobDetailsRepository jobDetailsRepository;

    @Test
    public void testUpdateJob() throws Exception {
        JobEntity jobEntity = new JobEntity();
        jobEntity.setJobName(RecapConstants.PURGE_EXCEPTION_REQUESTS);
        String status=updateJobController.updateJob(jobEntity);
        assertEquals(RecapCommonConstants.SUCCESS,status);
    }
}
