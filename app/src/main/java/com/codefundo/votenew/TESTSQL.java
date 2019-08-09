package com.codefundo.votenew;

import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;

public class TESTSQL {

    public static void main(String[] args) {

        // Connect to database
        String hostName = "vote4us.database.windows.net"; // update me
        String dbName = "myfirst"; // update me
        String user = "gptshubham595"; // update me
        String a="yuBPUvK1Kf6LMnCFqS9Oug==";
        String password = null; // update me
        try {
            password = AESCrypt.decrypt(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
                + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url);
            //String schema = connection.getSchema();
            //System.out.println("Successful connection - Schema: " + schema);

            System.out.println("Successful connection - Schema: " );
            System.out.println("Query data example:");
            System.out.println("=========================================");

            // Create and execute a SELECT SQL statement.
            String selectSql = "SELECT TOP 20 pc.Name as CategoryName, p.name as ProductName "
                    + "FROM [SalesLT].[ProductCategory] pc "
                    + "JOIN [SalesLT].[Product] p ON pc.productcategoryid = p.productcategoryid";
            String SQL = "select * from dbo.VOTERS";


            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL)) {

                // Print results from select statement
                System.out.println("Top 20 categories:");
                while (resultSet.next())
                {
                    System.out.println(resultSet.getString(1) + " "
                            + resultSet.getString(2));
                }
                connection.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
