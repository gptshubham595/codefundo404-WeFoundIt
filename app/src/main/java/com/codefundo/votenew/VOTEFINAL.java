package com.codefundo.votenew;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;

public class VOTEFINAL extends AppCompatActivity {
    public static final String hostName = "vote4us.database.windows.net"; // update me
    public static final String dbName = "myfirst"; // update me
    public static final String user = "gptshubham595"; // update me
    public static final String a="yuBPUvK1Kf6LMnCFqS9Oug==";
    public static String password="";
    public static final String url = String.format("jdbc:jtds:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
            + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);

    public static final String name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static Connection conn = null;
    public static PreparedStatement pst = null;
    public static Statement stmt = null;
    public static ResultSet rs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votefinal);

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            SQLServerDriver driver = new SQLServerDriver();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            //String url = "jdbc:sqlserver://<SERVER_IP>:<PORT_NO>;databaseName=" + DATABASE_NAME;
            //Connection conn = DriverManager.getConnection(url, user, PASSWORD);
            System.out.println("DB Connection started");
            connection = DriverManager.getConnection(url);
            //String schema = connection.getSchema();
            //System.out.println("Successful connection - Schema: " + schema);

            Toast.makeText(this, "Successful connection - Schema: " +connection.getMetaData(), Toast.LENGTH_SHORT).show();
            System.out.println("Query data example:");
            System.out.println("=========================================");

            // Create and execute a SELECT SQL statement.
            String SQL = "select * from dbo.VOTERS";


            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL)) {

                // Print results from select statement
                while (resultSet.next())
                {
                    Toast.makeText(this, resultSet.getString("Aadhaar") + " "
                            + resultSet.getString("FullName"), Toast.LENGTH_SHORT).show();
                }
                connection.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
