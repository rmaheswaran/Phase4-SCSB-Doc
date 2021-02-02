package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.context.ApplicationContext;
import org.recap.RecapConstants;
import org.recap.camel.processor.EmailService;
import org.recap.model.csv.OngoingAccessionReportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by premkb on 07/02/17.
 */
@Component
public class S3OngoingAccessionReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3OngoingAccessionReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate ongoing accession report to the S3.
     *
     * @param context              the context
     * @param ongoingAccessionPathS3 ongoing Accession Path s3
     * @param applicationContext   the application context
     */
    @Autowired
    public S3OngoingAccessionReportRouteBuilder(CamelContext context, @Value("${s3.add.s3.routes.on.startup}") boolean addS3RoutesOnStartup, @Value("${s3.ongoing.accession.collection.report.dir}") String ongoingAccessionPathS3, ApplicationContext applicationContext) {
        try {
            if (addS3RoutesOnStartup) {
                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(RecapConstants.FTP_ONGOING_ACCESSON_REPORT_Q)
                                .routeId(RecapConstants.FTP_ONGOING_ACCESSION_REPORT_ID)
                                .marshal().bindy(BindyType.Csv, OngoingAccessionReportRecord.class)
                                .setHeader(S3Constants.KEY, simple(ongoingAccessionPathS3 + "${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                                .onCompletion()
                                .bean(applicationContext.getBean(EmailService.class), RecapConstants.ACCESSION_REPORTS_SEND_EMAIL);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(RecapConstants.ERROR, e);
        }
    }
}
