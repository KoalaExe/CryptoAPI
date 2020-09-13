package com.dev.CryptoAPI.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaginatedCurrencyData {

    private String id;
    private String currentPrice;
    private String marketCap;
    private List<StatusUpdate> statusUpdates;
}
