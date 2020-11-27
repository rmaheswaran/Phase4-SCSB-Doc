package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapCommonConstants;
import org.recap.model.csv.AccessionSummaryRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by hemalathas on 23/11/16.
 */
@Component
public class S3AccessionSummaryReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3AccessionSummaryReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate accession summary report file to the FTP.
     *
     * @param context        the context
     * @param etlReportsPath the etl Reports Path
     */
    @Autowired
    public S3AccessionSummaryReportRouteBuilder(CamelContext context, @Value("${ftp.etl.reports.dir}") String etlReportsPath) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapCommonConstants.FTP_ACCESSION_SUMMARY_REPORT_Q)
                            .routeId(RecapCommonConstants.FTP_ACCESSION_SUMMARY_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, AccessionSummaryRecord.class)
                            .setHeader(S3Constants.KEY, simple("reports/share/etl-reports/${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                            .to("aws-s3://{{scsbBucketName}}?autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})");
                }
            });
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
        }
    }
}
