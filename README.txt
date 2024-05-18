Schedulizer - Avalon Beach Patrol Schedule Builder
Description
The Schedulizer is a Java program designed to facilitate the creation of schedules for the Avalon Beach Patrol. Its purpose is to efficiently allocate the available lifeguards based on their availability, qualifications, and the daily requirements of the beach patrol. 

Key Features:
Dynamic Adaptability: The Schedulizer is flexible and adaptable to the ever-changing demands of the beach patrol. It can quickly adjust schedules to accommodate fluctuations in crowd size and respond to emergencies efficiently.

Qualifications Management: The program intelligently manages the qualifications of lifeguards, ensuring that only qualified personnel are assigned to specific roles, such as medical personnel, waverunner operators, and surf rescue technicians (SRT).

Resource Optimization: With the Schedulizer, resources are optimally distributed to meet the daily requirements of the Avalon Beach Patrol. This helps in ensuring the safety of beachgoers and providing prompt assistance when needed.

Google Sheets Integration: The Schedulizer uses the google sheets API to integrate with cohesively with existing the patrols existing google sheets based schedule management systems. 

Automated Schedule Generation: By leveraging available data and predefined rules, the Schedulizer automates the schedule generation process, saving time and effort for the beach patrol staff.

Detailed Reports: The Schedulizer provides comprehensive reports on schedules, resource allocation, and qualification status, enabling efficient monitoring and analysis of beach patrol operations.

How to Use the Schedulizer - Avalon Beach Patrol Schedule Builder

Building the Program:

Clone or download the Schedulizer repository to your local machine.
Located at https://drive.google.com/drive/folders/1F5GXFU30iM1yxlKYq8S4vd9w_zgbNqcf?usp=sharing.
Locate and run the "userRebuild" batch file to build the program and its dependencies.

Running the Program:

Once the program is successfully built, run the "userRun" batch file to execute the Schedulizer.
The program will prompt you to enter the start date for the schedule. This date will be used as the beginning of the weekly schedule.

Preparing the Roster and TOA Sheets:
Before running the Schedulizer, you need two essential Google Sheets:

Roster Sheet: This sheet contains information about each guard, including start date, final date, year, birthday, qualifications, and the number of days they are assigned to work per week.
TOA Sheet (Time Off Approval): This sheet holds the time-off requests for each guard.
To create the sheets, you can use the templates provided in the Google Drive link: https://drive.google.com/drive/folders/1F5GXFU30iM1yxlKYq8S4vd9w_zgbNqcf?usp=sharing.
If pulling from the templates folder, it is recommended to upload directly to google without using excel to make any changes. 

Make a copy of both templates and save them to your Google Drive.
Ensure that the TOA entries are left-justified (aligned to the left) in the TOA sheet, although the order of the entries does not matter.
The names of guards in the TOA sheet must exactly match the names and spelling used in the Roster sheet.

It is crucial to maintain the formatting of the templates when setting up your own Roster and TOA sheets.

Using the Schedulizer:

After entering the start date, the Schedulizer will generate a weekly schedule based on the information provided in the Roster and TOA sheets.

Default settings are applied during the schedule generation process, but you have the option to customize settings for individual guards:

Additional Days Off: Adjust the number of additional days off a guard receives for TOAs, reducing the total working hours when overstaffed.

Overtime: You can assign overtime using the overtime setting or by directly changing the days assigned in the Roster sheet.

Distribution Settings: Modify daily targets to handle holidays or other events, affecting how work is assigned throughout the week. The daily targets act as weights for work distribution.
Once the schedule is generated, the Schedulizer will automatically open the schedule in your web browser. Additionally, it will print out a link to access the schedule.

Please note that the schedule is saved by default in the main directory of your Google Drive. To avoid duplicates, consider changing the location and name of the schedule file.

The Schedulizer streamlines the process of creating and managing schedules for the Avalon Beach Patrol. By leveraging the Google Sheets API, it offers efficient data synchronization, real-time updates, and enhanced collaboration. The program's flexibility allows you to adapt schedules to varying crowd sizes, optimize resource allocation, and ensure the safety and effectiveness of the beach patrol's operations.

For debugging issues, open cmd and go the cd //file_save_folder
manually run by entering, gradlew run
This will allow the Java debug to remain visible and show what line the code is in. 

Occasionally your API Token will expire this can be reset by delting the Stored Credential in the tokens folder. 

To use the Schedulizer, you need to have Java JDK installed on your computer. Follow these steps to install Java and set it up in your system path:

Download Java JDK:

Go to the Oracle JDK download page: https://www.oracle.com/java/technologies/javase-jdk17-downloads.html (or the latest version if available).
Scroll down to find the version suitable for your operating system (Windows, macOS, or Linux).
Click the "Download" button for the JDK package (not JRE).
You may need to accept the Oracle Binary Code License Agreement before proceeding with the download.
Install Java JDK:

Once the JDK installer is downloaded, run the installation program.
Follow the installation wizard's instructions to complete the JDK installation process.
During the installation, you may be prompted to specify the installation directory. Make note of the path as you will need it later.

Adding Java to System Path for Gradle Wrapper:

To add Java to the system path specifically for the Gradle Wrapper, follow these steps:

Find the Java installation directory:

Open File Explorer and navigate to the location where Java is installed (e.g., "C:\Program Files\Java\jdk-xxx" for JDK).
Copy the complete path of the Java installation directory.
Set up Java in the system path for the Gradle Wrapper:

Open the command prompt (cmd) as an administrator (right-click on cmd and select "Run as administrator").
Enter the following command, replacing "YOUR_JAVA_PATH" with the path to the Java installation directory you copied earlier:
setx /M PATH "%PATH%;YOUR_JAVA_PATH\bin"
For example:
setx /M PATH "%PATH%;C:\Program Files\Java\jdk-17.0.1\bin"
Close the command prompt and reopen it for the changes to take effect.

Verifying Java in System Path:

After adding Java to the system path, open a new command prompt (cmd) session.

Type the following command to verify if Java is accessible from anywhere in the system:
java -version
If Java is correctly set up in the system path, you should see the installed version of Java without any errors.

By checking Java's presence and setting up the correct system path, you ensure that the Schedulizer can utilize Java for the Gradle Wrapper to build and execute the Java program effectively.

GOOGLE SHEETS CREDENTIALS 

for security purposes, the google sheets credentials file has not been included. 

Obtain Google Sheets API Credentials:

Go to the Google Cloud Console.
Create a new project or select an existing project.
Navigate to the APIs & Services > Credentials section.
Click on the Create credentials dropdown and select Service Account Key.
Select the service account (or create a new one), set the key type to JSON, and click Create.
Save the generated JSON file containing your credentials as credentials.json.
Place credentials.json in the resources directory of this project.

