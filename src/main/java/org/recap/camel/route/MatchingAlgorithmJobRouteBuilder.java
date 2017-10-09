package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.recap.RecapConstants;
import org.recap.controller.OngoingMatchingAlgorithmJobRestController;
import org.recap.model.solr.SolrIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by rajeshbabuk on 5/9/17.
 */
@Component
public class MatchingAlgorithmJobRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmJobRouteBuilder.class);

    /**
     * Instantiates a new Matching algorithm job route builder.
     *
     * @param camelContext                              the camel context
     * @param ongoingMatchingAlgorithmJobRestController the ongoing matching algorithm job rest controller
     */
    @Autowired
    public MatchingAlgorithmJobRouteBuilder(CamelContext camelContext, OngoingMatchingAlgorithmJobRestController ongoingMatchingAlgorithmJobRestController) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.MATCHING_ALGORITHM_JOB_INITIATE_QUEUE)
                            .routeId(RecapConstants.MATCHING_ALGORITHM_JOB_INITIATE_ROUTE_ID)
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) {
                                    String jobId = null;
                                    try {
                                        Map<String, String> requestMap = (Map<String, String>) exchange.getIn().getBody();
                                        jobId = requestMap.get(RecapConstants.JOB_ID);
                                        logger.info("Ongoing Matching Algorithm Job Initiated for Job Id : " + jobId);
                                        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
                                        solrIndexRequest.setProcessType(requestMap.get(RecapConstants.PROCESS_TYPE));
                                        solrIndexRequest.setCreatedDate(new SimpleDateFormat(RecapConstants.MATCHING_BATCH_JOB_DATE_FORMAT).parse(requestMap.get(RecapConstants.CREATED_DATE)));
                                        String matchingAlgorithmJobStatus = ongoingMatchingAlgorithmJobRestController.startMatchingAlgorithmJob(solrIndexRequest);
                                        logger.info("Job Id : {} Ongoing Matching Algorithm Job Status : {}", jobId, matchingAlgorithmJobStatus);
                                        exchange.getIn().setBody("JobId:" + jobId + "|" + matchingAlgorithmJobStatus);
                                    } catch (Exception ex) {
                                        exchange.getIn().setBody("JobId:" + jobId + "|" + ex.getMessage());
                                        logger.info(RecapConstants.LOG_ERROR, ex);
                                    }
                                }
                            })
                            .onCompletion()
                            .to(RecapConstants.MATCHING_ALGORITHM_JOB_COMPLETION_OUTGOING_QUEUE)
                            .end();
                }
            });
        } catch (Exception ex) {
            logger.error(RecapConstants.EXCEPTION, ex);
        }
    }
}
