package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by hemalathas on 21/12/16.
 */
@Component
public class S3SubmitCollectionSummaryReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3SubmitCollectionSummaryReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate submit collection summary report to the S3.
     *
     * @param context the context
     */
    @Autowired
    public S3SubmitCollectionSummaryReportRouteBuilder(CamelContext context) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_Q)
                            .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .setHeader(S3Constants.KEY, simple(RecapConstants.SUBMIT_COLLECTION_BASE_PATH+"${in.header.fileName}"))
                            .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                }
            });

        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
    }

}
