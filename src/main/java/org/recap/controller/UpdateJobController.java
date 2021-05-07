package org.recap.controller;

import org.recap.ScsbCommonConstants;
import org.recap.model.jpa.JobEntity;
import org.recap.repository.jpa.JobDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 12/4/17.
 */
@RestController
@RequestMapping("/updateJobService")
public class UpdateJobController {

    @Autowired
    private JobDetailsRepository jobDetailsRepository;

    /**
     * This method is used to update the job entity.
     *
     * @param jobEntity the job entity
     * @return the string
     */
    @PostMapping(value="/updateJob")
    public String updateJob(@RequestBody JobEntity jobEntity) {
        jobDetailsRepository.save(jobEntity);
        return ScsbCommonConstants.SUCCESS;
    }
}
