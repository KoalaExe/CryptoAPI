package com.dev.CryptoAPI.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyDataTests {

    private static final String ID_ONE = "ID 1";
    private static final String ID_TWO = "ID 2";

    private CurrencyData currencyData;

    @BeforeEach
    public void init() {
        currencyData = new CurrencyData(ID_ONE);
    }

    @Test
    public void test_id_getters_and_setters() {
        assertEquals(ID_ONE, currencyData.getId());
        currencyData.setId(ID_TWO);
        assertEquals(ID_TWO, currencyData.getId());
    }
}
