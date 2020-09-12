package com.dev.CryptoAPI.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
public class CurrencyData {

    @NonNull
    private String id;
    private String symbol;
    private String name;
    private String genesisDate;
    private String lastUpdate;
    private MarketCap marketCap;
    private Map<String, String> currentPrices = new HashMap<>();
    private Map<String, String> pricePercentageChange = new HashMap<>();
    private Map<String, String> lastWeekPrice = new HashMap<>();
}
