package com.example.gym.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DataSourceDTO {

    private String SQL;
    private String DB_URL;
    private String USER;
    private String PASS;
    private String DRIVER;
}
