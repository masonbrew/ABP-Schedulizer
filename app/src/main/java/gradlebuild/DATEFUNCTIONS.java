package gradlebuild;

public class DATEFUNCTIONS { //Note that this organization only operates during the summer months so leap day handling is not incuded
    static int getDateID(int month, int day, int year) throws IllegalArgumentException{
        int dayOfYear = 0;
        if (month>12 || month<0){
            throw new IllegalArgumentException("Invalid month format. Date: " + month + "/" + day + "/" + year);
        }
        if (day>31 || day<0){
            throw new IllegalArgumentException("Invalid date format. Date: " + month + "/" + day + "/" + year);
        }
        if (year>3000||year<1900){
            throw new IllegalArgumentException("Invalid year or out of bounds. Date:" + month + "/" + day + "/" + year);
        }
        // Calculate the cumulative days in the previous months
        switch (month) {
            case 12:
                dayOfYear += 30; // November
            case 11:
                dayOfYear += 31; // October
            case 10:
                dayOfYear += 30; // September
            case 9:
                dayOfYear += 31; // August
            case 8:
                dayOfYear += 31; // July
            case 7:
                dayOfYear += 30; // June
            case 6:
                dayOfYear += 31; // May
            case 5:
                dayOfYear += 30; // April
            case 4:
                dayOfYear += 31; // March
            case 3:
                dayOfYear += 28; // February (Assuming non-leap year)
            case 2:
                dayOfYear += 31; // January
        }
                
        // Add the day of the month to get the total day of the year
        dayOfYear = dayOfYear + day + year*365;
        
        return dayOfYear;
    }

    public static int stringToID (String dateString) {
        // Split the string into month, date, and year
        String[] parts = dateString.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format. Expected format: month/date/year");
        }
        
        // Parse the month, date, and year values
        int monthNumber = Integer.parseInt(parts[0]);
        if (monthNumber>12 || monthNumber<0){
            throw new IllegalArgumentException("Invalid month format. Date: " + dateString);
        }
        int dateNumber = Integer.parseInt(parts[1]);
        if (dateNumber>31 || dateNumber<0){
            throw new IllegalArgumentException("Invalid date format. Date: " + dateString);
        }
        int yearNumber = Integer.parseInt(parts[2]);
        if (yearNumber>3000||yearNumber<1900){
            throw new IllegalArgumentException("Invalid year or out of bounds. Date:" + dateString);
        }
        
        // Calculate the sum
        int dayID = getDateID(monthNumber, dateNumber, yearNumber);
        
        return dayID;
    }

    public static String iDtoString (int dateID){
        int ID = dateID;
        int year = ID/365;
        ID = ID - 365*year;
        int[] monthLengths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int month = 1;
        while (ID > monthLengths[month]) {
        ID -= monthLengths[month];
        month++;
        }
        return month + "/" + ID + "/" + year;
    }
}
