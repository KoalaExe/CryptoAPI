package com.dev.CryptoAPI.models;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class CurrencyValues {

    private String aud;
    private String usd;
    private String jpy;
    private String btc;
}
