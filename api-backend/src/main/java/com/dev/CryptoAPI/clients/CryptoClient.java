package com.dev.CryptoAPI.clients;

import com.dev.CryptoAPI.dto.CurrencyDataDTO;
import com.dev.CryptoAPI.dto.CurrencyHistoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "currencydata", url = "https://api.coingecko.com/api/v3")
public interface CryptoClient {

    @GetMapping("/coins/{id}")
    CurrencyDataDTO getCurrencyData(@PathVariable String id);

    @GetMapping("/coins/{id}/history")
    CurrencyHistoryDTO getCurrencyHistory(@PathVariable String id, @RequestParam(name = "date") String date);
}
