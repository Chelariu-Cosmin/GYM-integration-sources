package com.example.gym.controller;

import com.example.gym.controller.dto.JSONResponse;
import com.example.gym.data.service.CountryViewBuilder;
import com.example.gym.data.service.ICountriesViewBuilder;
import com.example.gym.util.JSONFileDSConnector;
import com.example.gym.util.JSONResourceFileDataSourceConnector;
import com.example.gym.view.AddressView;
import com.example.gym.view.CountriesView;
import com.example.gym.view.CountryView;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.gym.util.Constants.JSON_DATA_SOURCE;

/**
 * REST Service URL
 * http://localhost:8090/data-source-service/json/status
 * http://localhost:8090/data-source-service/json/views
 * http://localhost:8090/data-source-service/json/view/country
 * http://localhost:8090/data-source-service/json/view/address
 */

@RestController
@RequestMapping("/" + JSON_DATA_SOURCE)
public class JSONController {

    private ICountriesViewBuilder builder;
    private JSONFileDSConnector jsonDSConnector;

    public JSONController() throws Exception {
        this.jsonDSConnector = new JSONResourceFileDataSourceConnector("datasource/country_address.json");
        this.builder = new CountryViewBuilder(jsonDSConnector);
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONResponse> checkStatusDS() {
        try {
            return new ResponseEntity<>(new JSONResponse(true, "Response received from JSONDataSource!", 200), HttpStatus.OK);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
        }
    }

    @RequestMapping(value = "/views", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public CountriesView getViews() throws Exception {
        return this.builder.build().getViews();
    }

    @RequestMapping(value = "/view/country", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public List<CountryView> findAllCountries() throws Exception {
        return builder.build().getCountries();
    }

    @RequestMapping(value = "/view/address", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public List<AddressView> findAllAddresses() throws Exception {
        return builder.build().getAddresses();
    }
}
