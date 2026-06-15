package com.okanetransfer.service.dto.devise.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Réponse de https://open.er-api.com/v6/latest/EUR — seuls les champs utiles
 * (result, rates) sont mappés, le reste est ignoré.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateApiResponse {

    private String result;
    private Map<String, BigDecimal> rates;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map<String, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<String, BigDecimal> rates) {
        this.rates = rates;
    }
}
