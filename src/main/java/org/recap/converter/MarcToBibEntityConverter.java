package org.recap.converter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jpa.*;
import org.recap.model.marc.BibMarcRecord;
import org.recap.model.marc.HoldingsMarcRecord;
import org.recap.model.marc.ItemMarcRecord;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.recap.util.DBReportUtil;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chenchulakshmig on 17/10/16.
 */
@Service
public class MarcToBibEntityConverter implements XmlToBibEntityConverterInterface {

    private static final Logger logger = LoggerFactory.getLogger(MarcToBibEntityConverter.class);

    @Autowired
    private MarcUtil marcUtil;

    @Autowired
    private DBReportUtil dbReportUtil;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    private Map itemStatusMap;
    private Map collectionGroupMap;
    private Map institutionEntityMap;

    /**
     * This method uses the marc record and builds the bibliographic entity. For exceptions, adds them to report entities. Also adds failed record counts.
     *
     * @param marcRecord
     * @param institutionName the institution name
     * @param accessionRequest    the customer code
     * @return
     */
    @Override
    public Map convert(Object marcRecord, String institutionName, AccessionRequest accessionRequest) {
            int failedItemCount = 0;
            int successItemCount = 0;
            String reasonForFailureItem = "";
        Map<String, Object> map = new HashMap<>();
        String incompleteResponse = "";

        Record record = (Record) marcRecord;
        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        List<ItemEntity> itemEntities = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();

        getDbReportUtil().setInstitutionEntitiesMap(getInstitutionEntityMap());
        getDbReportUtil().setCollectionGroupMap(getCollectionGroupMap());

        BibMarcRecord bibMarcRecord = marcUtil.buildBibMarcRecord(record);
        Record bibRecord = bibMarcRecord.getBibRecord();
        Integer owningInstitutionId = (Integer) getInstitutionEntityMap().get(institutionName);
        Date currentDate = new Date();
        Map<String, Object> bibMap = processAndValidateBibliographicEntity(bibRecord, owningInstitutionId, currentDate,errorMessage);
        BibliographicEntity bibliographicEntity = (BibliographicEntity) bibMap.get(RecapConstants.BIBLIOGRAPHICENTITY);

        map.put(RecapConstants.FAILED_BIB_COUNT,(int)bibMap.get(RecapConstants.FAILED_BIB_COUNT));
        map.put(RecapConstants.SUCCESS_BIB_COUNT , (int)bibMap.get(RecapConstants.SUCCESS_BIB_COUNT));
        map.put(RecapConstants.REASON_FOR_BIB_FAILURE ,(String)bibMap.get(RecapConstants.REASON_FOR_BIB_FAILURE));
        map.put(RecapConstants.EXIST_BIB_COUNT ,(int)bibMap.get(RecapConstants.EXIST_BIB_COUNT));

        if (errorMessage.length()==0) {
            List<HoldingsMarcRecord> holdingsMarcRecords = bibMarcRecord.getHoldingsMarcRecords();
            if (CollectionUtils.isNotEmpty(holdingsMarcRecords)) {
                for (HoldingsMarcRecord holdingsMarcRecord : holdingsMarcRecords) {
                    Record holdingsRecord = holdingsMarcRecord.getHoldingsRecord();
                    Map<String, Object> holdingsMap = processAndValidateHoldingsEntity(bibliographicEntity, holdingsRecord, currentDate,errorMessage);
                    HoldingsEntity holdingsEntity = (HoldingsEntity) holdingsMap.get("holdingsEntity");
                    if (errorMessage.length()==0) {
                        holdingsEntities.add(holdingsEntity);
                    }
                    String holdingsCallNumber = marcUtil.getDataFieldValue(holdingsRecord, "852", 'h');
                    if(holdingsCallNumber == null){
                        holdingsCallNumber = "";
                    }
                    Character holdingsCallNumberType = marcUtil.getInd1(holdingsRecord, "852", 'h');

                    List<ItemMarcRecord> itemMarcRecordList = holdingsMarcRecord.getItemMarcRecordList();
                    if (CollectionUtils.isNotEmpty(itemMarcRecordList)) {
                        for (ItemMarcRecord itemMarcRecord : itemMarcRecordList) {
                            Record itemRecord = itemMarcRecord.getItemRecord();
                            Map<String, Object> itemMap = processAndValidateItemEntity(bibliographicEntity, owningInstitutionId,accessionRequest.getCustomerCode(), holdingsCallNumber, holdingsCallNumberType, itemRecord, institutionName, bibRecord, currentDate,errorMessage);
                            if(itemMap.containsKey(RecapConstants.FAILED_ITEM_COUNT)){
                                failedItemCount = failedItemCount + (int) itemMap.get(RecapConstants.FAILED_ITEM_COUNT);
                            }
                            if(itemMap.containsKey(RecapConstants.ITEMBARCODE)){
                                map.put(RecapConstants.ITEMBARCODE,(String)itemMap.get(RecapConstants.ITEMBARCODE));
                            }
                            if(itemMap.containsKey(RecapConstants.REASON_FOR_ITEM_FAILURE)){
                                String reason = (String)itemMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE);
                                if(!StringUtils.isEmpty(reason)){
                                    if(StringUtils.isEmpty(reasonForFailureItem)){
                                        reasonForFailureItem = (String) itemMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE);
                                    }else{
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(itemMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE));
                                        stringBuilder.append(",");
                                        stringBuilder.append(reasonForFailureItem);
                                        reasonForFailureItem=stringBuilder.toString();
                                    }
                                }
                            }
                            if(itemMap.containsKey(RecapConstants.SUCCESS_ITEM_COUNT)){
                                successItemCount = successItemCount + (int) itemMap.get(RecapConstants.SUCCESS_ITEM_COUNT);
                            }
                            ItemEntity itemEntity = (ItemEntity) itemMap.get("itemEntity");
                            if (errorMessage.length() == 0) {
                                if (holdingsEntity.getItemEntities() == null) {
                                    holdingsEntity.setItemEntities(new ArrayList<>());
                                }
                                holdingsEntity.getItemEntities().add(itemEntity);
                                itemEntities.add(itemEntity);
                            }
                            if(RecapConstants.INCOMPLETE_STATUS.equalsIgnoreCase(itemEntity.getCatalogingStatus())){
                                incompleteResponse = RecapConstants.INCOMPLETE_STATUS;
                            }
                        }
                    }

                }
                bibliographicEntity.setHoldingsEntities(holdingsEntities);
                bibliographicEntity.setItemEntities(itemEntities);
            }
        }

        if (errorMessage.length()==0) {
            map.put(RecapConstants.BIBLIOGRAPHICENTITY, bibliographicEntity);
        }
        map.put(RecapConstants.FAILED_ITEM_COUNT,failedItemCount);
        map.put(RecapConstants.SUCCESS_ITEM_COUNT,successItemCount);
        map.put(RecapConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
        map.put(RecapConstants.INCOMPLETE_RESPONSE,incompleteResponse);
        if(errorMessage.length()>0) {//Added to remove "," from the error message
            errorMessage = new StringBuilder(errorMessage.substring(0,errorMessage.length()-1));
        }
        map.put("errorMessage",errorMessage);

        return map;
    }

    /**
     * This method is used to validate all necessary bibRecord fields
     * @param bibRecord
     * @param owningInstitutionId
     * @param currentDate
     * @param errorMessage
     * @return
     */
    private Map<String, Object> processAndValidateBibliographicEntity(Record bibRecord, Integer owningInstitutionId,Date currentDate,StringBuilder errorMessage) {
        int failedBibCount = 0;
        int successBibCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        Map<String, Object> map = new HashMap<>();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        String owningInstitutionBibId = marcUtil.getControlFieldValue(bibRecord, "001");
        if (StringUtils.isNotBlank(owningInstitutionBibId)) {
            bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        } else {
            errorMessage.append("Owning Institution Bib Id cannot be null").append(",");
        }
        if (owningInstitutionId != null) {
            bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        } else {
            errorMessage.append("Owning Institution Id cannot be null").append(",");
        }
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCreatedDate(currentDate);
        bibliographicEntity.setCreatedBy(RecapConstants.ACCESSION);
        bibliographicEntity.setLastUpdatedDate(currentDate);
        bibliographicEntity.setLastUpdatedBy(RecapConstants.ACCESSION);

        String bibContent = marcUtil.writeMarcXml(bibRecord);
        if (StringUtils.isNotBlank(bibContent)) {
            bibliographicEntity.setContent(bibContent.getBytes());
        } else {
            errorMessage.append("Bib Content cannot be empty").append(",");
        }

        boolean subFieldExistsFor245 = marcUtil.isSubFieldExists(bibRecord, "245");
        if (!subFieldExistsFor245) {
            errorMessage.append("Atleast one subfield should be there for 245 tag").append(",");
        }
        Leader leader = bibRecord.getLeader();
        if(leader == null){
            errorMessage.append(" Leader field is missing").append(",");
        } else {
            String leaderValue = bibRecord.getLeader().toString();
            if (!(StringUtils.isNotBlank(leaderValue) && leaderValue.length() == 24)) {
                errorMessage.append("Leader Field value should be 24 characters").append(",");
            }
        }
        if(owningInstitutionId != null && StringUtils.isNotBlank(owningInstitutionBibId)){
            BibliographicEntity existBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(owningInstitutionId,owningInstitutionBibId);
            if(null != existBibliographicEntity){
                exitsBibCount = 1;
            }
        }
        List<ReportDataEntity> reportDataEntities = null;

        if (errorMessage.toString().length() > 1) {
            if(exitsBibCount == 0){
                failedBibCount = failedBibCount+1;
            }
            reasonForFailureBib = errorMessage.toString();
            reportDataEntities = getDbReportUtil().generateBibFailureReportEntity(bibliographicEntity, bibRecord);
            ReportDataEntity errorReportDataEntity = new ReportDataEntity();
            errorReportDataEntity.setHeaderName(RecapConstants.ERROR_DESCRIPTION);
            errorReportDataEntity.setHeaderValue(errorMessage.toString());
            reportDataEntities.add(errorReportDataEntity);
        }else if(exitsBibCount == 0){
            successBibCount = successBibCount+1;
        }

        map.put(RecapConstants.FAILED_BIB_COUNT , failedBibCount);
        map.put(RecapConstants.REASON_FOR_BIB_FAILURE , reasonForFailureBib);
        map.put(RecapConstants.BIBLIOGRAPHICENTITY, bibliographicEntity);
        map.put(RecapConstants.SUCCESS_BIB_COUNT,successBibCount);
        map.put(RecapConstants.EXIST_BIB_COUNT,exitsBibCount);
        return map;
    }

    /**
     * This method is used to validate all the fields in holding content.
     * @param bibliographicEntity
     * @param holdingsRecord
     * @param currentDate
     * @param errorMessage
     * @return
     */
    private Map<String, Object> processAndValidateHoldingsEntity(BibliographicEntity bibliographicEntity,  Record holdingsRecord, Date currentDate,StringBuilder errorMessage) {
        Map<String, Object> map = new HashMap<>();
        HoldingsEntity holdingsEntity = new HoldingsEntity();

        String holdingsContent = new MarcUtil().writeMarcXml(holdingsRecord);
        if (StringUtils.isNotBlank(holdingsContent)) {
            holdingsEntity.setContent(holdingsContent.getBytes());
        } else {
            errorMessage.append("Holdings Content cannot be empty").append(",");
        }
        holdingsEntity.setDeleted(false);
        holdingsEntity.setCreatedDate(currentDate);
        holdingsEntity.setCreatedBy(RecapConstants.ACCESSION);
        holdingsEntity.setLastUpdatedDate(currentDate);
        holdingsEntity.setLastUpdatedBy(RecapConstants.ACCESSION);
        Integer owningInstitutionId = bibliographicEntity.getOwningInstitutionId();
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        String owningInstitutionHoldingsId = marcUtil.getDataFieldValue(holdingsRecord, "852", '0');
        if (StringUtils.isBlank(owningInstitutionHoldingsId)) {
            owningInstitutionHoldingsId = UUID.randomUUID().toString();
        } else if (owningInstitutionHoldingsId.length() > 100) {
            owningInstitutionHoldingsId = UUID.randomUUID().toString();
        }
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);
        map.put("holdingsEntity", holdingsEntity);
        return map;
    }

    /**
     * This method is used to validate all the fields in item.
     * @param bibliographicEntity
     * @param owningInstitutionId
     * @param customerCode
     * @param holdingsCallNumber
     * @param holdingsCallNumberType
     * @param itemRecord
     * @param institutionName
     * @param bibRecord
     * @param currentDate
     * @param errorMessage
     * @return
     */
    private Map<String, Object> processAndValidateItemEntity(BibliographicEntity bibliographicEntity, Integer owningInstitutionId,String customerCode, String holdingsCallNumber, Character holdingsCallNumberType, Record itemRecord, String institutionName, Record bibRecord,
                                                             Date currentDate,StringBuilder errorMessage) {
        Map<String, Object> map = new HashMap<>();
        ItemEntity itemEntity = new ItemEntity();
        int failedItemCount = 0;
        int successItemCount = 0;
        boolean isComplete = true;
        String reasonForFailureItem = "";
        map.put(RecapConstants.FAILED_ITEM_COUNT,failedItemCount);
        map.put(RecapConstants.SUCCESS_ITEM_COUNT,successItemCount);
        map.put(RecapConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
        String itemBarcode = marcUtil.getDataFieldValue(itemRecord, "876", 'p');
        if (StringUtils.isNotBlank(itemBarcode)) {
            itemEntity.setBarcode(itemBarcode);
            map.put("itemBarcode",itemBarcode);
        } else {
            errorMessage.append("Item Barcode cannot be null").append(",");
        }

        itemEntity.setCustomerCode(customerCode);
        itemEntity.setCallNumber(holdingsCallNumber);
        itemEntity.setCallNumberType(holdingsCallNumberType != null ? String.valueOf(holdingsCallNumberType) : "");
        String copyNumber = marcUtil.getDataFieldValue(itemRecord, "876", 't');
        if (StringUtils.isNotBlank(copyNumber) && org.apache.commons.lang3.math.NumberUtils.isNumber(copyNumber)) {
            itemEntity.setCopyNumber(Integer.valueOf(copyNumber));
        }
        if (owningInstitutionId != null) {
            itemEntity.setOwningInstitutionId(owningInstitutionId);
        } else {
            errorMessage.append("Owning Institution Id cannot be null").append(",");
        }
        String collectionGroupCode = marcUtil.getDataFieldValue(itemRecord, "876", 'x');
        if (StringUtils.isNotBlank(collectionGroupCode) && getCollectionGroupMap().containsKey(collectionGroupCode)) {
            itemEntity.setCollectionGroupId((Integer) getCollectionGroupMap().get(collectionGroupCode));
        } else {
            isComplete = false;
            itemEntity.setCollectionGroupId((Integer) getCollectionGroupMap().get(RecapConstants.NOT_AVAILABLE_CGD));
        }
        itemEntity.setDeleted(false);
        itemEntity.setCreatedDate(currentDate);
        itemEntity.setCreatedBy(RecapConstants.ACCESSION);
        itemEntity.setLastUpdatedDate(currentDate);
        itemEntity.setLastUpdatedBy(RecapConstants.ACCESSION);

        String useRestrictions = marcUtil.getDataFieldValue(itemRecord, "876", 'h');
        if (StringUtils.isNotBlank(useRestrictions) && ("In Library Use".equalsIgnoreCase(useRestrictions) || "Supervised Use".equalsIgnoreCase(useRestrictions))) {
            itemEntity.setUseRestrictions(useRestrictions);
        } else if(null == useRestrictions){
            isComplete = false;
        }

        itemEntity.setVolumePartYear(marcUtil.getDataFieldValue(itemRecord, "876", '3'));
        String owningInstitutionItemId = marcUtil.getDataFieldValue(itemRecord, "876", 'a');
        if (StringUtils.isNotBlank(owningInstitutionItemId)) {
            itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        } else {
            errorMessage.append("Item Owning Institution Id cannot be null").append(",");
        }

        if(isComplete){
            bibliographicEntity.setCatalogingStatus(RecapConstants.COMPLETE_STATUS);
            itemEntity.setItemAvailabilityStatusId((Integer) getItemStatusMap().get("Available"));
            itemEntity.setCatalogingStatus(RecapConstants.COMPLETE_STATUS);
        } else {
            itemEntity.setItemAvailabilityStatusId((Integer) getItemStatusMap().get("Not Available"));
            bibliographicEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
            itemEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
        }
        if (errorMessage.toString().length() > 1) {
            if(map.containsKey(RecapConstants.FAILED_ITEM_COUNT)){
                failedItemCount = ((int) map.get(RecapConstants.FAILED_ITEM_COUNT)) + 1;
                map.put(RecapConstants.FAILED_ITEM_COUNT,failedItemCount);
            }
            if(map.containsKey(RecapConstants.REASON_FOR_ITEM_FAILURE)){
                reasonForFailureItem = errorMessage.toString();
                map.put(RecapConstants.REASON_FOR_ITEM_FAILURE,reasonForFailureItem);
            }

        }else{
            if(map.containsKey(RecapConstants.SUCCESS_ITEM_COUNT)){
                successItemCount = (int) map.get(RecapConstants.SUCCESS_ITEM_COUNT) + 1;
                map.put(RecapConstants.SUCCESS_ITEM_COUNT,successItemCount);
            }
        }
        map.put("itemEntity", itemEntity);
        return map;
    }

    /**
     * Gets all itemstatus and put it in a map.
     *
     * @return the item status map
     */
    public Map getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
                for (Iterator iterator = itemStatusEntities.iterator(); iterator.hasNext(); ) {
                    ItemStatusEntity itemStatusEntity = (ItemStatusEntity) iterator.next();
                    itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getItemStatusId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }
        return itemStatusMap;
    }

    /**
     * Gets all collection group and put it in a map.
     *
     * @return the collection group map
     */
    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
                for (Iterator iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                    CollectionGroupEntity collectionGroupEntity = (CollectionGroupEntity) iterator.next();
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getCollectionGroupId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }
        return collectionGroupMap;
    }

    /**
     * Gets all institution entity and puts them in a map.
     *
     * @return the institution entity map
     */
    public Map getInstitutionEntityMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap();
            try {
                Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
                for (Iterator iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                    InstitutionEntity institutionEntity = (InstitutionEntity) iterator.next();
                    institutionEntityMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getInstitutionId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }
        return institutionEntityMap;
    }

    /**
     * Gets db report util.
     *
     * @return the db report util
     */
    public DBReportUtil getDbReportUtil() {
        return dbReportUtil;
    }

    /**
     * Sets db report util.
     *
     * @param dbReportUtil the db report util
     */
    public void setDbReportUtil(DBReportUtil dbReportUtil) {
        this.dbReportUtil = dbReportUtil;
    }
}
