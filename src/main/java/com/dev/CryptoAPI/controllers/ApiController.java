package com.dev.CryptoAPI.controllers;

import com.dev.CryptoAPI.services.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/coins/{id}")
    public ResponseEntity<?> getCoin(@PathVariable("id") String currencyId) {
        return null;
    }
}
