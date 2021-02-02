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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by rajeshbabuk on 20/7/17.
 */
@Component
public class S3SubmitCollectionSuccessReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3SubmitCollectionSuccessReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate submit collection success report to the S3.
     *
     * @param context                    the context
     * @param submitCollectionS3ReportPath the submit Collection Report Path
     */
    @Autowired
    public S3SubmitCollectionSuccessReportRouteBuilder(CamelContext context, @Value("${add.s3.routes.on.startup}") boolean addS3RoutesOnStartup, @Value("${s3.submit.collection.report.dir}") String submitCollectionS3ReportPath) {
        try {
            if (addS3RoutesOnStartup) {
                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(RecapConstants.FTP_SUBMIT_COLLECTION_SUCCESS_REPORT_Q)
                                .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_SUCCESS_REPORT_ID)
                                .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                                .setHeader(S3Constants.KEY, simple(submitCollectionS3ReportPath + "${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
    }
}
