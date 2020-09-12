package com.dev.CryptoAPI.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MarketCap {

    private String aud;
    private String usd;
    private String jpy;
}
