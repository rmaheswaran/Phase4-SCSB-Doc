package org.recap.model.search.resolver.impl.item;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

/**
 * Created by rajeshbabuk on 21/Apr/2021
 */
public class ImsLocationValueResolver implements ItemValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return RecapConstants.IMS_LOCATION_CODE.equals(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setImsLocation((String) value);
    }
}
