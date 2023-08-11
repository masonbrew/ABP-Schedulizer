package gradlebuild;

import java.util.ArrayList;

public class Guard {
    private String firstName;
    private String lastName;
    private int year;
    private int birthdayID;
    private int firstDayID;
    private int lastDayID;
    private boolean waverunner;
    private boolean dispatch;
    private boolean srt;
    private boolean beachhouse;
    private boolean emt;
    private boolean[] canWork = new boolean[7];
    private int scheduleStartDayID;
    private boolean is18;
    private int work;
    private int daysAssigned;
    private Settings scheduleSettings;
    private ArrayList<Integer> toaIDs = new ArrayList<Integer>();

    public Guard(String guardFirst, String guardLast, int guardYear,int guardBirthdayID, boolean waverunnerTrained, boolean dispatchTrained, boolean srtTrained, boolean beachhouseTrained, boolean emtTrained, int guardFirstDayID, int guardLastDayID, Settings settings, int guardDaysAssigned) {
        work = 0;
        firstName = guardFirst;
        lastName = guardLast;
        year = guardYear;
        birthdayID = guardBirthdayID;
        waverunner = waverunnerTrained;
        dispatch = dispatchTrained;
        srt = srtTrained;
        beachhouse = beachhouseTrained;
        emt = emtTrained;
        firstDayID = guardFirstDayID;
        lastDayID = guardLastDayID;
        scheduleSettings = settings;
        daysAssigned = guardDaysAssigned;
        scheduleStartDayID = settings.getStartDateID();
        if (scheduleStartDayID-guardBirthdayID>=365*18){
                    is18 = true;
                }
                else{
                    is18 = false;
                } 
        setupCanWork();
    }
    private void setupCanWork(){
        for (int i = 0; i<7; i ++){
            if (firstDayID<=scheduleStartDayID+i&&lastDayID>=scheduleStartDayID+i){
                canWork[i] = true;
            }
            else {
                canWork[i] = false;
            }
        }
    }

    public void addWork(){
        work = work + 1;
    }

    public int getWork(){
        return work;
    }

    public int getDaysAssigned(){
        return daysAssigned;
    }

    public void applyTOAs(ArrayList<Integer> toaIDs){
        this.toaIDs = toaIDs;
        int toaCount = toaIDs.size();
        if (toaCount>0){
            for (int i =0; i<toaCount; i++){
                int toaDayID = toaIDs.get(i);
                int currentDay = toaDayID - scheduleStartDayID;
                switch (currentDay){
                    case 0:
                        cantWork(0);
                        break;
                    case 1:
                        cantWork(1);
                        break;
                    case 2:
                        cantWork(2);
                        break;
                    case 3:
                        cantWork(3);
                        break;
                    case 4:
                        cantWork(4);
                        break;
                    case 5:
                        cantWork(5);
                        break;
                    case 6:
                        cantWork(6);
                        break;
                    default:
                }
            }
        }
    }

    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public int getYear(){
        return year;
    }
    public int getBirthdayID(){
        return birthdayID;
    }
    public boolean isWaverunner(){
        return waverunner;
    }
    public boolean isDispatch(){
        return dispatch;
    }
    public boolean isSRT(){
        return srt;
    }
    public boolean isBeachhouse(){
        return beachhouse;
    }
    public boolean isEMT(){
        return emt;
    }
    public int getFirstDayID(){
        return firstDayID;
    }
    public int getLastDayID(){
        return lastDayID;
    }
    public void cantWork(int dayIndex){
        canWork[dayIndex] = false;
    }
    public String toString(){
        String guardString =
        firstName + " " +
        lastName + "\n" +
        "year:" + year  + "\n" +
        "birthdayID:" + birthdayID + "\n" + 
        "waverunner:" + waverunner + "\n" +
        "dispatch:" + dispatch + "\n" +
        "srt:" + srt + "\n" +
        "beachhouse:" + beachhouse + "\n" + 
        "emt:" + emt + "\n" +
        "firstDayID:" + firstDayID + "\n" +
        "lastDayID:" + lastDayID + "\n" +
        "Canwork";
        for (int i=0; i<7; i++){
            guardString = guardString + canWork[i] + ",";
        }
        return guardString;
    }

    public int getOffCount(){
        int offCount = 0;
        for (int i =0; i<7; i++){
            if (canWork[i]==false){
                offCount = offCount + 1;
            }
        }
        return offCount;
    }

    public boolean isAvailable(int dayIndex){
        if (dayIndex>7||dayIndex<0){
            throw new IndexOutOfBoundsException("Error in day of week called");
        }
        boolean result = canWork[dayIndex];
        return result;
    }

    public boolean isAdult(){
        return is18;
    }

    public boolean isTOA(int dayID){
        if (toaIDs.contains(dayID)){
            return true;
        }
        return false;
    }
}
