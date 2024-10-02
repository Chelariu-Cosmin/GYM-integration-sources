package com.example.gym.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@Data
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressView implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("address_id")
    private Long address_id;
    @JsonProperty("street_number")
    private String street_number;
    @JsonProperty("street_name")
    private String street_name;
    private String city;

    @JsonProperty("country_id")
    private Long countryId;

    @JsonIgnore
    @XmlTransient
    private CountryView country;
}
