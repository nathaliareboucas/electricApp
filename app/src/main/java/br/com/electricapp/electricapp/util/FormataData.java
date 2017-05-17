package br.com.electricapp.electricapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nathalia on 16/05/2017.
 */
public class FormataData {

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    String strData;
    Date data;

    public String dataToString(Date data) {
        return strData = sdf.format(data);
    }

    public Date stringToDate(String strData) {
        try {
            return this.data = sdf.parse(strData);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
