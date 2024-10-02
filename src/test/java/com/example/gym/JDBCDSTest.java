package com.example.gym;

import com.example.gym.controller.JDBCController;
import com.example.gym.data.service.CustomerViewBuilder;
import com.example.gym.util.JDBCDataSource;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCDSTest {
    private static Logger logger = Logger.getLogger(JDBCDSTest.class.getName());
    @Autowired
    private ApplicationContext context;

    private CustomerViewBuilder simpleViewBuilder;
    private JDBCDataSource jdbcConnector;

    @Before
    public void setUp() throws Exception {
        // jdbcConnector = new JDBCDataSourceConnector();
        jdbcConnector = new JDBCDataSource();
        simpleViewBuilder = new CustomerViewBuilder();
    }

    @Test
    public void testContextLoads() {
        // Ensure that the context is loaded
        assertNotNull(context);

        // Check if the JDBCController bean is present in the context
        assertNotNull(context.getBean(JDBCController.class));
    }

    @Test
    public void testGetCustomersViewList() throws Exception {
        String xmlView = new JDBCController().getSQLQueryResults(
                "SELECT idclient, nume, prenume, email, nrtel FROM clienti",
                "jdbc:postgresql://localhost/GymDB",
                "postgres", "1234",
                "org.postgresql.Driver"
        );
        assertNotNull("Get XML Data failed!", xmlView);

        System.out.println(xmlView);
    }

    @Test
    public void test1_GetJDBC_Data() throws Exception {
        String xmlView = new JDBCController().getSQLQueryResults(
                "SELECT idclient, nume, prenume, email, nrtel FROM clienti WHERE idclient = 1 ",
                "jdbc:postgresql://localhost/GymDB",
                "postgres", "1234",
                "org.postgresql.Driver"
        );
        assertNotNull("Get XML Data failed!", xmlView);
    }
}
