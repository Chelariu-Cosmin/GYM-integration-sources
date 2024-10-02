package com.example.gym.data.service;

import com.example.gym.util.JSONFileDSConnector;
import com.example.gym.view.AddressView;
import com.example.gym.view.CountriesView;
import com.example.gym.view.CountriesWrapper;
import com.example.gym.view.CountryView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CountryViewBuilder implements ICountriesViewBuilder {
    private final File jsonFile;
    private JSONFileDSConnector dataSourceConnector;
    private CountriesView views;
    private List<CountryView> countries;
    private List<AddressView> addresses;

    public CountryViewBuilder(JSONFileDSConnector dataSourceConnector) throws Exception {
        this.dataSourceConnector = dataSourceConnector;
        jsonFile = dataSourceConnector.getJSONDoc();
    }

    @Override
    public List<CountryView> getCountries() {
        return countries;
    }

    @Override
    public List<AddressView> getAddresses() {
        return addresses;
    }

    @Override
    public CountriesView getViews() {
        return views;
    }

    @Override
    public ICountriesViewBuilder build() throws Exception {
        return this.select().map();
    }

    public ICountriesViewBuilder map() {
        this.countries = this.views.getCountries();
        this.addresses = new ArrayList<>();

        for (CountryView country : countries) {
            for (AddressView cityView : country.getAddresses())
                cityView.setCountry(country);
            this.addresses.addAll(country.getAddresses());
        }

        return this;
    }

    //    @Override
//    public CountryViewBuilder select() throws Exception {
//        ObjectMapper objectMapper = new JsonMapper();
//        this.views = objectMapper.readValue(jsonFile, CountriesView.class);
//        return this;
//    }
    @Override
    public CountryViewBuilder select() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CountriesWrapper wrapper = objectMapper.readValue(jsonFile, CountriesWrapper.class);
        this.views = wrapper.getCountriesView();
        return this;
    }
}
