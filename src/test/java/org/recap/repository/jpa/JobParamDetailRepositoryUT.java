package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.JobParamDataEntity;
import org.recap.model.jpa.JobParamEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 3/5/17.
 */
public class JobParamDetailRepositoryUT extends BaseTestCase{

    @Autowired
    JobParamDetailRepository jobParamDetailRepository;

    @Test
    public void findByJobName() throws Exception {
        JobParamEntity jobParamEntity = saveJobParamEntity();
        assertNotNull(jobParamEntity);
        assertNotNull(jobParamEntity.getId());

        JobParamEntity byJobName = jobParamDetailRepository.findByJobName(jobParamEntity.getJobName());
        assertNotNull(byJobName);
        assertEquals(byJobName.getId(), jobParamEntity.getId());
        assertEquals(byJobName.getJobParamDataEntities().size(), jobParamEntity.getJobParamDataEntities().size());
    }

    private JobParamEntity saveJobParamEntity() {
        JobParamEntity jobParamEntity = new JobParamEntity();
        jobParamEntity.setJobName("GenerateAccessionReportTest");
        List<JobParamDataEntity> jobParamDataEntityList = new ArrayList<>();
        JobParamDataEntity reportTypeDataEntity = new JobParamDataEntity();
        reportTypeDataEntity.setParamName("ReportType");
        reportTypeDataEntity.setParamValue(RecapConstants.ONGOING_ACCESSION_REPORT);
        jobParamDataEntityList.add(reportTypeDataEntity);
        JobParamDataEntity transmissionTypeDataEntity = new JobParamDataEntity();
        transmissionTypeDataEntity.setParamName("TransmissionType");
        transmissionTypeDataEntity.setParamValue(RecapCommonConstants.FTP);
        jobParamDataEntityList.add(transmissionTypeDataEntity);
        jobParamEntity.addAll(jobParamDataEntityList);

        return jobParamDetailRepository.saveAndFlush(jobParamEntity);
    }

    @Test
    public void testJobParamEntity(){
        JobParamEntity jobparamEntity = new JobParamEntity();
        jobparamEntity.setId(1);
        jobparamEntity.setJobName("Test");
        jobparamEntity.setJobParamDataEntities(Arrays.asList(new JobParamDataEntity()));
        assertNotNull(jobparamEntity.getJobName());
        assertNotNull(jobparamEntity.getId());
        assertNotNull(jobparamEntity.getJobParamDataEntities());

    }
}