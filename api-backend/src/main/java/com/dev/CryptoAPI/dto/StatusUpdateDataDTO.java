package com.dev.CryptoAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StatusUpdateDataDTO {

    private String created_at;
    private String user_title;
    private String description;
}
