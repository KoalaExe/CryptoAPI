package com.dev.CryptoAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoryMarketDataDTO {

    private CurrencyValueDTO current_price;
}
