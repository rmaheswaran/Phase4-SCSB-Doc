package org.recap.camel.route;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by hemalathas on 21/12/16.
 */
@Component
public class FTPSubmitCollectionSummaryReportRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FTPSubmitCollectionSummaryReportRouteBuilder.class);

    /**
     * This method instantiates a new route builder to generate submit collection summary report to the FTP.
     *
     * @param context                         the context
     * @param ftpUserName                     the ftp user name
     * @param submitCollectionPULFtpLocation  the submit collection pul ftp location
     * @param submitCollectionCULFtpLocation  the submit collection cul ftp location
     * @param submitCollectionNYPLFtpLocation the submit collection nypl ftp location
     * @param ftpKnownHost                    the ftp known host
     * @param ftpPrivateKey                   the ftp private key
     */
    @Autowired
    public FTPSubmitCollectionSummaryReportRouteBuilder(CamelContext context, PropertyUtil propertyUtil,
                                                        @Value("${ftp.server.userName}") String ftpUserName, @Value("${ftp.submit.collection.report.dir}") String submitCollectionFtpLocation,
                                                        @Value("${ftp.server.knownHost}") String ftpKnownHost, @Value("${ftp.server.privateKey}") String ftpPrivateKey) {
        try {
            String submitCollectionPULFtpLocation = propertyUtil.getPropertyByInstitutionAndKey(RecapCommonConstants.PRINCETON, "ftp.submit.collection.report.dir");
            String submitCollectionCULFtpLocation = propertyUtil.getPropertyByInstitutionAndKey(RecapCommonConstants.COLUMBIA, "ftp.submit.collection.report.dir");
            String submitCollectionNYPLFtpLocation = propertyUtil.getPropertyByInstitutionAndKey(RecapCommonConstants.NYPL, "ftp.submit.collection.report.dir");

            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_Q)
                            .routeId(RecapConstants.FTP_SUBMIT_COLLECTION_SUMMARY_REPORT_ID)
                            .marshal().bindy(BindyType.Csv, SubmitCollectionReportRecord.class)
                            .choice()
                                .when(header(RecapConstants.FILE_NAME).contains(RecapCommonConstants.PRINCETON))
                                    .to(RecapCommonConstants.SFTP + ftpUserName + RecapCommonConstants.AT + submitCollectionPULFtpLocation + RecapCommonConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapCommonConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS)
                                .when(header(RecapConstants.FILE_NAME).contains(RecapCommonConstants.COLUMBIA))
                                    .to(RecapCommonConstants.SFTP + ftpUserName + RecapCommonConstants.AT + submitCollectionCULFtpLocation + RecapCommonConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapCommonConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS)
                                .when(header(RecapConstants.FILE_NAME).contains(RecapCommonConstants.NYPL))
                                    .to(RecapCommonConstants.SFTP + ftpUserName + RecapCommonConstants.AT + submitCollectionNYPLFtpLocation + RecapCommonConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapCommonConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS)
                                .otherwise()
                                    .to(RecapCommonConstants.SFTP + ftpUserName + RecapCommonConstants.AT + submitCollectionFtpLocation + RecapCommonConstants.PRIVATE_KEY_FILE + ftpPrivateKey + RecapCommonConstants.KNOWN_HOST_FILE + ftpKnownHost + RecapConstants.SUBMIT_COLLECTION_REPORT_SFTP_OPTIONS);

                }
            });

        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
        }
    }

}
