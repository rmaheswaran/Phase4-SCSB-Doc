package org.recap.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.recap.RecapCommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by angelind on 22/6/17.
 */
public class StopRouteProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(StopRouteProcessor.class);
    private String routeId;

    /**
     * Instantiates a new Stop route processor.
     *
     * @param routeId the route id
     */
    public StopRouteProcessor(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Thread stopThread;
        stopThread = new Thread() {
            @Override
            public void run() {
                try {
                    exchange.getContext().getRouteController().stopRoute(routeId);
                } catch (Exception e) {
                    logger.error("Exception while stop route : {}" , routeId);
                    logger.error(RecapCommonConstants.LOCAL_ITEM_ID, e.getStackTrace().toString());
                }
            }
        };
        stopThread.start();
    }
}
