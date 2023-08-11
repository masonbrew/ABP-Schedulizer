package gradlebuild;


import java.util.ArrayList;

import com.google.api.services.sheets.v4.model.ValueRange;

public class Roster {
    private ArrayList<Guard> guardList = new ArrayList<Guard>(0);
    private int scheduleStartDayID;
    private java.util.List<java.util.List<java.lang.Object>>	 sheet;
    private java.util.List<java.util.List<java.lang.Object>>	 TOAs;
    private Settings scheduleSettings;


    public Roster (ValueRange rosterSheet, Settings settings){
        scheduleSettings = settings;
        scheduleStartDayID = scheduleSettings.getStartDateID();
        sheet = rosterSheet.getValues();
        try {
            checkCategories(sheet);
        }
        catch (IllegalArgumentException IllegalCategories) {
            throw new IllegalArgumentException();
        }
        makeGuardList(sheet);
    }

    private void checkCategories (java.util.List<java.util.List<java.lang.Object>> fullSheet) throws IllegalArgumentException{
        java.util.List<Object> categoryRow = fullSheet.get(0);
        String categoryString = categoryRow.toString();
        categoryString = categoryString.toLowerCase();
        categoryString = categoryString.replaceAll("\\s", "");
        if (!categoryString.equals("[firstname,lastname,year,birthday,srt,dispatch,beachhouse,waverunner,emt,firstday,lastday,daysscheduled]")){
            System.out.println("Invalid categories in roster sheet");
            offerRosterTemplate();
            throw new IllegalArgumentException();
        }
    }

    private void offerRosterTemplate(){
        
    }
    public static String toA1(int row, int column) {
        StringBuilder sb = new StringBuilder();

        // Convert the column index to A1 notation
        int dividend = column + 1;
        while (dividend > 0) {
            int modulo = (dividend - 1) % 26;
            char letter = (char) (65 + modulo);
            sb.insert(0, letter);
            dividend = (dividend - modulo) / 26;
        }

        // Append the row index
        sb.append(row);

        return sb.toString();
    }

    private void makeGuardList(java.util.List<java.util.List<java.lang.Object>> fullSheet){
        int rowCount = fullSheet.size();
        if (rowCount == 1){
            System.out.println("No guards in roster");
            offerRosterTemplate();
            throw new IllegalArgumentException();
        }
        for (int guardRow = 1; guardRow < rowCount; guardRow++){
            try {

            String currentGuardFirst = fullSheet.get(guardRow).get(0).toString();
            currentGuardFirst = currentGuardFirst.toLowerCase();
            currentGuardFirst = currentGuardFirst.replaceAll("\\s", "");
            if (!currentGuardFirst.equals("")){
                String currentGuardLast = fullSheet.get(guardRow).get(1).toString();
                currentGuardLast = currentGuardLast.toLowerCase();
                currentGuardLast = currentGuardLast.replaceAll("\\s", "");
                int guardYear = Integer.parseInt(fullSheet.get(guardRow).get(2).toString());
                
                int guardBirthdayID = DATEFUNCTIONS.stringToID(fullSheet.get(guardRow).get(3).toString());
           
                boolean srtTrained = stringToBool(fullSheet.get(guardRow).get(4).toString());
                
                boolean dispatchTrained = stringToBool(fullSheet.get(guardRow).get(5).toString());
                
                boolean beachhouseTrained = stringToBool(fullSheet.get(guardRow).get(6).toString());
            
                boolean waverunnerTrained = stringToBool(fullSheet.get(guardRow).get(7).toString());

                boolean emtTrained = stringToBool(fullSheet.get(guardRow).get(8).toString());                
            
                int guardFirstDayID = DATEFUNCTIONS.stringToID(fullSheet.get(guardRow).get(9).toString());

                int guardLastDayID = DATEFUNCTIONS.stringToID(fullSheet.get(guardRow).get(10).toString());

                int guardDaysAssigned = Integer.parseInt(fullSheet.get(guardRow).get(11).toString());
                guardList.add(new Guard(
                    currentGuardFirst,
                    currentGuardLast,
                    guardYear,
                    guardBirthdayID,
                    waverunnerTrained,
                    dispatchTrained,
                    srtTrained,
                    beachhouseTrained,
                    emtTrained,
                    guardFirstDayID,
                    guardLastDayID,
                    scheduleSettings,
                    guardDaysAssigned));
            }
            }
            catch (Exception e) {
                System.out.println("Invalid data on roster in row: " + (guardRow + 1));
                offerRosterTemplate();
                throw new IllegalArgumentException("Invalid Roster data");

            }
        }
    }

    private boolean stringToBool (String TRUEorFALSE){
        if (TRUEorFALSE.equals("TRUE")){
            return true;
        }
        else if (TRUEorFALSE.equals("FALSE")){
            return false;
        }
        else {
            throw new IllegalArgumentException("Unable to recognize true false condition");
        }
    }

    public String getAsString(){
        int count = guardList.size();
        String rosterString = "Guards on roster" + "\n";
        for (int i=0; i < count; i++){
            rosterString = rosterString + guardList.get(i).toString() + "\n\n";
        }
        return rosterString;
    }

    public void setDaysOff(ValueRange TOASheet){
        TOAs = TOASheet.getValues();
        int row = 0;
        int column = 2;
        try {
            int rowCount = TOAs.size();
            for (int guardRow = 1; guardRow < rowCount; guardRow++){
                row = guardRow;
                String currentGuardFirst = TOAs.get(guardRow).get(0).toString();
                currentGuardFirst = currentGuardFirst.toLowerCase();
                currentGuardFirst = currentGuardFirst.replaceAll("\\s", "");
                if (currentGuardFirst.equals("")==false){
                    String currentGuardLAst = TOAs.get(guardRow).get(1).toString();
                    currentGuardLAst = currentGuardLAst.toLowerCase();
                    currentGuardLAst = currentGuardLAst.replaceAll("\\s", "");
                    ArrayList<Integer> guardTOAList = new ArrayList<Integer>();
                    int currentTOAColumnIndex=2;
                    String TOAdate = "";
                    if (currentTOAColumnIndex<TOAs.get(guardRow).size()){
                        TOAdate = TOAs.get(guardRow).get(currentTOAColumnIndex).toString();
                    }
                    while (currentTOAColumnIndex<TOAs.get(guardRow).size()&&TOAdate.equals("")==false){
                        column = currentTOAColumnIndex + 1;
                        int TOADateID = DATEFUNCTIONS.stringToID(TOAdate); 
                        guardTOAList.add(TOADateID);
                        currentTOAColumnIndex = currentTOAColumnIndex + 1;
                        if (currentTOAColumnIndex<TOAs.get(guardRow).size()){
                            TOAdate= TOAs.get(guardRow).get(currentTOAColumnIndex).toString();
                        }
                    } 
                    Guard currentGuard = getGuard(currentGuardFirst, currentGuardLAst);
                    currentGuard.applyTOAs(guardTOAList);
                }
            } 
        }
        catch (Exception e){
            System.out.println("Error parsing TOAs at " + toA1(row, column));
            System.out.println("Index" + row + ", " + column);
            System.out.println(e);
            throw new IllegalArgumentException();
        }   
    }

    

    private Guard getGuard(String first_name, String last_name){
        String inputFirst = first_name;
        inputFirst = inputFirst.toLowerCase();
        inputFirst = inputFirst.replaceAll("\\s", "");
        String inputLast = last_name;
        inputLast = inputLast.toLowerCase();
        inputLast = inputLast.replaceAll("\\s", "");
        int count = guardList.size();
        for (int i=0;i<count;i++){
            String searchFirst = guardList.get(i).getFirstName();
            String searchLast = guardList.get(i).getLastName();
            if (first_name.equals(searchFirst) && last_name.equals(searchLast)){
                return guardList.get(i);
            }
        }
        String inputFull = inputFirst+inputLast;
        int maxMatchCount=0;
        Guard maxMatchGuard = guardList.get(0);
        for (int i=0; i<count; i++){
            String searchFirst = guardList.get(i).getLastName();
            String searchLast = guardList.get(i).getLastName();
            String searchFull = searchFirst + searchLast;
            int matchCount = 0 ;
            for (char c : inputFull.toCharArray()) {
                if (searchFull.contains(String.valueOf(c))) {
                    matchCount++;
                }
            }
            if (maxMatchCount < matchCount) {
                maxMatchCount = matchCount;
                maxMatchGuard = guardList.get(i);
            }
            matchCount = 0;
        }
        return maxMatchGuard;
    }

    public ArrayList<Guard> getGuardList(){
        return guardList;
    }

    public int getStartDateID (){
        return scheduleStartDayID;
    }
}
