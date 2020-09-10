package com.dev.CryptoAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketDataDTO {

    private MarketCapDTO market_cap;
    private CurrencyValueDTO current_price;
    private CurrencyValueDTO price_change_percentage_24h_in_currency;
}
