package com.example.gym.controller;


import com.example.gym.controller.dto.DataSourceDTO;
import com.example.gym.controller.dto.JSONResponse;
import com.example.gym.data.service.CustomerViewBuilder;
import com.example.gym.data.service.JDBCService;
import com.example.gym.view.CustomerView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static com.example.gym.util.Constants.SQL_DATA_SOURCE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * REST Service URL
 * http://localhost:8090/data-source-service/jdbc/status
 * http://localhost:8090/data-source-service/jdbc/sql (GET/POST)
 * http://localhost:8090/data-source-service/jdbc/sql/view/customer
 * http://localhost:8090/data-source-service/jdbc/sql/view/customer/{id}
 */

@RestController
@RequestMapping("/" + SQL_DATA_SOURCE)
public class JDBCController {

    private static final Logger logger = Logger.getLogger(JDBCController.class.getName());

    @Autowired
    private JDBCService service;

    private final CustomerViewBuilder customersViewBuilder;

    public JDBCController() {
        this.customersViewBuilder = new CustomerViewBuilder();
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONResponse> checkStatusDS() {
        try {
            return new ResponseEntity<>(new JSONResponse(true, "Response received from JDBCDataSource!", 200), HttpStatus.OK);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Throwable throwable) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
        }
    }

    @GetMapping(value = "/sql", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getSQLQueryResults(@RequestHeader("SQL") String SQL, @RequestHeader("DB_URL") String DB_URL,
                                     @RequestHeader("USER") String USER, @RequestHeader("PASS") String PASS, @RequestHeader("JDBC_DRIVER") String JDBC_DRIVER) throws Exception {

        logger.info("REST Method: getSQLQueryResults");
        return new JDBCService().getSQLQueryResults(DataSourceDTO.builder()
                .DB_URL(DB_URL)
                .DRIVER(JDBC_DRIVER)
                .USER(USER)
                .PASS(PASS)
                .SQL(SQL)
                .build());
    }

    @RequestMapping(value = "/sql", method = POST, consumes = {MediaType.TEXT_PLAIN_VALUE}, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public String postSQLQueryGetResults(@RequestBody String SQL, @RequestHeader("DB_URL") String DB_URL,
                                         @RequestHeader("USER") String USER, @RequestHeader("PASS") String PASS, @RequestHeader("JDBC_DRIVER") String JDBC_DRIVER) throws Exception {

        return service.getSQLQueryResults(DataSourceDTO.builder()
                .DB_URL(DB_URL)
                .DRIVER(JDBC_DRIVER)
                .USER(USER)
                .PASS(PASS)
                .SQL(SQL)
                .build());
    }

    @RequestMapping(value = "/view/customer", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<CustomerView> getCustomerView() {
        return customersViewBuilder.build().getViewList();
    }

    @RequestMapping(value = "/view/customer/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public Optional<CustomerView> getCustomerViewById(@PathVariable("id") Integer customerId) {
        List<CustomerView> viewList = customersViewBuilder.build().getViewList();
        return viewList.stream()
                .filter(c -> c.getIdclient().equals(customerId))
                .findFirst();
    }
}
