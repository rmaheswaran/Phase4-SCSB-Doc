package org.recap.model.search.resolver.impl.holdings;

import org.recap.ScsbCommonConstants;
import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.Holdings;

/**
 * Created by angelind on 6/10/16.
 */
public class HoldingsIdValueResolver implements HoldingsValueResolver {

    /**
     * Returns true if field name is 'HoldingId'.
     *
     * @param field the field
     * @return
     */
    @Override
    public Boolean isInterested(String field) {
        return ScsbCommonConstants.HOLDING_ID.equals(field);
    }

    /**
     * Set Holding Id value to holdings
     *
     * @param holdings the holdings
     * @param value the value
     */
    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setHoldingsId((Integer) value);
    }
}
