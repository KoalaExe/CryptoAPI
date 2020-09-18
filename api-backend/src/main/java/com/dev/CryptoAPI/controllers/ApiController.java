package com.dev.CryptoAPI.controllers;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.models.ErrorResponse;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import com.dev.CryptoAPI.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/coins/{id}")
    public ResponseEntity<CurrencyData> getCoin(@PathVariable("id") String currencyId) throws CurrencyNotFoundException, Exception {
        CurrencyData currencyData = apiService.getCurrencyData(currencyId);
        ResponseEntity<CurrencyData> response = new ResponseEntity<CurrencyData>(currencyData, HttpStatus.OK);

        return response;
    }

    @GetMapping("/coins/markets")
    public ResponseEntity<List<PaginatedCurrencyData>> getPaginatedCurrencyData(@RequestParam(name = "vs_currency") String currency, @RequestParam(name = "limit", defaultValue = "10") int limit, @RequestParam(name = "page", defaultValue = "1") int page) throws CurrencyNotFoundException, Exception {
        List<PaginatedCurrencyData> paginatedData = apiService.getPaginatedCurrencyDataList(currency, limit, page);
        ResponseEntity<List<PaginatedCurrencyData>> response = new ResponseEntity<List<PaginatedCurrencyData>>(paginatedData, HttpStatus.OK);

        return response;
    }
}
