package com.example.gym.data.service;

import com.example.gym.util.JDBCDataSource;
import com.example.gym.view.CustomerView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CustomerViewBuilder {
    private static final Logger logger = Logger.getLogger(CustomerViewBuilder.class.getName());
    private String SQL_CUSTOMERS_SELECT = "SELECT idclient, nume, prenume, dataNastere, email, nrtel FROM clienti";
    private List<CustomerView> customersViewList = new ArrayList<>();

    public List<CustomerView> getViewList() {
        return this.customersViewList;
    }

    public CustomerViewBuilder build() {
        try (Connection jdbcConnection = JDBCDataSource.getConnection()) {
            // extract data
            Statement selectStmt = jdbcConnection.createStatement();
            ResultSet rs = selectStmt.executeQuery(SQL_CUSTOMERS_SELECT);

            // map data to EntityView
            customersViewList = new ArrayList<>();
            while (rs.next()) {
                Integer idclient = rs.getInt("idclient");
                String nume = rs.getString("nume");
                String prenume = rs.getString("prenume");
                LocalDate dataNastere = LocalDate.parse(rs.getString("dataNastere"));
                String email = rs.getString("email");
                String nrTel = rs.getString("nrTel");
                this.customersViewList.add(CustomerView.builder()
                        .idclient(idclient)
                        .nume(nume)
                        .prenume(prenume)
                        .dataNastere(dataNastere)
                        .email(email)
                        .nrTel(nrTel)
                        .build());
            }
            System.out.println("CustomerViewBuilder.build: {}," + customersViewList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }
}
