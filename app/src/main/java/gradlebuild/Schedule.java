package gradlebuild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import java.util.List;

import com.google.api.client.util.StringUtils;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.AutoResizeDimensionsRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.MergeCellsRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;

public class Schedule {
    Roster roster;
    
    private ArrayList<scheduleDay> week = new ArrayList<scheduleDay>(0);
    private scheduleDay saturday;
    private scheduleDay sunday;
    private scheduleDay monday;
    private scheduleDay tuesday;
    private scheduleDay wednesday;
    private scheduleDay thursday ;
    private scheduleDay friday;
    private final int EMT_MIN = 1;
    private final int WAVERUNNER_MIN = 2;
    private final int DISPATCH_MIN = 1;
    private final double SRT_PER = (double)0.01;
    private final double BH_MIN = 2;

    private ArrayList<Guard> fullGuardList;
    private boolean overtime;
    private boolean toaAdditional;
    private int toaAdditionalCap;

    public Schedule (Roster scheduleRoster, Settings settings){
        roster = scheduleRoster;
        saturday = new scheduleDay(roster);
        sunday = new scheduleDay(roster);
        monday = new scheduleDay(roster);
        tuesday = new scheduleDay(roster);
        wednesday = new scheduleDay(roster);
        thursday = new scheduleDay(roster);
        friday = new scheduleDay(roster);
        overtime = settings.isOvertime();
        toaAdditional = settings.isToaAdditional();
        toaAdditionalCap = settings.getToaAdditionalCap();
        week.add(0, friday);
        week.add(0, thursday);
        week.add(0, wednesday);
        week.add(0, tuesday);
        week.add(0, monday);
        week.add(0, sunday);
        week.add(0, saturday);
        week.get(0).setTargetHeadCount(settings.getHeadCountTarget(0));
        week.get(1).setTargetHeadCount(settings.getHeadCountTarget(1));
        week.get(2).setTargetHeadCount(settings.getHeadCountTarget(2));
        week.get(3).setTargetHeadCount(settings.getHeadCountTarget(3));
        week.get(4).setTargetHeadCount(settings.getHeadCountTarget(4));
        week.get(5).setTargetHeadCount(settings.getHeadCountTarget(5));
        week.get(6).setTargetHeadCount(settings.getHeadCountTarget(6));
        //System.out.println("Week is" + week);
        ArrayList<Guard> inputGuardList = roster.getGuardList();
        fullGuardList = (ArrayList<Guard>)inputGuardList.clone();
        //System.out.println("YAY");
        buildSchedule();
    }
    private int weekIndexReorder(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 6;
            case 2:
                return 1;
            case 3:
                return 5;
            case 4:
                return 2;
            case 5:
                return 4;
            case 6:
                return 3;
            default:
                throw new IllegalArgumentException("Invalid input: " + i);
        }
    }
    private void buildSchedule(){
        ArrayList<Guard> buildGuardList = (ArrayList<Guard>)fullGuardList.clone();
        Collections.sort(buildGuardList,Comparator.comparing(Guard::getOffCount).reversed().thenComparing(Comparator.comparingInt(Guard::getYear).reversed())); 
        int workingCapacity = 0;
        for (int i = 0; i < buildGuardList.size(); i++){
            workingCapacity = workingCapacity + 7 - buildGuardList.get(i).getOffCount();
        }
        while(buildGuardList.size()!=0){

            Guard currentGuard = buildGuardList.remove(0);  
            int maxWork = currentGuard.getDaysAssigned();
            if (currentGuard.isAdult()==true&&overtime==true){
                maxWork = 6;
            }
            if (toaAdditional==true){
                int toaCount = 0;https://docs.google.com/spreadsheets/d/10GF8fdIgIExouHdAbTl3MnwJCQhyp04xXIK_Mxubdgw/edit#gid=0
                for (int i =0; i<7; i++){
                    if (currentGuard.isTOA(roster.getStartDateID()+i)){
                        toaCount = toaCount+1;
                    }
                }
                if (toaCount>toaAdditionalCap){
                    toaCount = toaAdditionalCap;
                }
                maxWork = maxWork - toaCount;
                if (maxWork<0){
                    maxWork = 0;
                }
            }

            //EMT
            if (currentGuard.isEMT()==true){
                for (int i = 0; i<7; i++){
                    if (currentGuard.isAvailable(weekIndexReorder(i))==true){
                    scheduleDay currentDay = week.get(weekIndexReorder(i));
                    if (currentDay.isWorking(currentGuard)==false&&currentGuard.getWork()<maxWork){
                        if(currentDay.emtCount()<EMT_MIN){
                            currentDay.assign(currentGuard);
                        }
                    }
                    }
                }
            }

            //waverunner
            if (currentGuard.isWaverunner()==true){
                for (int i = 0; i<7; i++){
                    if (currentGuard.isAvailable(weekIndexReorder(i))==true){
                    scheduleDay currentDay = week.get(weekIndexReorder(i));
                    if (currentDay.isWorking(currentGuard)==false&&currentGuard.getWork()<maxWork){
                        if(currentDay.waverunnerCount()<WAVERUNNER_MIN){
                            currentDay.assign(currentGuard);
                        }
                    }
                    }
                }
            }

            //dispatch
            if (currentGuard.isDispatch()==true){
                for (int i = 0; i<7; i++){
                    if (currentGuard.isAvailable(weekIndexReorder(i))==true){
                    scheduleDay currentDay = week.get(weekIndexReorder(i));
                    if (currentDay.isWorking(currentGuard)==false&&currentGuard.getWork()<maxWork){
                        if(currentDay.dispatchCount()<DISPATCH_MIN){
                            currentDay.assign(currentGuard);
                        }
                    }
                    }
                }
            }
            
            //srt 
            if (currentGuard.isSRT()==true){
                for (int i = 0; i<7; i++){
                    if (currentGuard.isAvailable(weekIndexReorder(i))==true){
                    scheduleDay currentDay = week.get(weekIndexReorder(i));
                    if (currentDay.isWorking(currentGuard)==false&&currentGuard.getWork()<maxWork){
                        if(currentDay.srtCount()<SRT_PER*workingCapacity){
                            currentDay.assign(currentGuard);
                        }
                    }
                    }
                }
            }

            //dispatch
            if (currentGuard.isBeachhouse()==true){
                for (int i = 0; i<7; i++){
                    if (currentGuard.isAvailable(weekIndexReorder(i))==true){
                    scheduleDay currentDay = week.get(weekIndexReorder(i));
                    if (currentDay.isWorking(currentGuard)==false&&currentGuard.getWork()<maxWork){
                        if(currentDay.beachhouseCount()<BH_MIN){
                            currentDay.assign(currentGuard);
                        }
                    }
                    }
                }
            }

            //place based on lowest percentage of target
            ArrayList<scheduleDay> fillOrderWeek = (ArrayList<scheduleDay>) week.clone();
            Collections.shuffle(fillOrderWeek);
            Collections.sort(fillOrderWeek,Comparator.comparing(scheduleDay::getPercentage).reversed());
            for (int i = 6; i>=0; i=i-1){
                scheduleDay currentDay = fillOrderWeek.get(i);
                int weekIndex = 0;
                for (int j=0;j<7; j=j+1) {
                    if (week.get(j).equals(currentDay)){
                        weekIndex = j;
                    }
                }
                if (currentDay.isWorking(currentGuard)==false&&currentGuard.getWork()<maxWork&&currentGuard.isAvailable(weekIndex)==true){
                    currentDay.assign(currentGuard);
                }
            }      
        } 
    }

    public String capitalize(String input) {
    if (input == null || input.isEmpty()) {
        return input;
    }
    
    char firstChar = Character.toUpperCase(input.charAt(0));
    String restOfString = input.substring(1);
    
    return firstChar + restOfString;
    }
    
    public void printSchedule(){
        System.out.println("Saturday");
        System.out.println(week.get(0).toString());
        System.out.println("Sunday");
        System.out.println(week.get(1).toString());
        System.out.println("Monday");
        System.out.println(week.get(2).toString());
        System.out.println("Tuesday");
        System.out.println(week.get(3).toString());
        System.out.println("Wednesday");
        System.out.println(week.get(4).toString());
        System.out.println("Thursday");
        System.out.println(week.get(5).toString());
        System.out.println("Friday");
        System.out.println(week.get(6).toString());
    }

    public String createSpreadsheet(Sheets service) throws IOException{
        //Create new spreadsheet
        Spreadsheet resultsSpreadsheet = new Spreadsheet()
        .setProperties(new SpreadsheetProperties()
            .setTitle("Schedule " + DATEFUNCTIONS.iDtoString(roster.getStartDateID())));
        resultsSpreadsheet = service.spreadsheets().create(resultsSpreadsheet)
        .setFields("spreadsheetId")
        .execute();
        // Prints the new spreadsheet id
        String resultsID = resultsSpreadsheet.getSpreadsheetId();

        //Create the Sheets for working and days off
        List<Request> sheetSetupRequests = new ArrayList<>();
        sheetSetupRequests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties()
            .setTitle("Working"))));
        sheetSetupRequests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties()
            .setTitle("Off"))));
        sheetSetupRequests.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(0)));
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(sheetSetupRequests);
        service.spreadsheets().batchUpdate(resultsID, body)
            .execute();
        
        //Get the IDs of the sheets we cretaed    
        Sheet workingSheet = service.spreadsheets().get(resultsID).execute()
                .getSheets().stream()
                .filter(s -> s.getProperties().getTitle().equals("Working"))
                .findFirst().orElse(null);
        
        
        int workingID = workingSheet.getProperties().getSheetId();

        Sheet offSheet = service.spreadsheets().get(resultsID).execute()
                .getSheets().stream()
                .filter(s -> s.getProperties().getTitle().equals("Off"))
                .findFirst().orElse(null);
        
        
        int offID = offSheet.getProperties().getSheetId();

        List<String> dayLabels = new ArrayList<>();
        dayLabels.add("Saturday");
        dayLabels.add("Sunday");
        dayLabels.add("Monday");
        dayLabels.add("Tuesday");
        dayLabels.add("Wednesday");
        dayLabels.add("Thursday");
        dayLabels.add("Friday");

        List<String> dateLabels = new ArrayList<>();
        dateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()));
        dateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+1));
        dateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+2));
        dateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+3));
        dateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+4));
        dateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+5));
        dateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+6));


        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setMergeCells(new MergeCellsRequest()
                .setRange(new GridRange().setSheetId(workingID).setStartColumnIndex(0).setStartRowIndex(0).setEndColumnIndex(7).setEndRowIndex(1))));

        for (int i=0; i<7; i++){
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, 1, i, dayLabels.get(i), "black", "white", true));
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, 2, i, dateLabels.get(i), "black", "white", true));
        }

        String title = "Guard Working For The Week of " + DATEFUNCTIONS.iDtoString(roster.getStartDateID()) + " (Schedulizer Generated)";
        requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, 0, 0, title, "black", "white", true));

        int[] emtCounts = new int[]{0,0,0,0,0,0,0};
        int[] srtCounts = new int[]{0,0,0,0,0,0,0};
        int[] beachhouseCounts = new int[]{0,0,0,0,0,0,0};
        int[] waverunnerCounts = new int[]{0,0,0,0,0,0,0};
        int[] disptachCounts = new int[]{0,0,0,0,0,0,0};
        int[] fourUpCounts = new int[]{0,0,0,0,0,0,0};
        int[] threeCounts = new int[]{0,0,0,0,0,0,0};
        int[] twoCounts = new int[]{0,0,0,0,0,0,0};
        int[] oneCounts = new int[]{0,0,0,0,0,0,0};

        int bottomRow = 6;
        for (int i=0; i<7;i++){
            scheduleDay currentDay = week.get(i);
            ArrayList<Guard> working = currentDay.getWorking();
            Collections.sort(working,Comparator.comparingInt(Guard::getYear).reversed().thenComparing(Comparator.comparing(Guard::getLastName)));
            for (int j=0;j<working.size();j++){
                Guard currentGuard = working.get(j);
                String nameLabel = currentGuard.getYear() + " " + capitalize(currentGuard.getLastName()) + ", " +capitalize(currentGuard.getFirstName()) + " ";
                if (currentGuard.getBirthdayID()>(roster.getStartDateID()+i-(365*18))){
                    nameLabel = nameLabel + "|M";
                }
                if (currentGuard.isEMT()==true){
                    nameLabel = nameLabel + "|EMT";
                }
                if (currentGuard.isSRT()==true){
                    nameLabel = nameLabel + "|SRT";
                }
                if (currentGuard.isDispatch()==true){
                    nameLabel = nameLabel + "|DSP";                   
                }
                if (currentGuard.isBeachhouse()==true){
                    nameLabel = nameLabel + "|BH";   
                }
                String txColor = "lightblue";
                if (currentGuard.getYear()>3){
                    txColor = "magenta";
                }
                else if(currentGuard.getYear()>1){
                    txColor = "blue";
                }
                requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, j+3, i, nameLabel, txColor, "white", true));
                if (currentGuard.isEMT()){
                    emtCounts[i]=emtCounts[i]+1;
                }
                if (currentGuard.isSRT()){
                    srtCounts[i]=srtCounts[i]+1;
                }
                if (currentGuard.isBeachhouse()){
                    beachhouseCounts[i]=beachhouseCounts[i]+1;
                }
                if (currentGuard.isWaverunner()){
                    waverunnerCounts[i]=waverunnerCounts[i]+1;
                }
                if (currentGuard.isDispatch()){
                    disptachCounts[i]=disptachCounts[i]+1;
                }
                if(currentGuard.getYear()>=4){
                    fourUpCounts[i] = fourUpCounts[i]+1;
                }
                else if(currentGuard.getYear()==3){
                    threeCounts[i] = threeCounts[i]+1;
                }
                else if(currentGuard.getYear()==2){
                    twoCounts[i] = twoCounts[i]+1;
                }
                else if(currentGuard.getYear()==1){
                    oneCounts[i] = oneCounts[i]+1;
                }

                if(j+4>bottomRow){
                    bottomRow = j+4;
                }
            }
        }
        for (int i=0; i< 7; i++){
            String label = "Totals:";
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+2, i, label, "white", "lightblue", true));
            String guardsLabel = "Guards: " + week.get(i).getWorking().size();
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+3, i, guardsLabel, "black", "white", true));
            String srtLabel = "SRTs: " + srtCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+4, i, srtLabel, "black", "white", true));
            String emtLabel = "EMTs: " + emtCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+5, i, emtLabel, "black", "white", true));
            String dispatchLabel = "Disps: " + disptachCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+6, i, dispatchLabel, "black", "white", true));
            String beachhouseLabel = "BHs: " + beachhouseCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+7, i, beachhouseLabel, "black", "white", true));
            String waverunnerLabel = "RWCs: " + waverunnerCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+8, i, waverunnerLabel, "black", "white", true));
            String fourUpLabel = "4+yrs: " + fourUpCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+9, i, fourUpLabel, "black", "white", true));
            String threeLabel = "3yrs: " + threeCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+10, i, threeLabel, "black", "white", true));
            String twoLabel = "2yrs: " + twoCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+11, i, twoLabel, "black", "white", true));
            String oneLabel = "1yrs: " + oneCounts[i];
            requests.add(SHEETS_FUNCTIONS.createCellFormatRequest(workingID, bottomRow+12, i, oneLabel, "black", "white", true));
        }
        


       BatchUpdateSpreadsheetRequest workingBatchUpdate = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(resultsID, workingBatchUpdate)
            .execute();


        //OFF SHEET
        


        List<Request> offSheetRequests = new ArrayList<>();
        
        Request offTitleMergeRequest = new Request().setMergeCells(new MergeCellsRequest()
                .setRange(new GridRange().setSheetId(offID).setStartColumnIndex(0).setStartRowIndex(0).setEndColumnIndex(7).setEndRowIndex(1)));
        offSheetRequests.add(offTitleMergeRequest);

        List<String> weekLabels = new ArrayList<>();
        weekLabels.add("Saturday");
        weekLabels.add("Sunday");
        weekLabels.add("Monday");
        weekLabels.add("Tuesday");
        weekLabels.add("Wednesday");
        weekLabels.add("Thursday");
        weekLabels.add("Friday");
        
        List<String> weekDateLabels = new ArrayList<>();
        weekDateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()));
        weekDateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+1));
        weekDateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+2));
        weekDateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+3));
        weekDateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+4));
        weekDateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+5));
        weekDateLabels.add(DATEFUNCTIONS.iDtoString(roster.getStartDateID()+6));

        offSheetRequests.add(SHEETS_FUNCTIONS.createFillRangeRequest(offID, 1, 0, weekLabels, true, 3));
        offSheetRequests.add(SHEETS_FUNCTIONS.createFillRangeRequest(offID, 2, 0, weekDateLabels, true, 3));

        for (int weekDay = 0; weekDay < 7; weekDay++){
            scheduleDay currentDay = week.get(weekDay);
            ArrayList<Guard> offList = currentDay.getOff();
            Collections.sort(offList,Comparator.comparingInt(Guard::getYear).reversed().thenComparing(Comparator.comparing(Guard::getLastName)));
            for (int guardIndex=0;guardIndex<offList.size();guardIndex++){
                int columnIndex = weekDay;
                int rowIndex = guardIndex + 3;
                Guard currentGuard = offList.get(guardIndex);
                String cellText = currentGuard.getYear() + " " + capitalize(currentGuard.getLastName()) + ", " +capitalize(currentGuard.getFirstName()) + " ";
                if (currentGuard.getBirthdayID()>(roster.getStartDateID()+weekDay-(365*18))){
                    cellText = cellText + "|M";
                }
                if (currentGuard.isEMT()==true){
                    cellText = cellText + "|EMT";
                }
                if (currentGuard.isSRT()==true){
                    cellText = cellText + "|SRT";
                }
                if (currentGuard.isDispatch()==true){
                    cellText = cellText + "|DSP";                   
                }
                if (currentGuard.isBeachhouse()==true){
                    cellText = cellText + "|BH";   
                }
                String textColor = "black";
                String backGroundColor = "pink";
                if (currentGuard.getFirstDayID()> roster.getStartDateID()+weekDay){
                    textColor = "white";
                    backGroundColor = "black";
                }
                else if (currentGuard.getLastDayID()< roster.getStartDateID()+weekDay){
                    textColor = "white";
                    backGroundColor = "black";
                }
                else if (currentGuard.isTOA(roster.getStartDateID()+weekDay)){
                    backGroundColor = "red";
                }
                offSheetRequests.add(SHEETS_FUNCTIONS.createCellFormatRequest(offID, rowIndex, columnIndex, cellText, textColor, backGroundColor, true));
            }
        }

        offSheetRequests.add(new Request().setAutoResizeDimensions(new AutoResizeDimensionsRequest().setDimensions(new DimensionRange().setSheetId(offID).setDimension("COLUMNS").setStartIndex(0).setEndIndex(7))));
        offSheetRequests.add(new Request().setAutoResizeDimensions(new AutoResizeDimensionsRequest().setDimensions(new DimensionRange().setSheetId(workingID).setDimension("COLUMNS").setStartIndex(0).setEndIndex(7))));
        
        //Title added after to prevent resize from trying autofit
        List<String> offTitleList =  new ArrayList<>();
        offTitleList.add("Days of for the week of " + DATEFUNCTIONS.iDtoString(roster.getStartDateID()));
        offSheetRequests.add(SHEETS_FUNCTIONS.createFillRangeRequest(offID, 0, 0, offTitleList, true, 3));

        BatchUpdateSpreadsheetRequest offBatch = new BatchUpdateSpreadsheetRequest().setRequests(offSheetRequests);
        service.spreadsheets().batchUpdate(resultsID, offBatch)
            .execute();
        return resultsID;
    }
}
