package com.dev.CryptoAPI.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class CurrencyHistoryDTO {

    private Map<String, Map<String, ? extends Number>> market_data;
}
