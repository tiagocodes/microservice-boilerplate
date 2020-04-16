package tdd.micro.boot.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * Check transaction date is before today.
     * @param date
     * @return
     */
    public static boolean isBeforeToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar hoy = Calendar.getInstance();
        hoy.setTime(new Date());

        if (calendar.get(Calendar.DAY_OF_YEAR) < hoy.get(Calendar.DAY_OF_YEAR)) {
            return true;
        } else if (calendar.get(Calendar.DAY_OF_MONTH) < hoy.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }

        return false;
    }

    /**
     * Check transaction date is today.
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar hoy = Calendar.getInstance();
        hoy.setTime(new Date());

        if (calendar.get(Calendar.DAY_OF_YEAR) == hoy.get(Calendar.DAY_OF_YEAR) ||
                calendar.get(Calendar.DAY_OF_MONTH) == hoy.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }

        return false;
    }

    /**
     * Check transaction date is after today.
     * @param date
     * @return
     */
    public static boolean isAfterToday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar hoy = Calendar.getInstance();
        hoy.setTime(new Date());

        if (calendar.get(Calendar.DAY_OF_YEAR) > hoy.get(Calendar.DAY_OF_YEAR)) {
            return true;
        } else if (calendar.get(Calendar.DAY_OF_MONTH) > hoy.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }

        return false;
    }

}
