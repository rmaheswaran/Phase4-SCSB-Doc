package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.camel.EmailPayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 10/4/17.
 */
@RestController
@RequestMapping("/batchJobEmailService")
public class BatchJobEmailController {

    @Autowired
    private ProducerTemplate producerTemplate;

    /**
     * This method is used to send email on successful completion of batch job.
     *
     * @param emailPayLoad the email pay load
     * @return the string
     */
    @PostMapping(value="/batchJobEmail")
    public String batchJobSendEmail(@RequestBody EmailPayLoad emailPayLoad) {
        producerTemplate.sendBodyAndHeader(ScsbConstants.EMAIL_Q, emailPayLoad, ScsbConstants.EMAIL_FOR, ScsbConstants.BATCHJOB);
        return ScsbCommonConstants.SUCCESS;
    }
}
