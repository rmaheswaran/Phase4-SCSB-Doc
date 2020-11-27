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
 * Created by hemalathas on 21/12/16.
 */
@Component
public class FTPSubmitCollectionRejectionReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FTPSubmitCollectionRejectionReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate submit collection rejection report to the FTP.
     *
     * @param context         the context
     * @param submitCollectionReportPath the submit Collection Report Path
     */
    @Autowired
    public FTPSubmitCollectionRejectionReportRouteBuilder(CamelContext context, @Value("${ftp.submit.collection.report.dir}") String submitCollectionReportPath) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapCommonConstants.FTP_SUBMIT_COLLECTION_REJECTION_REPORT_Q)
                            .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_REJECTION_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .setHeader(S3Constants.KEY,simple("reports/collection/submitCollection/${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                            .to("aws-s3://{{scsbReports}}?autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})");
                }
            });
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
    }

}
