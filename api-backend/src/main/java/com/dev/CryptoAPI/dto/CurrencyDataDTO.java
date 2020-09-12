package com.dev.CryptoAPI.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CurrencyDataDTO {

    private String id;
    private String symbol;
    private String name;
    private String genesis_date;
    private String last_updated;
    private MarketDataDTO market_data;
}
