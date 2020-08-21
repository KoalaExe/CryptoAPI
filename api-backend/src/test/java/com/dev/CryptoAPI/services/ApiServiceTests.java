package com.dev.CryptoAPI.services;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ApiServiceTests {

    private static final String API_URL = "http://localhost:9999";
    private static final String BITCOIN = "bitcoin";
    private static final String INVALID_CURRENCY = "bitcoi";
    private static final String USD = "usd";

    private static final int VALID_LIMIT = 10;
    private static final int INVALID_LIMIT = 15;
    private static final int PAGE_NUMBER_ONE = 1;
    private static final int PAGE_NUMBER_TWO = 2;

    private ApiService apiService;

    @BeforeEach
    public void init() {
        apiService = new ApiService(API_URL);
    }

    @Test
    public void test_get_currency_data_with_valid_currency() throws Exception {
        CurrencyData currencyData = apiService.getCurrencyData(BITCOIN);

        assertEquals(currencyData.getId(), "bitcoin");
        assertEquals(currencyData.getSymbol(), "btc");
        assertEquals(currencyData.getName(), "Bitcoin");
        assertEquals(currencyData.getMarketCap(), "225371848920");
        assertEquals(currencyData.getGenesisDate(), "03-01-2009");
        assertEquals(currencyData.getLastUpdate(), "18-08-2020");

        assertEquals(currencyData.getCurrentPrices().get("aud"), "16868.56");
        assertEquals(currencyData.getCurrentPrices().get("usd"), "12207.18");
        assertEquals(currencyData.getCurrentPrices().get("jpy"), "1287797.0");
        assertEquals(currencyData.getCurrentPrices().get("btc"), "1.0");

        assertEquals(currencyData.getPricePercentageChange().get("aud"), "1.86061");
        assertEquals(currencyData.getPricePercentageChange().get("usd"), "2.57275");
        assertEquals(currencyData.getPricePercentageChange().get("jpy"), "1.81386");
        assertEquals(currencyData.getPricePercentageChange().get("btc"), "0.0");

        assertEquals(currencyData.getLastWeekPrice().get("aud"), "15951.186295908086");
        assertEquals(currencyData.getLastWeekPrice().get("usd"), "11398.671060896633");
        assertEquals(currencyData.getLastWeekPrice().get("jpy"), "1213955.2112711808");
        assertEquals(currencyData.getLastWeekPrice().get("btc"), "1.0");
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
        List<PaginatedCurrencyData> paginatedCurrencyDataList = apiService.getPaginatedCurrencyDataList(USD, VALID_LIMIT, PAGE_NUMBER_ONE);

        assertNotNull(paginatedCurrencyDataList);
        assertTrue(paginatedCurrencyDataList.size() <= VALID_LIMIT);
    }

    @Test
    public void test_get_paginated_data_with_invalid_currency_throws_currency_exception() {
        Exception invalidCurrencyException = assertThrows(Exception.class, () -> {
            apiService.getPaginatedCurrencyDataList(INVALID_CURRENCY, VALID_LIMIT, PAGE_NUMBER_ONE);
        });

        assertTrue(invalidCurrencyException.getMessage().contains("Invalid currency! Use usd, aud or jpy"));
    }

    @Test
    public void test_get_paginated_data_with_invalid_limit_throws_exception() {
        Exception invalidCurrencyException = assertThrows(Exception.class, () -> {
            apiService.getPaginatedCurrencyDataList(USD, INVALID_LIMIT, PAGE_NUMBER_ONE);
        });

        assertTrue(invalidCurrencyException.getMessage().contains("Pagination limit out of range!"));
    }

    @Test
    public void test_get_paginated_data_that_returns_inconsistent_data_throws_exception() {
        Exception invalidCurrencyException = assertThrows(CurrencyNotFoundException.class, () -> {
            apiService.getPaginatedCurrencyDataList(USD, VALID_LIMIT, PAGE_NUMBER_TWO);
        });

        assertTrue(invalidCurrencyException.getMessage().contains(" status updates were not found!"));
    }
}
