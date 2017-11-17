package org.recap.model.solr;

import java.util.Date;

/**
 * Created by SheikS on 6/18/2016.
 */
public class SolrIndexRequest {
    private String docType;
    private Integer numberOfThreads;
    private Integer numberOfDocs;
    private Integer commitInterval;
    private String owningInstitutionCode;
    private boolean doClean;
    private String dateFrom;
    private String dateTo;

    private String matchingCriteria;
    private String reportType;
    private String transmissionType;
    private Date createdDate;
    private String processType;
    private Date toDate;

    private String fromBibId;
    private String toBibId;
    private String partialIndexType;
    private String bibIds;

    private String replaceRequestByType;
    private String requestStatus;
    private String requestIds;
    private String startRequestId;
    private String endRequestId;
    private String fromDate;


    /**
     * Gets doc type.
     *
     * @return the doc type
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets doc type.
     *
     * @param docType the doc type
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * Gets number of threads.
     *
     * @return the number of threads
     */
    public Integer getNumberOfThreads() {
        return numberOfThreads;
    }

    /**
     * Sets number of threads.
     *
     * @param numberOfThreads the number of threads
     */
    public void setNumberOfThreads(Integer numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    /**
     * Gets number of docs.
     *
     * @return the number of docs
     */
    public Integer getNumberOfDocs() {
        return numberOfDocs;
    }

    /**
     * Sets number of docs.
     *
     * @param numberOfDocs the number of docs
     */
    public void setNumberOfDocs(Integer numberOfDocs) {
        this.numberOfDocs = numberOfDocs;
    }

    /**
     * Gets commit interval.
     *
     * @return the commit interval
     */
    public Integer getCommitInterval() {
        return commitInterval;
    }

    /**
     * Sets commit interval.
     *
     * @param commitInterval the commit interval
     */
    public void setCommitInterval(Integer commitInterval) {
        this.commitInterval = commitInterval;
    }

    /**
     * Gets owning institution code.
     *
     * @return the owning institution code
     */
    public String getOwningInstitutionCode() {
        return owningInstitutionCode;
    }

    /**
     * Sets owning institution code.
     *
     * @param owningInstitutionCode the owning institution code
     */
    public void setOwningInstitutionCode(String owningInstitutionCode) {
        this.owningInstitutionCode = owningInstitutionCode;
    }

    /**
     * Is do clean boolean.
     *
     * @return the boolean
     */
    public boolean isDoClean() {
        return doClean;
    }

    /**
     * Sets do clean.
     *
     * @param doClean the do clean
     */
    public void setDoClean(boolean doClean) {
        this.doClean = doClean;
    }

    /**
     * Gets matching criteria.
     *
     * @return the matching criteria
     */
    public String getMatchingCriteria() {
        return matchingCriteria;
    }

    /**
     * Sets matching criteria.
     *
     * @param matchingCriteria the matching criteria
     */
    public void setMatchingCriteria(String matchingCriteria) {
        this.matchingCriteria = matchingCriteria;
    }

    /**
     * Gets report type.
     *
     * @return the report type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Sets report type.
     *
     * @param reportType the report type
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    /**
     * Gets transmission type.
     *
     * @return the transmission type
     */
    public String getTransmissionType() {
        return transmissionType;
    }

    /**
     * Sets transmission type.
     *
     * @param transmissionType the transmission type
     */
    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets created date.
     *
     * @param createdDate the created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets date from.
     *
     * @return the date from
     */
    public String getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets date from.
     *
     * @param dateFrom the date from
     */
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * Gets date to.
     *
     * @return the date to
     */
    public String getDateTo() {
        return dateTo;
    }

    /**
     * Sets date to.
     *
     * @param dateTo the date to
     */
    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * Gets process type.
     *
     * @return the process type
     */
    public String getProcessType() {
        return processType;
    }

    /**
     * Sets process type.
     *
     * @param processType the process type
     */
    public void setProcessType(String processType) {
        this.processType = processType;
    }

    /**
     * Gets to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets to date.
     *
     * @param toDate the to date
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * Gets from bib id.
     *
     * @return the from bib id
     */
    public String getFromBibId() {
        return fromBibId;
    }

    /**
     * Sets from bib id.
     *
     * @param fromBibId the from bib id
     */
    public void setFromBibId(String fromBibId) {
        this.fromBibId = fromBibId;
    }

    /**
     * Gets to bib id.
     *
     * @return the to bib id
     */
    public String getToBibId() {
        return toBibId;
    }

    /**
     * Sets to bib id.
     *
     * @param toBibId the to bib id
     */
    public void setToBibId(String toBibId) {
        this.toBibId = toBibId;
    }

    /**
     * Gets partial index type.
     *
     * @return the partial index type
     */
    public String getPartialIndexType() {
        return partialIndexType;
    }

    /**
     * Sets partial index type.
     *
     * @param partialIndexType the partial index type
     */
    public void setPartialIndexType(String partialIndexType) {
        this.partialIndexType = partialIndexType;
    }

    /**
     * Gets bib ids.
     *
     * @return the bib ids
     */
    public String getBibIds() {
        return bibIds;
    }

    /**
     * Sets bib ids.
     *
     * @param bibIds the bib ids
     */
    public void setBibIds(String bibIds) {
        this.bibIds = bibIds;
    }

    /**
     * Gets replace request by type.
     *
     * @return the replace request by type
     */
    public String getReplaceRequestByType() {
        return replaceRequestByType;
    }

    /**
     * Sets replace request by type.
     *
     * @param replaceRequestByType the replace request by type
     */
    public void setReplaceRequestByType(String replaceRequestByType) {
        this.replaceRequestByType = replaceRequestByType;
    }

    /**
     * Gets request status.
     *
     * @return the request status
     */
    public String getRequestStatus() {
        return requestStatus;
    }

    /**
     * Sets request status.
     *
     * @param requestStatus the request status
     */
    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    /**
     * Gets request ids.
     *
     * @return the request ids
     */
    public String getRequestIds() {
        return requestIds;
    }

    /**
     * Sets request ids.
     *
     * @param requestIds the request ids
     */
    public void setRequestIds(String requestIds) {
        this.requestIds = requestIds;
    }

    /**
     * Gets start request id.
     *
     * @return the start request id
     */
    public String getStartRequestId() {
        return startRequestId;
    }

    /**
     * Sets start request id.
     *
     * @param startRequestId the start request id
     */
    public void setStartRequestId(String startRequestId) {
        this.startRequestId = startRequestId;
    }

    /**
     * Gets end request id.
     *
     * @return the end request id
     */
    public String getEndRequestId() {
        return endRequestId;
    }

    /**
     * Sets end request id.
     *
     * @param endRequestId the end request id
     */
    public void setEndRequestId(String endRequestId) {
        this.endRequestId = endRequestId;
    }

    /**
     * Gets from date.
     *
     * @return the from date
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * Sets from date.
     *
     * @param fromDate the from date
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }
}
