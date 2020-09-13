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
}
