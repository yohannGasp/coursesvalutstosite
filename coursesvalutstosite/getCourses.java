/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursesvalutstosite;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
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
    public String FilenameOnLocalMachineXml;
    public String PathOnFtpXml;
    public String Log;
    public String fDebug;
    public String connectionString;
    public String userCft;
    public String passwordCft;
    public static String workDir;

    enum typeFlag {
        SQL_ERROR, FTP_ERROR
    }

    /**
     * Get curs exchange from CFT
     *
     */
    public void getCursFromCFT() {

        String str, str_1, r0 = "";
        String[] param = new String[1];

        Calendar today = Calendar.getInstance();
        String day1 = util1.normal_date(String.valueOf(today.get(Calendar.DAY_OF_MONTH)));
        String mon = util1.normal_date(String.valueOf(today.get(Calendar.MONTH) + 1));
        String date_1 = day1 + "." + mon + "." + String.valueOf(today.get(Calendar.YEAR)).substring(2);
        String date_2 = day1 + "." + mon + "." + String.valueOf(today.get(Calendar.YEAR));
        String time_2 = String.valueOf(today.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(today.get(Calendar.MINUTE));
        param[0] = date_1;

        try (FileWriter fw = new FileWriter(FilenameOnLocalMachine, false);
                OutputStreamWriter fXml = new OutputStreamWriter(new FileOutputStream(FilenameOnLocalMachineXml), "windows-1251");) {
            // header
            fXml.write("<?xml version=\"1.0\" encoding=\"windows-1251\" ?>" + System.lineSeparator());
            fXml.write("<ValCurs Date=\"" + date_2 + "\" time=\"" + time_2 + "\" name=\"Foreign Currency Market\">" + System.lineSeparator());

            db = new DB(this.connectionString, this.userCft, this.passwordCft);
            ArrayList<valuta> list = db.ExecProcPS(db.SQL, param);

            for (valuta item : list) {

                // String to file for site
                try (Formatter fmt = new Formatter(Locale.ENGLISH)) {

                    str = fmt.format("%s\t"
                            + "%s\t"
                            + "%s\t"
                            + "%s\t"
                            + "%s%n", item.branch, // branch
                            item.date, // date
                            item.type, // type
                            item.code, // code
                            item.value // value
                    ).toString();
                }
                fw.write(str);

                // String to file xml
                if (item.branch.equals("003-00")) {

                    if (item.code.equals("EUR")) {
                        r0 = "R01239";
                    }
                    if (item.code.equals("USD")) {
                        r0 = "R01235";
                    }

                    str_1 = cursToXmlFile(r0,
                            item.numcode, // numcode
                            item.code, // code
                            "1", // nominal
                            item.name, // name
                            item.type, //type
                            item.value); // value

                    fXml.write(str_1);
                }

            }
            fw.flush();
            fXml.write("</ValCurs>");
            fXml.flush();

        } catch (IOException ex) {

            util1.createFlagFile(this.workDir, typeFlag.SQL_ERROR);
            CoursesValutsToSite.log.log(Level.WARNING, ex.toString());

        } finally {

            CoursesValutsToSite.log.log(Level.INFO, "Get curs exchange from CFT");

        }

    }

    /**
     * Upload file to FTP server должен быть включен пассивный режим !!!
     */
    public void UploadFileOnFtp() {

        FTPClient client = new FTPClient();
        try (FileInputStream fis = new FileInputStream(FilenameOnLocalMachine);
                FileInputStream fis2 = new FileInputStream(FilenameOnLocalMachineXml);) {

            client.connect(FTPADDR);
            if (fDebug.equals("true")) {
                CoursesValutsToSite.log.log(Level.INFO, client.getReplyString());
            }

            client.login(user, Password);
            if (fDebug.equals("true")) {
                CoursesValutsToSite.log.log(Level.INFO, client.getReplyString());
            }

            client.enterLocalPassiveMode();
            /* пассивный режим */

 /* закачиваем первый файл */
            Boolean r1 = client.storeFile(PathOnFtp, fis);
            if (fDebug.equals("true")) {
                CoursesValutsToSite.log.log(Level.INFO, client.getReplyString());
            }
            if (r1 == false) {
                throw new SocketException();
            }

            /* закачиваем второй файл */
            Boolean r2 = client.storeFile(PathOnFtpXml, fis2);
            if (fDebug.equals("true")) {
                CoursesValutsToSite.log.log(Level.INFO, client.getReplyString());
            }
            if (r2 == false) {
                throw new SocketException();
            }

        } catch (SocketException e) {

            util1.createFlagFile(this.workDir, typeFlag.FTP_ERROR);
            CoursesValutsToSite.log.log(Level.WARNING, e.toString());

        } catch (IOException e) {

            util1.createFlagFile(this.workDir, typeFlag.FTP_ERROR);
            CoursesValutsToSite.log.log(Level.WARNING, e.toString());

        } finally {

            try {

                client.logout();
                client.disconnect();

            } catch (IOException e) {
                /* FTP_ERROR not in level */
                CoursesValutsToSite.log.log(Level.WARNING, "ftp logout " + e.toString());
            }

            CoursesValutsToSite.log.log(Level.INFO, "Upload file to FTP server");
        }
    }

    /**
     * cursToXmlFile
     *
     * @param id
     * @param numCode
     * @param CharCode
     * @param Nominal
     * @param Name
     * @param Value
     * @return String
     */
    public String cursToXmlFile(String id, String numCode, String CharCode, String Nominal, String Name, String type, String Value) {
        return "<Valute ID=\"" + id + "\" type=\"" + type + "\">" + System.lineSeparator()
                + "\t" + "<NumCode>" + numCode + "</NumCode>" + System.lineSeparator()
                + "\t" + "<CharCode>" + CharCode + "</CharCode>" + System.lineSeparator()
                + "\t" + "<Nominal>" + Nominal + "</Nominal>" + System.lineSeparator()
                + "\t" + "<Name>" + Name + "</Name>" + System.lineSeparator()
                + "\t" + "<Value>" + Value + "</Value>" + System.lineSeparator()
                + "</Valute>" + System.lineSeparator();
    }

}
