package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApiServiceTests {

    private static final String BITCOIN = "bitcoin";
    private static final String INVALID_CURRENCY = "bitcoi";

    private ApiService apiService;

    @BeforeEach
    public void init() {
        apiService = new ApiService();
    }

    @Test
    public void test_valid_api_request() throws Exception {
        CurrencyData currencyData = apiService.getCurrencyData(BITCOIN);
    }

    @Test
    public void test_invalid_id_throws_not_found_exception() throws Exception {
        assertThrows(CurrencyNotFoundException.class, () -> {
            apiService.getCurrencyData(INVALID_CURRENCY);
        });
    }
}
