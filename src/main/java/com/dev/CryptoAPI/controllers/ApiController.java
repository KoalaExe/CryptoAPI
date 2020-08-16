package com.dev.CryptoAPI.controllers;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.models.PaginatedCurrencyData;
import com.dev.CryptoAPI.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    private ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/coins/{id}")
    public ResponseEntity<?> getCoin(@PathVariable("id") String currencyId) {
        ResponseEntity<?> response;

        try {
            CurrencyData currencyData = apiService.getCurrencyData(currencyId);
            response = new ResponseEntity<CurrencyData>(currencyData, HttpStatus.OK);
        } catch (CurrencyNotFoundException e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("status", "404");
            errorMap.put("message", e.getMessage());

            response = new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("status", "422");

            response = new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.UNPROCESSABLE_ENTITY);
            if(e.getMessage() != null) {
                errorMap.put("message", e.getMessage());
                response = new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        return response;
    }

    @GetMapping("/coins/market")
    public ResponseEntity<?> getPaginatedCurrencyData(@RequestParam(name = "vs_currency") String currency, @RequestParam(name = "limit", defaultValue = "10") int limit, @RequestParam(name = "page", defaultValue = "1") int page) {
        ResponseEntity<?> response;

        try {
            List<PaginatedCurrencyData> paginatedData = apiService.getPaginatedCurrencyDataList(currency, limit, page);
            response = new ResponseEntity<List<PaginatedCurrencyData>>(paginatedData, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("status", "422");
            e.printStackTrace();

            response = new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return response;
    }
}
