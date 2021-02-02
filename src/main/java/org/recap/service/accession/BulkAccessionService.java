package org.recap.service.accession;

import com.google.common.collect.Lists;
import org.apache.camel.Exchange;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.accession.AccessionSummary;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.service.accession.callable.BibDataCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by sheiks on 26/05/17.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BulkAccessionService extends AccessionService{

    private static final Logger logger = LoggerFactory.getLogger(BulkAccessionService.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * The batch accession thread size.
     */
    @Value("${batch.accession.thread.size}")
    int batchAccessionThreadSize;

    @Override
    public List<AccessionResponse> doAccession(List<AccessionRequest> accessionRequestList, AccessionSummary accessionSummary, Exchange exhange) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int requestedCount = accessionRequestList.size();
        List<AccessionRequest> trimmedAccessionRequests = getTrimmedAccessionRequests(accessionRequestList);
        trimmedAccessionRequests = getAccessionHelperUtil().removeDuplicateRecord(trimmedAccessionRequests);

        int duplicateCount = requestedCount - trimmedAccessionRequests.size();
        if(duplicateCount>0){
            saveReportForDuplicateBarcodes(accessionSummary, accessionRequestList);
        }
        accessionSummary.setRequestedRecords(requestedCount);
        accessionSummary.setDuplicateRecords(duplicateCount);

        ExecutorService executorService = Executors.newFixedThreadPool(batchAccessionThreadSize);

        List<List<AccessionRequest>> partitions = Lists.partition(trimmedAccessionRequests, batchAccessionThreadSize);


            for (Iterator<List<AccessionRequest>> iterator = partitions.iterator(); iterator.hasNext(); ) {
                List<AccessionRequest> accessionRequests = iterator.next();
                List<Future> futures = new ArrayList<>();
                List<AccessionRequest> failedRequests = new ArrayList<>();
                for (Iterator<AccessionRequest> accessionRequestIterator = accessionRequests.iterator(); accessionRequestIterator.hasNext(); ) {
                    AccessionRequest accessionRequest = accessionRequestIterator.next();
                    logger.info("Processing accession for item barcode----->{}", accessionRequest.getItemBarcode());
                    // validate empty barcode ,customer code and owning institution
                    String itemBarcode = accessionRequest.getItemBarcode();
                    String customerCode = accessionRequest.getCustomerCode();
                    AccessionValidationResponse accessionValidationResponse = validateBarcodeOrCustomerCode(itemBarcode, customerCode);

                    String owningInstitution = accessionValidationResponse.getOwningInstitution();

                    if (!accessionValidationResponse.isValid()) {
                        String message = accessionValidationResponse.getMessage();
                        List<ReportDataEntity> reportDataEntityList = new ArrayList<>(getAccessionHelperUtil().createReportDataEntityList(accessionRequest, message));
                        saveReportEntity(owningInstitution, reportDataEntityList);
                        addCountToSummary(accessionSummary, message);
                        continue;
                    }

                    BibDataCallable bibDataCallable = applicationContext.getBean(BibDataCallable.class);
                    bibDataCallable.setAccessionRequest(accessionRequest);
                    bibDataCallable.setOwningInstitution(owningInstitution);
                    futures.add(executorService.submit(bibDataCallable));

                }
                for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
                    Future bibDataFuture = futureIterator.next();
                    try {
                        Object object = bibDataFuture.get();
                        if (object instanceof Set) {
                            prepareSummary(accessionSummary, object);
                        } else if (object instanceof AccessionRequest) {
                            failedRequests.add((AccessionRequest) object);
                        }

                    } catch (Exception e) {
                        logger.error(RecapCommonConstants.LOG_ERROR, e);
                        exhange.setException(e);
                    }
                }

                // Processed failed barcodes one by one
                for (Iterator<AccessionRequest> accessionRequestIterator = failedRequests.iterator(); accessionRequestIterator.hasNext(); ) {
                    AccessionRequest accessionRequest = accessionRequestIterator.next();
                    BibDataCallable bibDataCallable = applicationContext.getBean(BibDataCallable.class);
                    bibDataCallable.setAccessionRequest(accessionRequest);
                    bibDataCallable.setWriteToReport(true);
                    String owningInstitution = getOwningInstitution(accessionRequest.getCustomerCode());
                    bibDataCallable.setOwningInstitution(owningInstitution);
                    Future submit = executorService.submit(bibDataCallable);
                    try {
                        Object o = submit.get();
                        prepareSummary(accessionSummary, o);
                    } catch (Exception e) {
                        logger.error(RecapCommonConstants.LOG_ERROR, e);
                        exhange.setException(e);
                        accessionSummary.addException(1);
                    }
                }
            }
            executorService.shutdown();
            stopWatch.stop();

        logger.info("Total time taken to accession for all barcode -> {} sec",stopWatch.getTotalTimeSeconds());
        return null;
    }

    private void saveReportForDuplicateBarcodes(AccessionSummary accessionSummary, List<AccessionRequest> accessionRequestList) {
        accessionRequestList.stream()
                .collect(Collectors.groupingBy(AccessionRequest::getItemBarcode))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toSet())
                .forEach(accessionRequest -> {
                    String owningInstitution = getOwningInstitution(accessionRequest.getCustomerCode());
                    List<ReportDataEntity> reportDataEntityList = accessionHelperUtil.createReportDataEntityList(accessionRequest, RecapConstants.DUPLICATE_BARCODE_ENTRY);
                    saveReportEntity(owningInstitution, reportDataEntityList);
                });
    }

    @Override
    public BibliographicEntity saveBibRecord(BibliographicEntity fetchBibliographicEntity) {
        return accessionDAO.saveBibRecord(fetchBibliographicEntity);
    }

}
