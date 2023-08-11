package gradlebuild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class scheduleDay {
    private ArrayList<Guard> working = new ArrayList<Guard>(0);
    private int targetHeadCount;
    private Roster roster;
    scheduleDay(Roster roster){
        this.roster = roster;
        targetHeadCount = 100;
    };

    public void setTargetHeadCount(int targetHeadCount) {
        this.targetHeadCount = targetHeadCount;
    }

    public int getTragetHeadCount(){
        return targetHeadCount;
    }

    public String toString(){
        String asString = "";
        for (int i=0; i < working.size(); i ++){
            Guard currentGuard = working.get(i);
            asString = asString + currentGuard.getLastName()+", " +  currentGuard.getFirstName() + "\n";
        }

        asString = asString + "\nTotal: " + working.size() + "\n"
        + "EMTs: " + emtCount() + "\n" 
        + "SRTs: " + srtCount() + "\n"
        + "Dispatchs: " + dispatchCount() + "\n"
        + "Waverunners: " + waverunnerCount() + "\n";
        return asString;
    }

    public ArrayList<Guard> getWorking(){
        return working;
    }

    public ArrayList<Guard> getOff(){
        ArrayList<Guard> fullGuardList = roster.getGuardList();
        ArrayList<Guard> offList = new ArrayList<>();
        for (int i = 0; i <fullGuardList.size(); i++){
            if(working.contains(fullGuardList.get(i))== false){
                offList.add(fullGuardList.get(i));
            }
        }
        Collections.sort(offList,Comparator.comparing(Guard::getLastName).reversed().thenComparing(Comparator.comparing(Guard::getFirstName)));
        return offList;
    }

    public void assign(Guard guard){
        working.add(0,guard);
        guard.addWork();
    };

    public boolean isWorking(Guard guard){
        for (int i = 0; i < working.size(); i++){
            if (working.get(i).equals(guard)){
                return true;
            }
        }
        return false;
    }

    public int emtCount(){
        int emts = 0;
        for (int i = 0; i < working.size(); i++){
            if (working.get(i).isEMT()){
                emts = emts + 1;
            }
        }
        return emts;
    }

    public int waverunnerCount(){
        int wrs = 0;
        for (int i = 0; i < working.size(); i++){
            if (working.get(i).isWaverunner()){
                wrs = wrs + 1;
            }
        }
        return wrs;
    }

    public int dispatchCount(){
        int dbs = 0;
        for (int i = 0; i < working.size(); i++){
            if (working.get(i).isDispatch()){
                dbs = dbs + 1;
            }
        }
        return dbs;
    }

    public int beachhouseCount(){
        int bhs = 0;
        for (int i = 0; i < working.size(); i++){
            if (working.get(i).isBeachhouse()){
                bhs = bhs + 1;
            }
        }
        return bhs;
    }

    public int srtCount(){
        int srts = 0;
        for (int i = 0; i < working.size(); i++){
            if (working.get(i).isSRT()){
                srts = srts + 1;
            }
        }
        return srts;
    }

    public double getPercentage(){
        if (targetHeadCount==0){
            return 20;
        }
        double percentage = (double)working.size()/targetHeadCount;
        return percentage;
    }    
}
