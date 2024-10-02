package com.example.gym.data.service;

import com.example.gym.view.AddressView;
import com.example.gym.view.CountriesView;
import com.example.gym.view.CountryView;

import java.util.List;

public interface ICountriesViewBuilder {

    List<CountryView> getCountries();

    List<AddressView> getAddresses();

    CountriesView getViews();

    // Builder Workflow
    ICountriesViewBuilder build() throws Exception;

    ICountriesViewBuilder select() throws Exception;

}