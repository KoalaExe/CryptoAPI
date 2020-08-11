package com.dev.CryptoAPI.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CurrencyDataTests {

    private static final String ID_ONE = "ID 1";

    private CurrencyData currencyData;

    @BeforeEach
    public void init() {
        currencyData = new CurrencyData(ID_ONE);
    }

    @Test
    public void test_test() {
        System.out.println("Hello");
    }
}
