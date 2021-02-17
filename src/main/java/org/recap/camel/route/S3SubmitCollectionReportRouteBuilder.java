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
 * Created by akulak on 30/5/17.
 */
@Component
public class S3SubmitCollectionReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3SubmitCollectionReportRouteBuilder.class);

    /**
     * Instantiates a new route builder for submit collection api to generate the file which contains the response for submit collection to the S3.
     *
     * @param context                    the context
     * @param submitCollectionS3ReportPath the submit Collection Report Path
     */
    @Autowired
    public S3SubmitCollectionReportRouteBuilder(CamelContext context, @Value("${s3.add.s3.routes.on.startup}") boolean addS3RoutesOnStartup, @Value("${s3.submit.collection.support.team.report.dir}") String submitCollectionS3ReportPath) {
        try {
            if (addS3RoutesOnStartup) {
                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(RecapConstants.FTP_SUBMIT_COLLECTION_REPORT_Q)
                                .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_REPORT_ID)
                                .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                                .setHeader(S3Constants.KEY, simple(submitCollectionS3ReportPath + "${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                                .onCompletion().log("Submit Collection Report file generated in the S3");
                    }
                });
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
    }

}
