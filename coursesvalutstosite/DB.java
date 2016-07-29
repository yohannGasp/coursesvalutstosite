/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursesvalutstosite;

import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author akulov_ev
 */
public class DB {

    private static final String CONNECTION_STRING = "jdbc:oracle:thin:@172.25.1.50:1521:BIB_PROD";
    private static final String CONNECTION_LOGIN = "ibs";
    private static final String CONNECTION_PASSWORD = "ibs1223";

    private Connection conn;
    public String SQL = "begin BIB_GET_KURS(?); end;";

    /* Singletone */
    Connection Connection() throws SQLException {
        if (this.conn == null) {
            java.sql.DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            this.conn = java.sql.DriverManager.getConnection(CONNECTION_STRING, CONNECTION_LOGIN, CONNECTION_PASSWORD);
        }
        return this.conn;
    }

    /**
     * querySet
     *
     * @param Select_SQL - string query
     * @return ResultSet
     * @throws java.sql.SQLException
     */
    public ResultSet ResultSet(String Select_SQL) {
        ResultSet result = null;
        try {
            Statement statement = Connection().createStatement();
            result = statement.executeQuery(Select_SQL);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return result;
    }

    /**
     * querySet for parameters
     *
     * @param Select_SQL - string query and parameters
     * @return ResultSet
     * @throws java.sql.SQLException
     */
    public ResultSet ResultSetPS(String Select_SQL,String[] param) throws SQLException {
        PreparedStatement pstmt = Connection().prepareStatement(Select_SQL);
        for (int i = 0; i < param.length; i++) {
            pstmt.setString(i+1,param[i]);
        }
        ResultSet rs = pstmt.executeQuery();
        return rs;
    }

    /**
     * exec procedure in oracle
     *
     * @param SQL
     * @return ResultSet
     * @throws java.sql.SQLException
     */
    public ResultSet ExecProc(String SQL) throws SQLException {
        ResultSet result = null;
        try {
            Statement stmt = Connection().createStatement();
            stmt.executeQuery(SQL);
            if (stmt.getMoreResults()) {
                result = stmt.getResultSet();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return result;
    }
    /**
     * exec procedure in oracle with parameters
     *
     * @param SQL
     * @return ResultSet
     * @throws java.sql.SQLException
     */
    public ResultSet ExecProcPS(String SQL,String[] param) throws SQLException {
        ResultSet result = null;
        try {
            CallableStatement stmt = Connection().prepareCall(SQL);
            for (int i = 0; i < param.length; i++) {
                stmt.setString(i+1,param[i]);
            }
            stmt.execute();
            if (stmt.getMoreResults()) {
                result = stmt.getResultSet();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return result;
    }

    /**
     * close connection
     * @throws java.sql.SQLException
     */
    public void Close() throws SQLException{
        if (this.conn != null) {
            this.conn.close();
        }
    }

    /**
     * queryfromFile
     *
     * @param fileName
     * @return String
     */
    private String queryfromFile(String fileName) {
        String Result = "";
        try (FileReader reader = new FileReader(fileName)) {
            int c;
            while ((c = reader.read()) != -1) {
                Result += (char) c;
            }
        } catch (IOException ex) {
            Result += ex.getMessage();
        }
        return Result;
    }

}
