package com.example.gym.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class DSCustomersSchemaGenerator {

    private final String SQL_DROP_TYPE_SUBSCRIPTION;
    private final String SQL_DROP_SUBSCRIPTIONS;
    private final String SQL_DROP_CUSTOMERS;

    private final String SQL_CREATE_CUSTOMERS;
    private final String SQL_CREATE_SUBSCRIPTIONS;
    private final String SQL_CREATE_TYPE_SUBSCRIPTION;

    public DSCustomersSchemaGenerator() throws Exception {

        SQL_DROP_TYPE_SUBSCRIPTION = "DROP TABLE IF EXISTS Tip_abonament";
        SQL_DROP_SUBSCRIPTIONS = "DROP TABLE IF EXISTS Abonamente";
        SQL_DROP_CUSTOMERS = "DROP TABLE IF EXISTS Clienti";

        SQL_CREATE_CUSTOMERS =
                "CREATE TABLE Clienti ( "
                        + "idClient SERIAL PRIMARY KEY, "
                        + "Nume VARCHAR(100) NOT NULL, "
                        + "Prenume VARCHAR(100) NOT NULL, "
                        + "dataNastere DATE NOT NULL, "
                        + "email VARCHAR(100) NOT NULL, "
                        + "nrTel VARCHAR(20) NOT NULL, "
                        + "idabonament INT REFERENCES Abonamente(idAbonament), "
                        + "idsala INT "
                        + ") ";

        SQL_CREATE_TYPE_SUBSCRIPTION =
                "CREATE TABLE Tip_abonament ( "
                        + "idTipAbonament SERIAL PRIMARY KEY, "
                        + "denumire VARCHAR(50) NOT NULL, "
                        + "descriere TEXT "
                        + ") ";

        SQL_CREATE_SUBSCRIPTIONS =
                "CREATE TABLE Abonamente ( "
                        + "idAbonament SERIAL PRIMARY KEY, "
                        + "numeAbonament VARCHAR(100) NOT NULL, "
                        + "pret NUMERIC(10, 2) NOT NULL, "
                        + "idTipAbonament INT REFERENCES Tip_abonament(idTipAbonament) "
                        + ") ";
    }

    public void generateSQLSchema() {
        try {
            this.cleanSQLSchema();
            this.createSQLSchema();
            this.getSchemaInfo();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Collection<String> getSchemaInfo() {
        Set<String> tableNames = new TreeSet<>();
        try (Connection jdbcConnection = JDBCDataSource.getConnection()) {
            DatabaseMetaData dbMetaData = jdbcConnection.getMetaData();

            // catalog, schemaPattern, tableNamePattern, types
            String[] table = {"TABLE"};
            ResultSet result = dbMetaData.getTables(null, "Clienti", null, table);
            while (result.next()) {
                String tableName = result.getString(3);
                System.out.println("Table: " + tableName);
                tableNames.add(tableName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tableNames;
    }

    private void cleanSQLSchema() {
        try (Connection jdbcConnection = JDBCDataSource.getConnection()) {
            Statement ddlStmt = jdbcConnection.createStatement();
            ddlStmt.executeUpdate(SQL_DROP_CUSTOMERS);
            ddlStmt.executeUpdate(SQL_DROP_SUBSCRIPTIONS);
            ddlStmt.executeUpdate(SQL_DROP_TYPE_SUBSCRIPTION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createSQLSchema() {
        try (Connection jdbcConnection = JDBCDataSource.getConnection()) {
            Statement ddlStmt = jdbcConnection.createStatement();
            ddlStmt.executeUpdate(SQL_CREATE_TYPE_SUBSCRIPTION);
            ddlStmt.executeUpdate(SQL_CREATE_SUBSCRIPTIONS);
            ddlStmt.executeUpdate(SQL_CREATE_CUSTOMERS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
