package com.dev.CryptoAPI.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatusUpdate {

    private String title;
    private String description;
    private String createdAt;
}
