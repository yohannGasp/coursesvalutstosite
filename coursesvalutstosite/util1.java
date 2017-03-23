/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursesvalutstosite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Level;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author akulov_ev
 */
public class util1 {

    public static String normal_date(String in) {
        String str;
        str = in;
        if (str.length() == 1) {
            str = "0" + str;
        }
        return str;
    }

    //первое число прошлого месяца
    public static String firstDataLastMonth() {
        //return "01." + normal_date(String.valueOf(Calendar.getInstance().get(Calendar.MONTH))) + "." + Calendar.getInstance().get(Calendar.YEAR);
        return "11.07.2016";
    }

    //вчерашняя дата
    public static String Yesteday() {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.DAY_OF_WEEK) == 2) {
            today.add(Calendar.DAY_OF_MONTH, -3); //пятница

        } else {
            today.add(Calendar.DAY_OF_MONTH, -1); //вчерашняя дата
        }
        today.add(Calendar.MONTH, 1);         // выправляем месяц
        return normal_date(String.valueOf(today.get(Calendar.DAY_OF_MONTH))) + "." + normal_date(String.valueOf(today.get(Calendar.MONTH))) + "." + today.get(Calendar.YEAR);
    }

    /**
     * md5Apache function
     *
     * @param text
     * @return MD5
    */
    public static String md5Apache(String text){
            return DigestUtils.md5Hex(text);
    }

    /**
     * Text from file courses
     *
     * @param fileName
     * @return text from file
     * @throws java.io.FileNotFoundException
     * @throws java.security.NoSuchAlgorithmException
    */
    public static String GetText(String fileName) throws FileNotFoundException, NoSuchAlgorithmException {
        String result = "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            result = sb.toString();
        } catch (IOException ex) {
            CoursesValutsToSite.log.log(Level.WARNING, ex.toString());
        }
        return result;
    }

    /**
     * create file flag
     *
     * @param type
    */
    public static void createFlagFile(String workDir, getCourses.typeFlag type) {
        try {
            File file1 = new File(workDir, type + ".flag");
            file1.createNewFile();
        } catch (IOException e) {
            CoursesValutsToSite.log.log(Level.WARNING, e.toString());
        }
    }

}
