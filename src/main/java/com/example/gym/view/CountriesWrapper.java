package com.example.gym.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountriesWrapper {

    @JsonProperty("countries")
    private CountriesView countriesView;
}
