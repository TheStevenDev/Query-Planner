import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    public static boolean vaildIpAdress(String ip){
        if(ip.trim().toLowerCase().equals("localhost")) return true;

        ip = ip.trim();

        if (ip .equals("")) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;

    }

    public static boolean vaildPort(String port){
        if (port == null || port.isEmpty()) {
            return false;
        }


        //only numbers
        for (char c : port.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        if (port.length() > 5) {
            return false;
        }

        int portNumber = Integer.parseInt(port);
        return portNumber >= 0 && portNumber <= 65535;
    }

    public static LocalDate validDate(String date){
        if (date == null || date.trim().isEmpty()) {
            return null; //empty string
        }

        //date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try {
            //try parsing
            LocalDate localDate = LocalDate.parse(date.trim(), formatter);
            return localDate;
        } catch (DateTimeParseException e) {
            return null;
        }

    }


    public static LocalTime validTime(String time){
        if (time == null || time.trim().isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime localTime = LocalTime.parse(time.trim(), formatter);
            return localTime;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isBeforeToday(LocalDate date) {
        LocalDate nowDate = LocalDate.now();

        //check if the provided date is before today's date
        return date.isBefore(nowDate);
    }

    public static boolean isBeforeNow(LocalTime time) {
        LocalTime now = LocalTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

        //truncate the input time to hours and minutes
        LocalTime truncatedTime = time.truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

        return truncatedTime.isBefore(now);
    }



}
