package com.codefundo.votenew;

import java.sql.*;

public class READSQL {
    public static final String hostName = "vote4us.database.windows.net"; // update me
    public static final String dbName = "myfirst"; // update me
    public static final String user = "gptshubham595"; // update me
    public static final String a="yuBPUvK1Kf6LMnCFqS9Oug==";
    public static String password="";

    {
        try {
            password = AESCrypt.decrypt(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
            + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);

    public static final String name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static Connection conn = null;
    public static PreparedStatement pst = null;
    public static Statement stmt = null;
    public static ResultSet rs = null;

    public static void main(String[] args) {

        try {

            String SQL = "select * from dbo.VOTERS";
            Class.forName(name);
            conn = DriverManager.getConnection(url);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                System.out.println(rs.getString("FullName"));
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