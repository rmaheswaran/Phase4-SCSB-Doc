package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
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
public class FTPSolrExceptionRecordRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FTPSolrExceptionRecordRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate solr exception record to FTP.
     *
     * @param context         the context
     * @param ftpUserName     the ftp user name
     * @param ftpRemoteServer the ftp remote server
     * @param ftpKnownHost    the ftp known host
     * @param ftpPrivateKey   the ftp private key
     */
    @Autowired
    public FTPSolrExceptionRecordRouteBuilder(CamelContext context,
                                         @Value("${ftp.server.userName}") String ftpUserName, @Value("${ftp.solr.reports.dir}") String ftpRemoteServer,
                                         @Value("${ftp.server.knownHost}") String ftpKnownHost, @Value("${ftp.server.privateKey}") String ftpPrivateKey) {

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapCommonConstants.FTP_SOLR_EXCEPTION_REPORT_Q)
                            .routeId(RecapCommonConstants.FTP_SOLR_EXCEPTION_REPORT_ROUTE_ID)
                            .marshal().bindy(BindyType.Csv, SolrExceptionReportReCAPCSVRecord.class)
                            .to("sftp://" + ftpUserName + "@" + ftpRemoteServer + "?privateKeyFile=" + ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName=${in.header.fileName}-${date:now:ddMMMyyyy}.csv")
                            .onCompletion().log("File has been uploaded to ftp successfully.");
                }
            });
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
    }
}
