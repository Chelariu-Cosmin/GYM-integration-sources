package com.example.gym.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@XmlRootElement(name = "countries")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountriesView implements Serializable {

    @JsonProperty("country")
    @XmlElement(name = "country")
    private List<CountryView> countries = new ArrayList<>();
}
