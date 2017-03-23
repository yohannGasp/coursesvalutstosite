/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursesvalutstosite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author akulov_ev
 */
public class CoursesValutsToSite {

    public static final Logger log = Logger.getLogger(CoursesValutsToSite.class.getName());

    public static void main(String[] args) throws InterruptedException, FileNotFoundException, NoSuchAlgorithmException, IOException {
        String old_hash;
        String new_hash = null;
/* /baikalbank.bget.ru/public_html/courses.txt !!!!!!! */
        /* input parameters */
        getCourses gc = new getCourses();
        gc.FilenameOnLocalMachine = args[0]; // путь к файлу с курсами
        gc.FTPADDR = args[1];                // адрес FTP
        gc.PathOnFtp = args[2];              // путь FTP
        gc.user = args[3];                   // юзер
        gc.Password = args[4];               // пароль
        gc.Log = args[5];                    // логи
        gc.FilenameOnLocalMachineXml = args[6]; // xml file
        gc.PathOnFtpXml = args[7];           // путь FTP xml file
        gc.fDebug = args[8];                 // debug FTP соединения
        gc.connectionString = args[9];       // connectionString
        gc.userCft = args[10];               // userCft
        gc.passwordCft = args[11];           // passwordCft
        gc.workDir = args[12];               // workDir
       
        /*  Loger */
        FileHandler fh;
        fh = new FileHandler(gc.Log, true);
        fh.setFormatter(new SimpleFormatter());
        fh.setEncoding("UTF-8");
        log.addHandler(fh);
        log.setLevel(Level.ALL);

        /* hash old from file */
        File file = new File(gc.FilenameOnLocalMachine);
        if (file.exists() && file.isFile()) {
            old_hash = util1.md5Apache(util1.GetText(gc.FilenameOnLocalMachine));
        } else {
            old_hash = "";
        }

        /* get courses */
        gc.getCursFromCFT();
        Thread.sleep(5000);

        /* hash new from file */
        if (file.exists() && file.isFile()) {
            new_hash = util1.md5Apache(util1.GetText(gc.FilenameOnLocalMachine));
        }

        /* compare hash, if not equals to FTP */
        if (!old_hash.equals(new_hash)) {
            gc.UploadFileOnFtp();
        }
    }
}
