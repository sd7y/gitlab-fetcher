package net.aplat;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateTest {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String str = "2018-03-08T16:24:09.783+08:00";
        System.out.println(sdf.parse(str));
    }

}
