package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 29/6/17.
 */
public class JobParamDataEntityUT extends BaseTestCase{

    @Test
    public void testJobParamDataEntity(){
        JobParamDataEntity jobParamDataEntity = new JobParamDataEntity();
        jobParamDataEntity.setId(1);
        jobParamDataEntity.setParamName("Accession");
        jobParamDataEntity.setParamValue("test");
        jobParamDataEntity.setRecordNum("10");
        assertNotNull(jobParamDataEntity.getRecordNum());
        assertNotNull(jobParamDataEntity.getId());
        assertNotNull(jobParamDataEntity.getParamName());
        assertNotNull(jobParamDataEntity.getParamValue());
    }

}