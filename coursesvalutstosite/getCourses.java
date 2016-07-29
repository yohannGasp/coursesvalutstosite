/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursesvalutstosite;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author akulov_ev
 */
public class getCourses {
    private DB db;
    public String FTPADDR;
    public String user;
    public String Password;
    public String PathOnFtp;
    public String FilenameOnLocalMachine;
    public String Log;

    /**
    * Get curs exchange from CFT
    *
     * @throws java.lang.InterruptedException
    */
    public void getCursFromCFT() throws InterruptedException {
        String str;
        String[] param = new String[1];

        Calendar today = Calendar.getInstance();
        String day1 = util1.normal_date(String.valueOf(today.get(Calendar.DAY_OF_MONTH)));
        String mon  = util1.normal_date(String.valueOf(today.get(Calendar.MONTH)+1));
        String date_1 = day1 + "." + mon + "." + String.valueOf(today.get(Calendar.YEAR)).substring(2);
        param[0] = date_1;

        try(FileWriter fw = new FileWriter(FilenameOnLocalMachine,false)) {
            db = new DB();
            ResultSet rs = db.ExecProcPS(db.SQL, param);
            while (rs.next()) {
                try (Formatter fmt = new Formatter(Locale.ENGLISH)) {
                    str = fmt.format("%s\t"
                            + "%s\t"
                            + "%s\t"
                            + "%s\t"
                            + "%s%n", rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),rs.getString(5)).toString();
                }
                fw.write(str);
            }
            fw.flush();
            db.Close();
        } catch(SQLException | IOException ex) {
            CoursesValutsToSite.log.log(Level.WARNING, (Supplier<String>) ex);
        }
        CoursesValutsToSite.log.log(Level.INFO, "Get curs exchange from CFT");
    }

    /**
    * Upload file to FTP server
    *
    */
    public void UploadFileOnFtp() {
        FTPClient client = new FTPClient();
        try (FileInputStream fis = new FileInputStream(FilenameOnLocalMachine);) {
            client.connect(FTPADDR);
            client.login(user, Password);
            client.storeFile(PathOnFtp, fis);
            client.logout();
        } catch (IOException e) {
            CoursesValutsToSite.log.log(Level.WARNING, (Supplier<String>) e);
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                CoursesValutsToSite.log.log(Level.WARNING, (Supplier<String>) e);
            }
            CoursesValutsToSite.log.log(Level.INFO, "Upload file to FTP server");
        }
    }
}
