package com.bojie.currencybo.helper;

import com.bojie.currencybo.Constants;
import com.bojie.currencybo.value_objects.Currency;

import org.json.JSONObject;

/**
 * Created by bojiejiang on 1/20/16.
 */
public class CurrencyParserHelper {

    public static Currency parseCurrency(JSONObject obj, String currencyName) {
        Currency currency = new Currency();
        currency.setBase(obj.optString(Constants.BASE));
        currency.setDate(obj.optString(Constants.DATE));
        JSONObject rateObject = obj.optJSONObject(Constants.RATES);
        if(rateObject != null) {
            currency.setRate(rateObject.optDouble(currencyName));
        }
        currency.setName(currencyName);
        return currency;
    }
}
