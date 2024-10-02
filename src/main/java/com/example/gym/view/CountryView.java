package com.example.gym.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryView implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("country_id")
    private Long country_id;

    @JsonProperty("country_name")
    private String country_name;

    @JsonProperty("addresses")
    private List<AddressView> addresses = new ArrayList<>();
}
