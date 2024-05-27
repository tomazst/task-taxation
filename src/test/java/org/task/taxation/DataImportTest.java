package org.task.taxation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;


@ExtendWith(SpringExtension.class)
public class DataImportTest {

    private final String tableName = "fo_random";
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/taxation";
    //        String jdbcUrl = "jdbc:log4jdbc:mysql://localhost:3306/taxation"; // add logging
    private final String username = "root";
    private final String pass = "root";

    @BeforeAll
    static void beforeAll() throws ClassNotFoundException, SQLException {
        Class.forName("net.sf.log4jdbc.DriverSpy");
        DriverManager.registerDriver(new net.sf.log4jdbc.DriverSpy());
    }

    @Test
    void bulkInsertPerformanceTest() throws FileNotFoundException, IOException {
        long start = System.currentTimeMillis();

        System.out.println("Execution started: " + new Date());

        String filePath = "fo_random.txt";
        // Read and parse the file
        List<DataRow> dataRows = readAndSortData(filePath);

        System.out.println("Reading data from file and sorting ended: " + new Date());
        System.out.println("dataRows count: " + dataRows.size());
        System.out.println("Execution duration in milis: " + (System.currentTimeMillis() - start));

        System.out.println("Started importing to database: " + new Date());

        // Insert data into the database


        String insertStatement = "";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, pass)) {

            String insertQuery = String.format("INSERT INTO %s (match_id, market_id, outcome_id, specifiers, date_insert) VALUES ", tableName);

            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                //statement.execute("SET GLOBAL max_allowed_packet = 20 * 1024 * 1024");
                long n = 1;
                StringBuffer values = new StringBuffer();
                for (DataRow dataRow : dataRows) {
                    if (!values.isEmpty()) {
                        values.append(",");
                    }
                    values.append(dataRow.getInsertValueStatement(LocalDateTime.now().format(dtf)));
                    if (n % 200 == 0) {
                        insertStatement = insertQuery + values.toString();
                        statement.addBatch(insertStatement);
                        values = new StringBuffer();
                    }
                    n++;
                }
                if (!values.isEmpty()) {
                    insertStatement = insertQuery + values.toString();
                    statement.addBatch(insertStatement);
                }
                statement.executeBatch();
            }
            connection.commit();
        } catch (Exception e) {
            System.out.println(insertStatement);
            e.printStackTrace();
        }

        System.out.println("Execution ended: " + new Date());
        System.out.println("Execution duration in milis: " + (System.currentTimeMillis() - start));
    }

    @Test
    void fileInsertPerformanceTest() throws IOException{
        long start = System.currentTimeMillis();

        System.out.println("Execution started: " + new Date());

        String filePath = "fo_random.txt";
        // Read and parse the file
        List<DataRow> dataRows = readAndSortData(filePath);

        System.out.println("Reading data from file and sorting ended: " + new Date());
        System.out.println("dataRows count: " + dataRows.size());
        System.out.println("Execution duration in milis: " + (System.currentTimeMillis() - start));

        System.out.println("Writing sorted data to tmp file to be later imported in mysql table.");
        File tempFile = File.createTempFile("tmpData", ".txt");
        tempFile.deleteOnExit();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (DataRow row : dataRows) {
                row.setTime(LocalDateTime.now().format(dtf));
                writer.write(row.formatLine());
                writer.newLine();
            }
        }
        System.out.println("Execution duration in milis: " + (System.currentTimeMillis() - start));

        System.out.println("Started importing to database: " + new Date());

        // Insert data into the database
        String loadQuery = "LOAD DATA LOCAL INFILE '" + tempFile.getAbsolutePath().replace("\\", "\\\\") +
                        "' INTO TABLE " + tableName +
                        " FIELDS TERMINATED BY ';' " +
                        "LINES TERMINATED BY '\\n' " +
                        "(match_id, market_id, outcome_id, specifiers, @date_insert) " +
                        "SET date_insert = STR_TO_DATE(@date_insert,'%Y-%m-%d %H:%i:%s.%f')";
        System.out.println(loadQuery);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, pass);
             Statement statement = connection.createStatement()) {
            statement.execute(loadQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Execution ended: " + new Date());
        System.out.println("Execution duration in milis: " + (System.currentTimeMillis() - start));
    }


    private List<DataRow> readAndSortData(String filePath) {

        // Read and parse the file
        List<DataRow> dataRows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // ignore first line with header info
            br.readLine();
            while ((line = br.readLine()) != null) {

                String[] columns = line.split("\\|", 4);

                DataRow dataRow = new DataRow(
                        columns[0],
                        parseInt(columns[1]),
                        columns[2],
                        columns[3]
                );
                dataRows.add(dataRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the data
        Collections.sort(dataRows, (o1, o2) -> {
            int matchIdComparison = o1.matchId.compareTo(o2.matchId);
            if (matchIdComparison != 0) return matchIdComparison;
            int marketIdComparison = Integer.compare(o1.marketId, o2.marketId);
            if (marketIdComparison != 0) return marketIdComparison;
            int outcomeIdComparison = o1.outcomeId.compareTo(o2.outcomeId);
            if (outcomeIdComparison != 0) return outcomeIdComparison;
            return o1.specifiers.compareTo(o2.specifiers);
        });

        return dataRows;
    }

    private int parseInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (RuntimeException e) {
            return 0;
        }
    }


}
