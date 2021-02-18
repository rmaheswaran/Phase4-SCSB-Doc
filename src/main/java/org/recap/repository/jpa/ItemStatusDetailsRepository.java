package org.recap.repository.jpa;

import org.recap.model.jpa.ItemStatusEntity;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface ItemStatusDetailsRepository extends BaseRepository<ItemStatusEntity> {

    /**
     * Finds item status entity by using item status code.
     *
     * @param statusCode the status code
     * @return the item status entity
     */
    ItemStatusEntity findByStatusCode(String statusCode);
}
