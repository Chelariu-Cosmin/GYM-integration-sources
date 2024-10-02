package com.example.gym.controller;

import com.example.gym.controller.dto.JSONResponse;
import com.example.gym.data.service.SaliViewBuilder;
import com.example.gym.util.XMLFileDataSourceConnector;
import com.example.gym.util.XMLResourceFileDataSourceConnector;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static com.example.gym.util.Constants.XML_DATA_SOURCE;

/**
 * REST Service URL
 * http://localhost:8090/data-source-service/xml/status
 */
@RestController
@RequestMapping("/" + XML_DATA_SOURCE)
public class XMLController {

    private XMLFileDataSourceConnector xmlDSConnector;

    private SaliViewBuilder builder;

    public XMLController() throws Exception {
        this.xmlDSConnector = new XMLResourceFileDataSourceConnector("datasource/sali.xml");
        this.builder = new SaliViewBuilder();
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONResponse> checkStatusDS() {
        try {
            return new ResponseEntity<>(new JSONResponse(true, "Response received from XMLDataSource!", 200), HttpStatus.OK);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
        }
    }
}
