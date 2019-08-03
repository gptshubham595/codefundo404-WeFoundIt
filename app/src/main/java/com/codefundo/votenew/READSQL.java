package com.codefundo.votenew;

import java.sql.*;

public class READSQL {

    public static final String url = "jdbc:sqlserver://***.database.windows.net:1433;database=***;user=***password=***;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
    public static final String name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static Connection conn = null;
    public static PreparedStatement pst = null;
    public static Statement stmt = null;
    public static ResultSet rs = null;

    public static void main(String[] args) {

        try {

            String SQL = "select * from dbo.Student";
            Class.forName(name);
            conn = DriverManager.getConnection(url);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void close() {
        try {
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}