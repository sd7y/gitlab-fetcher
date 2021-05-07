package net.aplat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static Date parseDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }
}
