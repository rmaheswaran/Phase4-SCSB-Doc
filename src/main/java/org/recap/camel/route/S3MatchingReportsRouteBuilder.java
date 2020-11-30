package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.processor.EmailService;
import org.recap.camel.processor.StopRouteProcessor;
import org.recap.model.matchingReports.MatchingSerialAndMVMReports;
import org.recap.model.matchingReports.MatchingSummaryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by angelind on 22/6/17.
 */
@Component
public class S3MatchingReportsRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3MatchingReportsRouteBuilder.class);

    /**
     * Instantiates a new s3 matching reports route builder.
     *
     * @param camelContext               the camel context
     * @param matchingReportsDirectory   the matching reports directory
     * @param s3MatchingReportsDirectory the s3 MatchingReports Directory
     * @param applicationContext         the application context
     */
    public S3MatchingReportsRouteBuilder(CamelContext camelContext, @Value("${ongoing.matching.report.directory}") String matchingReportsDirectory,
                                         @Value("${s3.matchingAlgorithm.reports.dir}") String s3MatchingReportsDirectory, ApplicationContext applicationContext) {
        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FILE + matchingReportsDirectory + RecapConstants.DELETE_FILE_OPTION)
                            .routeId(RecapConstants.FTP_TITLE_EXCEPTION_REPORT_ROUTE_ID)
                            .noAutoStartup()
                            .setHeader(S3Constants.KEY, simple(s3MatchingReportsDirectory+"/${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                            .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                            .onCompletion()
                            .process(new StopRouteProcessor(RecapConstants.FTP_TITLE_EXCEPTION_REPORT_ROUTE_ID))
                            .log("Title_Exception report generated and uploaded to s3 successfully.");
                }
            });
        } catch (Exception e) {
            logger.info(RecapCommonConstants.LOG_ERROR, e);
        }

        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SERIAL_MVM_REPORT_Q)
                            .routeId(RecapConstants.FTP_SERIAL_MVM_REPORT_ROUTE_ID)
                            .noAutoStartup()
                            .marshal().bindy(BindyType.Csv, MatchingSerialAndMVMReports.class)
                            .setHeader(S3Constants.KEY, simple(s3MatchingReportsDirectory+"/${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                            .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                            .onCompletion()
                            .process(new StopRouteProcessor(RecapConstants.FTP_SERIAL_MVM_REPORT_ROUTE_ID))
                            .log("Matching Serial_MVM reports generated and uploaded to s3 successfully.");

                }
            });
        } catch (Exception e) {
            logger.info(RecapCommonConstants.LOG_ERROR, e);
        }

        try {
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_Q)
                            .routeId(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_ROUTE_ID)
                            .noAutoStartup()
                            .marshal().bindy(BindyType.Csv, MatchingSummaryReport.class)
                            .setHeader(S3Constants.KEY, simple(s3MatchingReportsDirectory+"/${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                            .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                            .onCompletion()
                            .bean(applicationContext.getBean(EmailService.class), RecapConstants.MATCHING_REPORTS_SEND_EMAIL)
                            .process(new StopRouteProcessor(RecapConstants.FTP_MATCHING_SUMMARY_REPORT_ROUTE_ID))
                            .log("Matching Summary reports generated and uploaded to s3 successfully.")
                            .end();

                }
            });
        } catch (Exception e) {
            logger.info(RecapCommonConstants.LOG_ERROR, e);
        }
    }
}
