package com.dev.CryptoAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CurrencyValueDTO {

    private Number aud;
    private Number usd;
    private Number jpy;
    private Number btc;

    public Number getValue(String currency) {
        switch(currency) {
            case "aud":
                return aud;
            case "usd":
                return usd;
            case "jpy":
                return jpy;
            case "btc":
                return btc;
            default:
                return 0;
        }
    }
}
