/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursesvalutstosite;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author akulov_ev
 */
public class DB {

    private String connectionString;
    private String userCft;
    private String passwordCft;

    public final String SQL = "begin BIB_GET_KURS(?); end;";

    /**
     *
     * @param connectionString
     * @param userCft
     * @param passwordCft
     */
    public DB(String connectionString, String userCft, String passwordCft) {
        this.connectionString = connectionString;
        this.userCft = userCft;
        this.passwordCft = passwordCft;
    }

    /**
     * exec procedure in oracle with parameters
     *
     * @param SQL
     * @return ResultSet
     */
    public ArrayList ExecProcPS(String SQL, String[] param) {

        Connection connect = null;
        CallableStatement statement = null;
        ResultSet result = null;

        ArrayList<valuta> list = new ArrayList<>();

        try {

            System.setProperty("java.security.egd", "file:///dev/urandom"); //http://blockdump.blogspot.ru/2012/07/connection-problems-inbound-connection.html

            java.sql.DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            connect = java.sql.DriverManager.getConnection(this.connectionString, this.userCft, this.passwordCft);

            /* add parameters */
            statement = connect.prepareCall(SQL);
            for (int i = 0; i < param.length; i++) {
                statement.setString(i + 1, param[i]);
            }

            statement.execute();
            if (statement.getMoreResults()) {

                result = statement.getResultSet();

                while (result.next()) {

                    list.add(new valuta(result.getString(1),
                            result.getString(2),
                            result.getString(3),
                            result.getString(4),
                            result.getString(5),
                            result.getString(6),
                            result.getString(7)));

                }

            }

        } catch (SQLException ex) {

            util1.createFlagFile(getCourses.workDir, getCourses.typeFlag.SQL_ERROR);
            CoursesValutsToSite.log.log(Level.WARNING, ex.toString());

        } finally {

            try {

                result.close();
                statement.close();
                connect.close();

            } catch (SQLException ex) {

                util1.createFlagFile(getCourses.workDir, getCourses.typeFlag.SQL_ERROR);
                CoursesValutsToSite.log.log(Level.WARNING, "close connect " + ex.toString());
            }

        }
        return list;
    }

}
