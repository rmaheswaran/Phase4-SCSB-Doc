package org.recap.repository.jpa;

import org.recap.model.jpa.ItemStatusEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by hemalathas on 22/6/16.
 */
//@RepositoryRestResource(collectionResourceRel = "itemStatus", path = "itemStatus")
public interface ItemStatusDetailsRepository extends BaseRepository<ItemStatusEntity> {

    /**
     * Finds item status entity by using item status code.
     *
     * @param statusCode the status code
     * @return the item status entity
     */
    ItemStatusEntity findByStatusCode(String statusCode);
}
