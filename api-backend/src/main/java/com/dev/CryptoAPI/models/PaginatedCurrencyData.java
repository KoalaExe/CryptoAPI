package com.dev.CryptoAPI.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
public class PaginatedCurrencyData {

    @NonNull
    private String id;
    private String currentPrice;
    private String marketCap;
    private List<Map<String, String>> statusUpdates = new ArrayList<>();
}
