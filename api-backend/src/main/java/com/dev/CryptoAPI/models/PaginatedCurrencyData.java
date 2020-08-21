package com.dev.CryptoAPI.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PaginatedCurrencyData {

    @NonNull @Getter @Setter
    private String id;

    @Getter @Setter
    private String currentPrice;

    @Getter @Setter
    private String marketCap;

    @Getter @Setter
    private List<Map<String, String>> statusUpdates = new ArrayList<>();
}
