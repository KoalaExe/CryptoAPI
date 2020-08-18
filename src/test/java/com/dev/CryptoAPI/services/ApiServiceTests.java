package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApiServiceTests {

    private static final String BITCOIN = "bitcoin";
    private static final String INVALID_CURRENCY = "bitcoi";
    private static final String USD = "usd";

    private static final int VALID_LIMIT = 10;
    private static final int INVALID_LIMIT = 15;

    private ApiService apiService;

    @BeforeEach
    public void init() {
        apiService = new ApiService();
    }

    @Test
    public void test_get_currency_data_with_valid_currency() throws Exception {
        CurrencyData currencyData = apiService.getCurrencyData(BITCOIN);

        assertNotNull(currencyData.getId());
        assertNotNull(currencyData.getSymbol());
        assertNotNull(currencyData.getName());
        assertNotNull(currencyData.getMarketCap());
        assertNotNull(currencyData.getGenesisDate());
        assertNotNull(currencyData.getLastUpdate());
        assertNotNull(currencyData.getCurrentPrices());
        assertNotNull(currencyData.getPricePercentageChange());
        assertNotNull(currencyData.getLastWeekPrice());
    }

    @Test
    public void test_get_currency_data_with_invalid_currency_id_throws_currency_not_found_exception() throws Exception {
        CurrencyNotFoundException notFoundException = assertThrows(CurrencyNotFoundException.class, () -> {
            apiService.getCurrencyData(INVALID_CURRENCY);
        });

        assertTrue(notFoundException.getMessage().contains(INVALID_CURRENCY + " was not found!"));
    }

    @Test
    public void test_get_paginated_data_with_valid_currency_id_limit_page_api_request() throws Exception {
        List<PaginatedCurrencyData> paginatedCurrencyDataList = apiService.getPaginatedCurrencyDataList(USD, VALID_LIMIT, 1);

        assertNotNull(paginatedCurrencyDataList);
        assertTrue(paginatedCurrencyDataList.size() <= VALID_LIMIT);
    }

    @Test
    public void test_get_paginated_data_with_invalid_currency_throws_currency_exception() {
        Exception invalidCurrencyException = assertThrows(Exception.class, () -> {
            apiService.getPaginatedCurrencyDataList(INVALID_CURRENCY, VALID_LIMIT, 1);
        });

        assertTrue(invalidCurrencyException.getMessage().contains("Invalid currency!"));
    }

    @Test
    public void test_get_paginated_data_with_invalid_limit_throws_exception() {
        Exception invalidCurrencyException = assertThrows(Exception.class, () -> {
            apiService.getPaginatedCurrencyDataList(USD, INVALID_LIMIT, 1);
        });

        assertTrue(invalidCurrencyException.getMessage().contains("Pagination limit out of range!"));
    }
}
