package com.dev.CryptoAPI.controllers;

import com.dev.CryptoAPI.exceptions.CurrencyNotFoundException;
import com.dev.CryptoAPI.models.CurrencyData;
import com.dev.CryptoAPI.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
            response = new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response = new ResponseEntity<String>(HttpStatus.UNPROCESSABLE_ENTITY);
            if(e.getMessage() != null) {
                response = new ResponseEntity<String>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        return response;
    }
}
