package com.dev.CryptoAPI.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CurrencyHistoryDTO {

    private HistoryMarketDataDTO  market_data;
}
