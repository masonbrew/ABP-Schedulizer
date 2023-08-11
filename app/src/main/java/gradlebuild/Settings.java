package gradlebuild;

import java.util.Scanner;

public class Settings {
    private boolean overtime;
    private boolean toaAdditional;
    private int startDateID;
    private int toaAdditionalCap;
    private String startDateString;
    private Scanner scanner;
    int[] targetHeadCountWeek = new int[7];
    public Settings(Scanner sc){
        //setup default settings
        scanner = sc;
        overtime = false;
        toaAdditional = false;
        toaAdditionalCap = 7;
        targetHeadCountWeek[0] = 120;
        targetHeadCountWeek[1] = 120;
        targetHeadCountWeek[2] = 100;
        targetHeadCountWeek[3] = 100;
        targetHeadCountWeek[4] = 100;
        targetHeadCountWeek[5] = 100;
        targetHeadCountWeek[6] = 100;
    }

    public void userSetup(){
        setupAdditionalToa(scanner);
        setupOvertime(scanner);
        setupDayTarget(scanner);
    }

    private void setupAdditionalToa(Scanner scanner){
        //Scanner scanner = new Scanner(System.in);
        boolean complete = false;
        while (complete == false){
        System.out.println("");
        System.out.print("Are TOAs additional days off?(Y/N):");
        String input = scanner.nextLine();
        if (input.toLowerCase().equals("y")){
            System.out.println("");
            System.out.println("TOAs will be an additional day");
            System.out.println("");
            toaAdditional = true;
            complete = true;
            System.out.println("");
            System.out.print("Is there a cap for additional days off?(Y/N):");
            input = scanner.nextLine();
                if (input.toLowerCase().equals("y")){
                    System.out.println("");
                    System.out.println("Enter the cap for additional days:");
                    System.out.println("");
                    toaAdditionalCap = Integer.parseInt(scanner.nextLine());
                }

        }else if (input.toLowerCase().equals("n")){
            toaAdditional = false;
            complete = true;
            System.out.println("");
            System.out.println("TOAs will not be an additional day");
            System.out.println("");
        }
        else {
            System.out.println("");
            System.out.println("Invalid input");
            System.out.println("");
        }
        //scanner.close();
        }
    }

    private void setupOvertime(Scanner scanner){
        //Scanner scanner = new Scanner(System.in);
        boolean complete = false;
        while (complete == false){
        System.out.println("");
        System.out.print("Is overtime available for 18+ guards?(Y/N):");
        String input = scanner.nextLine();
        if (input.toLowerCase().equals("y")){
            System.out.println("");
            System.out.println("Overtime will be given");
            System.out.println("");
            overtime = true;
            complete = true;
        }else if (input.toLowerCase().equals("n")){
            overtime = false;
            complete = true;
            System.out.println("");
            System.out.println("Overtime will not be given");
            System.out.println("");
        }
        else {
            System.out.println("");
            System.out.println("Invalid input");
            System.out.println("");
        }
        //scanner.close();
        }
    }

    private void setupDayTarget(Scanner sc){
        boolean complete = false;
        while (complete == false){
        System.out.println("");
        System.out.print("Use default daily guard count targets?(Y/N):");
        System.out.println("");
        //Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        if (input.toLowerCase().equals("y")){
            System.out.println("");
            System.out.println("Using default daily targets");
            System.out.println("");
            complete = true;
        }else if (input.toLowerCase().equals("n")){
            complete = true;
            System.out.println();
            System.out.println("");
            System.out.print("Saturday target headcount");
            System.out.println("");
            targetHeadCountWeek[0] = sc.nextInt();
            System.out.println("");
            System.out.print("Sunday target headcount");
            System.out.println("");
            targetHeadCountWeek[1] = sc.nextInt();
            System.out.println("");
            System.out.print("Monday target headcount");
            System.out.println("");
            targetHeadCountWeek[2] = sc.nextInt();
            System.out.println("");
            System.out.print("Tuesday target headcount");
            System.out.println("");
            targetHeadCountWeek[3] = sc.nextInt();
            System.out.println("");
            System.out.print("Wednesday target headcount");
            System.out.println("");
            targetHeadCountWeek[4] = sc.nextInt();
            System.out.println("");
            System.out.print("Thursday target headcount");
            System.out.println("");
            targetHeadCountWeek[5] = sc.nextInt();
            System.out.println("");
            System.out.print("Friday target headcount");
            System.out.println("");
            targetHeadCountWeek[6] = sc.nextInt();
        }
        else {
            System.out.println("");
            System.out.println("Invalid input");
            System.out.println("");
        }
        }
        
    }

    public boolean isToaAdditional(){
        return toaAdditional;
    }

    public int getToaAdditionalCap(){
        return toaAdditionalCap;
    }

    public void setStartDate(String startDate){
        try {
        startDateID = DATEFUNCTIONS.stringToID(startDate);
        }
        catch (IllegalArgumentException illegalDateException) {
            System.out.println("");
            System.out.println("Start date: Invalid date or date format");
            System.out.println("");
            throw new IllegalArgumentException("setStartDate");
        }
    }

    public int getHeadCountTarget(int dayIndex){
        return targetHeadCountWeek[dayIndex];
    }

    public boolean isOvertime(){
        return overtime;
    }

    public String getStartDateString(){
        return startDateString;
    }
    public int getStartDateID(){
        return startDateID;
    }

    
}
