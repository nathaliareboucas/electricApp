package br.com.electricapp.electricapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nathalia on 06/05/2017.
 */
public class DateConverter {

    DateFormat formatter;
    Date date;

    public Date stringToDate(String str_date) {
        try {
            formatter = new SimpleDateFormat("dd/MM/yyyy");
            date = (Date)formatter.parse(str_date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String nomeMes(Calendar data) {
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul",
                "Ago", "Set", "Out", "Nov", "Dez"};
        return meses[data.get(Calendar.MONTH)];
    }
}
