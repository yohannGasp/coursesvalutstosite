/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coursesvalutstosite;

/**
 *
 * @author evgeniy
 */
public class valuta {

    public valuta(String branch, String date, String type, String code, String value, String numcode, String name) {
        this.branch = branch;
        this.date = date;
        this.type = type;
        this.code = code;
        this.value = value;
        this.numcode = numcode;
        this.name = name;
    }
    
    String branch;
    String date;
    String type;
    String code;
    String value;
    String numcode;
    String name;
    
}
