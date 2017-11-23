package org.recap.converter;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.Holding;
import org.recap.model.jaxb.Holdings;
import org.recap.model.jaxb.Items;
import org.recap.model.jaxb.marc.CollectionType;
import org.recap.model.jaxb.marc.ContentType;
import org.recap.model.jaxb.marc.LeaderFieldType;
import org.recap.model.jaxb.marc.RecordType;
import org.recap.model.jpa.*;
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
 * Created by premkb on 15/12/16.
 */
@Service
public class SCSBToBibEntityConverter implements XmlToBibEntityConverterInterface {

    private static final Logger logger = LoggerFactory.getLogger(SCSBToBibEntityConverter.class);

    @Autowired
    private DBReportUtil dbReportUtil;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private MarcUtil marcUtil;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private Map itemStatusMap;
    private Map collectionGroupMap;
    private Map institutionEntityMap;

    /**
     * This method is used to convert scsb record into bib entity
     * @param scsbRecord
     * @param institutionName the institution name
     * @param accessionRequest    the customer code
     * @return
     */
    @Override
    public Map convert(Object scsbRecord, String institutionName, AccessionRequest accessionRequest){
        int failedItemCount = 0;
        int successItemCount = 0;
        String reasonForFailureItem = "";
        Map<String, Object> map = new HashMap<>();
        String incompleteResponse = "";
        List<HoldingsEntity> holdingsEntities = new ArrayList<>();
        List<ItemEntity> itemEntities = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();

        getDbReportUtil().setInstitutionEntitiesMap(getInstitutionEntityMap());
        getDbReportUtil().setCollectionGroupMap(getCollectionGroupMap());

        try {
            BibRecord bibRecord = (BibRecord) scsbRecord;
            Integer owningInstitutionId = (Integer) getInstitutionEntityMap().get(institutionName);
            Date currentDate = new Date();

            Map<String, Object> bibMap = processAndValidateBibliographicEntity(bibRecord, owningInstitutionId, currentDate,errorMessage);
            BibliographicEntity bibliographicEntity = (BibliographicEntity) bibMap.get("bibliographicEntity");

            map.put(RecapConstants.FAILED_BIB_COUNT, bibMap.get(RecapConstants.FAILED_BIB_COUNT));
            map.put(RecapConstants.SUCCESS_BIB_COUNT , bibMap.get(RecapConstants.SUCCESS_BIB_COUNT));
            map.put(RecapConstants.REASON_FOR_BIB_FAILURE , bibMap.get(RecapConstants.REASON_FOR_BIB_FAILURE));
            map.put(RecapConstants.EXIST_BIB_COUNT , bibMap.get(RecapConstants.EXIST_BIB_COUNT));

            List<Holdings> holdingsList = bibRecord.getHoldings();
            if (errorMessage.length()==0) {
                for(Holdings holdings:holdingsList){
                    List<Holding> holdingList=null;
                    if (holdings.getHolding()!=null) {
                        holdingList = holdings.getHolding();
                    } else {
                        logger.error("holding is empty---"+bibRecord.getBib().getOwningInstitutionBibId());
                    }
                    for(Holding holding:holdingList){
                        if (holding.getContent() != null) {
                            CollectionType holdingContentCollection = holding.getContent().getCollection();
                            List<RecordType> holdingRecordTypes = holdingContentCollection.getRecord();
                            RecordType holdingsRecordType = holdingRecordTypes.get(0);

                            Map<String, Object> holdingsMap = processAndValidateHoldingsEntity(bibliographicEntity, holding, holdingContentCollection,currentDate,errorMessage);
                            HoldingsEntity holdingsEntity = (HoldingsEntity) holdingsMap.get("holdingsEntity");
                            if (errorMessage.length()==0) {
                                holdingsEntities.add(holdingsEntity);
                            }
                            String holdingsCallNumber = getMarcUtil().getDataFieldValueForRecordType(holdingsRecordType, "852", null, null, "h");
                            if(holdingsCallNumber == null){
                                holdingsCallNumber = "";
                            }
                            String holdingsCallNumberType = getMarcUtil().getInd1ForRecordType(holdingsRecordType, "852", "h");

                            List<Items> items = holding.getItems();
                            for (Items item : items) {
                                ContentType itemContent = item.getContent();
                                CollectionType itemContentCollection = itemContent.getCollection();

                                List<RecordType> itemRecordTypes = itemContentCollection.getRecord();
                                for (RecordType itemRecordType : itemRecordTypes) {
                                    Map<String, Object> itemMap = processAndValidateItemEntity(bibliographicEntity, owningInstitutionId, holdingsCallNumber, holdingsCallNumberType, itemRecordType,accessionRequest,currentDate,errorMessage);
                                    if (itemMap != null) {
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
                                                    reasonForFailureItem = stringBuilder.toString();
                                                }

                                            }
                                        }
                                        if(itemMap.containsKey(RecapConstants.SUCCESS_ITEM_COUNT)){
                                            successItemCount = successItemCount + (int) itemMap.get(RecapConstants.SUCCESS_ITEM_COUNT);
                                        }
                                        ItemEntity itemEntity = (ItemEntity) itemMap.get("itemEntity");
                                        if (errorMessage.length()==0) {
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
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            errorMessage.append(e.getMessage());
        }

        if(errorMessage.length()>0) {//Added to remove "," from the error message
            errorMessage = new StringBuilder(errorMessage.substring(0,errorMessage.length()-1));
        }
        map.put("errorMessage",errorMessage);
        return map;
    }

    /**
     * This method is used to validate all necessary bib record fields
     * @param bibRecord
     * @param owningInstitutionId
     * @param currentDate
     * @return
     */
    private Map<String, Object> processAndValidateBibliographicEntity(BibRecord bibRecord,Integer owningInstitutionId,Date currentDate,StringBuilder errorMessage) {
        int failedBibCount = 0;
        int successBibCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        Map<String, Object> map = new HashMap<>();

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        String owningInstitutionBibId = bibRecord.getBib().getOwningInstitutionBibId();

        if (StringUtils.isNotBlank(owningInstitutionBibId)) {
            bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        } else {
            errorMessage.append("Owning Institution Bib Id cannot be null").append(",");
        }
        if (owningInstitutionId != null) {
            bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        } else {
            errorMessage.append(" Owning Institution Id cannot be null ").append(",");
        }
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCreatedDate(currentDate);
        bibliographicEntity.setCreatedBy(RecapConstants.ACCESSION);
        bibliographicEntity.setLastUpdatedDate(currentDate);
        bibliographicEntity.setLastUpdatedBy(RecapConstants.ACCESSION);

        ContentType bibContent = bibRecord.getBib().getContent();
        CollectionType bibContentCollection = bibContent.getCollection();
        String bibXmlContent = bibContentCollection.serialize(bibContentCollection);
        if (!StringUtils.isEmpty(bibXmlContent)) {
            bibliographicEntity.setContent(bibXmlContent.getBytes());
        } else {
            errorMessage.append(" Bib Content cannot be empty").append(",");
        }

        boolean subFieldExistsFor245 = getMarcUtil().isSubFieldExists(bibContentCollection.getRecord().get(0), "245");

        if (!subFieldExistsFor245) {
            errorMessage.append(" At least one subfield should be there for 245 tag").append(",");
        }

        LeaderFieldType leader = bibContentCollection.getRecord().get(0).getLeader();
        if(leader == null){
            errorMessage.append(" Leader field is missing").append(",");
        } else if (!(leader != null && StringUtils.isNotBlank(leader.getValue()) && leader.getValue().length() == 24)) {
            errorMessage.append(" Leader field value should be 24 characters ").append(",");
        }

        if(owningInstitutionId != null && StringUtils.isNotBlank(owningInstitutionBibId)){
            BibliographicEntity existBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(owningInstitutionId,owningInstitutionBibId);
            if(null != existBibliographicEntity){
                exitsBibCount = 1;
            }
        }
        if(errorMessage.toString().length()==0){
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
     * This method is used to validate all necessary holdings fields required in the bib record.
     * @param bibliographicEntity
     * @param holding
     * @param holdingContentCollection
     * @param currentDate
     * @param errorMessage
     * @return
     */
    private Map<String, Object> processAndValidateHoldingsEntity(BibliographicEntity bibliographicEntity, Holding holding, CollectionType holdingContentCollection,Date currentDate,StringBuilder errorMessage) {
        Map<String, Object> map = new HashMap<>();
        HoldingsEntity holdingsEntity = new HoldingsEntity();

        String holdingsContent = holdingContentCollection.serialize(holdingContentCollection);
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
        String owningInstitutionHoldingsId = holding.getOwningInstitutionHoldingsId();
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
     * This method is used to validate all necessary fields required for item.
     *
     * @param bibliographicEntity
     * @param owningInstitutionId
     * @param holdingsCallNumber
     * @param holdingsCallNumberType
     * @param itemRecordType
     * @param accessionRequest
     * @param currentDate
     * @param errorMessage
     * @return
     */
    private Map<String, Object> processAndValidateItemEntity(BibliographicEntity bibliographicEntity, Integer owningInstitutionId,
                                                             String holdingsCallNumber, String holdingsCallNumberType, RecordType itemRecordType,AccessionRequest accessionRequest,
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
        String itemBarcode = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "p");
        if (accessionRequest.getItemBarcode().equals(itemBarcode)) {//This is to avoid creation of multiple items when response from partner service is having 1Bib 1Hold n items, accession should be done for one item which comes in the request and should not be done for other items which is linked with the same bib
            if (StringUtils.isNotBlank(itemBarcode)) {
                itemEntity.setBarcode(itemBarcode);
                map.put("itemBarcode",itemBarcode);
            } else {
                errorMessage.append("Item Barcode cannot be null").append(",");
            }
            itemEntity.setCustomerCode(accessionRequest.getCustomerCode());
            itemEntity.setCallNumber(holdingsCallNumber);
            itemEntity.setCallNumberType(holdingsCallNumberType != null ? String.valueOf(holdingsCallNumberType) : "");
            String copyNumber = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "t");
            if (StringUtils.isNotBlank(copyNumber) && org.apache.commons.lang3.math.NumberUtils.isNumber(copyNumber)) {
                itemEntity.setCopyNumber(Integer.valueOf(copyNumber));
            }
            if (owningInstitutionId != null) {
                itemEntity.setOwningInstitutionId(owningInstitutionId);
            } else {
                errorMessage.append("\n");
                errorMessage.append("Owning Institution Id cannot be null").append(",");
            }
            String collectionGroupCode = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "900", null, null, "a");
            if (StringUtils.isNotBlank(collectionGroupCode) && collectionGroupMap.containsKey(collectionGroupCode)) {
                itemEntity.setCollectionGroupId((Integer) collectionGroupMap.get(collectionGroupCode));
            } else {
                itemEntity.setCollectionGroupId((Integer) collectionGroupMap.get("Open"));
            }
            itemEntity.setDeleted(false);
            itemEntity.setCreatedDate(currentDate);
            itemEntity.setCreatedBy(RecapConstants.ACCESSION);
            itemEntity.setLastUpdatedDate(currentDate);
            itemEntity.setLastUpdatedBy(RecapConstants.ACCESSION);

            String useRestrictions = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "h");
            if (StringUtils.isNotBlank(useRestrictions) && ("In Library Use".equalsIgnoreCase(useRestrictions) || "Supervised Use".equalsIgnoreCase(useRestrictions))) {
                itemEntity.setUseRestrictions(useRestrictions);
            } else if(null == useRestrictions){
                isComplete = false;
            }

            itemEntity.setVolumePartYear(getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "3"));
            String owningInstitutionItemId = getMarcUtil().getDataFieldValueForRecordType(itemRecordType, "876", null, null, "a");
            if (StringUtils.isNotBlank(owningInstitutionItemId)) {
                itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
            } else {
                errorMessage.append("Item Owning Institution Id cannot be null").append(",");
            }

            if(isComplete){
                itemEntity.setItemAvailabilityStatusId((Integer) getItemStatusMap().get("Available"));
                bibliographicEntity.setCatalogingStatus(RecapConstants.COMPLETE_STATUS);
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
        return null;
    }

    /**
     * This method gets all item status and puts it in a map.
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
     * This method gets all collection group and puts it in a map.
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
     * This method gets all institution entity and puts it in a map.
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
     * This method gets db report util.
     *
     * @return the db report util
     */
    public DBReportUtil getDbReportUtil() {
        return dbReportUtil;
    }

    /**
     * This method sets db report util.
     *
     * @param dbReportUtil the db report util
     */
    public void setDbReportUtil(DBReportUtil dbReportUtil) {
        this.dbReportUtil = dbReportUtil;
    }

    /**
     * Gets marc util.
     *
     * @return the marc util
     */
    public MarcUtil getMarcUtil() {
        if (null == marcUtil) {
            marcUtil = new MarcUtil();
        }
        return marcUtil;
    }
}
