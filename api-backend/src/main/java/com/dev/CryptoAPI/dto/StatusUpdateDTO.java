package com.dev.CryptoAPI.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class StatusUpdateDTO {

    private List<StatusUpdateDataDTO> status_updates;
}
