package com.dev.CryptoAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MarketCapDTO {

    private Long aud;
    private Long usd;
    private Long jpy;
}
