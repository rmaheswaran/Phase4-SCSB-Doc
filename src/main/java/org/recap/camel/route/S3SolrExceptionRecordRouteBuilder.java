package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapCommonConstants;
import org.recap.model.csv.SolrExceptionReportReCAPCSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by angelind on 30/9/16.
 */
@Component
public class S3SolrExceptionRecordRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(S3SolrExceptionRecordRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate solr exception record to FTP.
     *
     * @param context         the context
     * @param solrReportsPath the s3 solrReportsPath
     */
    @Autowired
    public S3SolrExceptionRecordRouteBuilder(CamelContext context, @Value("${ftp.solr.reports.dir}") String solrReportsPath) {

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapCommonConstants.FTP_SOLR_EXCEPTION_REPORT_Q)
                            .routeId(RecapCommonConstants.FTP_SOLR_EXCEPTION_REPORT_ROUTE_ID)
                            .marshal().bindy(BindyType.Csv, SolrExceptionReportReCAPCSVRecord.class)
                            .setHeader(S3Constants.KEY,simple("reports/share/etl/${in.header.fileName}-${date:now:ddMMMyyyyHHmmss}.csv"))
                            .to("aws-s3://{{scsbReports}}?autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                            .onCompletion().log("File has been uploaded to ftp successfully.");
                }
            });
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
    }
}
