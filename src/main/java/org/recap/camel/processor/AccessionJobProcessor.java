package org.recap.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.camel.EmailPayLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class AccessionJobProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AccessionJobProcessor.class);

    @Autowired
    private ProducerTemplate producer;
    @Value("${accession.job.exception.email.to}")
    private String emailTo;
    @Value("${accession.job.exception.email.cc}")
    private String emailCc;

    public void caughtException(Exchange exchange){
        logger.info("inside caught exception..........");
        Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        if(exception!=null){
            producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, getEmailPayLoadForExcepion(exception,exception.getMessage()), RecapConstants.EMAIL_FOR,RecapConstants.ACCESSION_JOB_FAILURE);
        }
    }

    private EmailPayLoad getEmailPayLoadForExcepion(Exception exception,String exceptionMessage) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setSubject(RecapConstants.ACCESSION_JOB_FAILURE);
        emailPayLoad.setTo(emailTo);
        emailPayLoad.setCc(emailCc);
        emailPayLoad.setMessage("An exception has occurred in Ongoing Accession process. \n Exception : "+exception+"\n Exception Message : "+exceptionMessage);
        return  emailPayLoad;
    }
}
