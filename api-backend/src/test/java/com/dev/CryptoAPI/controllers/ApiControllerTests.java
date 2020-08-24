package com.dev.CryptoAPI.controllers;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import com.dev.CryptoAPI.services.ApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApiControllerTests {

    private static final String CURRENCY_ONE = "Currency 1";
    private static final String INVALID_CURRENCY = "bitcoi";
    private static final String INVALID_CURRENCY_EXCEPTION = "Currency not found!";

    private static final String USD = "usd";
    private static final int VALID_LIMIT = 10;
    private static final int INVALID_LIMIT = 15;
    private static final int PAGE_NUMBER = 1;

    @Mock
    private ApiService mockApiService;

    private ApiController apiController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);

        apiController = new ApiController(mockApiService);
    }

    @Test
    public void test_get_coin_handles_valid_request() throws Exception {
        CurrencyData validData = new CurrencyData(CURRENCY_ONE);

        when(mockApiService.getCurrencyData(CURRENCY_ONE)).thenReturn(validData);

        ResponseEntity<?> response = apiController.getCoin(CURRENCY_ONE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(validData, response.getBody());
    }

    @Test
    public void test_get_coin_caught_currency_not_found_exception() throws Exception {
        when(mockApiService.getCurrencyData(INVALID_CURRENCY)).thenThrow(new CurrencyNotFoundException(INVALID_CURRENCY_EXCEPTION));

        ResponseEntity<?> response = apiController.getCoin(INVALID_CURRENCY);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404", ((Map<String, String>) response.getBody()).get("status"));
        assertEquals(INVALID_CURRENCY_EXCEPTION, ((Map<String, String>) response.getBody()).get("message"));
    }

    @Test
    public void test_get_coin_caught_other_exception() throws Exception {
        when(mockApiService.getCurrencyData(INVALID_CURRENCY)).thenThrow(Exception.class);

        ResponseEntity<?> response = apiController.getCoin(INVALID_CURRENCY);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("422", ((Map<String, String>) response.getBody()).get("status"));
    }

    @Test
    public void test_get_coin_caught_other_exception_with_message() throws Exception {
        when(mockApiService.getCurrencyData(INVALID_CURRENCY)).thenThrow(new Exception("Error"));

        ResponseEntity<?> response = apiController.getCoin(INVALID_CURRENCY);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("422", ((Map<String, String>) response.getBody()).get("status"));
        assertEquals("Error", ((Map<String, String>) response.getBody()).get("message"));
    }

    @Test
    public void test_get_paginated_data_with_valid_parameters_returns_response_status_ok() throws Exception {
        List<PaginatedCurrencyData> currencyData = new ArrayList<>();

        when(mockApiService.getPaginatedCurrencyDataList(USD, VALID_LIMIT, PAGE_NUMBER)).thenReturn(currencyData);

        ResponseEntity<?> response = apiController.getPaginatedCurrencyData(USD, VALID_LIMIT, PAGE_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(currencyData, response.getBody());
    }

    @Test
    public void test_get_paginated_data_caught_currency_not_found_exception() throws Exception {
        when(mockApiService.getPaginatedCurrencyDataList("ABC", VALID_LIMIT, PAGE_NUMBER)).thenThrow(new CurrencyNotFoundException(INVALID_CURRENCY_EXCEPTION));

        ResponseEntity<?> response = apiController.getPaginatedCurrencyData("ABC", VALID_LIMIT, PAGE_NUMBER);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404", ((Map<String, String>) response.getBody()).get("status"));
        assertEquals(INVALID_CURRENCY_EXCEPTION, ((Map<String, String>) response.getBody()).get("message"));
    }

    @Test
    public void test_get_paginated_data_caught_other_exception() throws Exception {
        when(mockApiService.getPaginatedCurrencyDataList(anyString(), anyInt(), anyInt())).thenThrow(Exception.class);

        ResponseEntity<?> response = apiController.getPaginatedCurrencyData(anyString(), anyInt(), anyInt());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("422", ((Map<String, String>) response.getBody()).get("status"));
    }
}
