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
    public ResponseEntity<?> getCoin(@PathVariable("id") String currencyId) {
        ResponseEntity<?> response;

        try {
            CurrencyData currencyData = apiService.getCurrencyData(currencyId);
            response = new ResponseEntity<CurrencyData>(currencyData, HttpStatus.OK);
        } catch (CurrencyNotFoundException e) {
            ErrorResponse error = ErrorResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND.value())
                    .build();

            response = new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .build();

            response = new ResponseEntity<ErrorResponse>(error, HttpStatus.UNPROCESSABLE_ENTITY);
            if(e.getMessage() != null) {
                error.setMessage(e.getMessage());
                response = new ResponseEntity<ErrorResponse>(error, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        return response;
    }

    @GetMapping("/coins/markets")
    public ResponseEntity<?> getPaginatedCurrencyData(@RequestParam(name = "vs_currency") String currency, @RequestParam(name = "limit", defaultValue = "10") int limit, @RequestParam(name = "page", defaultValue = "1") int page) {
        ResponseEntity<?> response;

        try {
            List<PaginatedCurrencyData> paginatedData = apiService.getPaginatedCurrencyDataList(currency, limit, page);
            response = new ResponseEntity<List<PaginatedCurrencyData>>(paginatedData, HttpStatus.OK);
        } catch(CurrencyNotFoundException e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();

            response = new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .build();

            response = new ResponseEntity<ErrorResponse>(error, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return response;
    }
}
