package com.example.gym.data.service;

import com.example.gym.controller.dto.DataSourceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

@Service
public class JDBCService {
    private static final Logger logger = LoggerFactory.getLogger(JDBCService.class);

    public static String convertToXMLString(ResultSet resultSet) throws Exception {
        StringBuilder xmlArray = new StringBuilder("<results>");
        String columnTagStart, columnTagEnd;
        while (resultSet.next()) {
            int total_cols = resultSet.getMetaData().getColumnCount();
            xmlArray.append("	<result> \n");
            for (int i = 0; i < total_cols; i++) {
                columnTagStart = "<?>";
                columnTagEnd = "</?>";
                columnTagStart = columnTagStart.replace("?",
                        resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase());
                columnTagEnd = columnTagEnd.replace("?",
                        resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase());
                xmlArray.append(columnTagStart).append(resultSet.getObject(i + 1)).append(columnTagEnd);
            }
            xmlArray.append("	</result> \n");
        }
        xmlArray.append("</results>");
        return xmlArray.toString();
    }

    public static Connection getConnection(String DB_URL, String USER, String PASS, String JDBC_DRIVER) throws Exception {
        logger.info("JDBC_DRIVER: " + JDBC_DRIVER);

        if (JDBC_DRIVER != null && !JDBC_DRIVER.trim().isEmpty()) Class.forName(JDBC_DRIVER);

        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASS);

        return DriverManager.getConnection(DB_URL, props);
    }

    public String getSQLQueryResults(DataSourceDTO dataSourceDTO) throws Exception {
        logger.info("REST Method: getSQLQueryResults");

        try (Connection jdbcConnection = getConnection(dataSourceDTO.getDB_URL(), dataSourceDTO.getUSER(), dataSourceDTO.getPASS(), dataSourceDTO.getDRIVER())) {
            logger.info(dataSourceDTO.getSQL());

            Statement selectStmt = jdbcConnection.createStatement();
            ResultSet rs = selectStmt.executeQuery(dataSourceDTO.getSQL());

            String xmlString = convertToXMLString(rs);

            logger.info("XML produced for: ********************** ");
            logger.info(xmlString);

            return xmlString;
        } catch (Exception ex) {
            throw new Exception("Error executing SQL query", ex);
        }
    }
}
