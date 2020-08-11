package com.dev.CryptoAPI.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CurrencyData {

    @NonNull @Getter @Setter
    private String id;

    @Getter @Setter
    private String symbol;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String marketCap;

    @Getter @Setter
    private String genesisDate;

    @Getter @Setter
    private String lastUpdate;

    @Getter @Setter
    private Map<String, String> currentPrices = new HashMap<>();

    @Getter @Setter
    private Map<String, String> pricePercentageChange = new HashMap<>();

    @Getter @Setter
    private Map<String, String> lastWeekPrice = new HashMap<>();
}
