# Assignment04-Rest Blood Bank

**Theme for the Group Project**

The theme for the Group Project is to bring together everything you have learned this term:

**1. JPA –** for model objects

**2. Session beans –** for business logic

**3. REST –** representation of back-end resources

**4. JEE Security Roles –** controls who can invoke which operation

**5. Testing using JUnit –** a series of testcases that demonstrate the operation of the system


**General Instructions**

Below is the link for a collection of instructions in this course. You do not have to do everything mentioned in them. Find what you need and simply follow those instructions.

**OS**

1 - This course only support windows.
   1. If you are using MAC it is highly recommended you install a windows VM. You can use VMware which you can get for free from school, Parallel or VirtualBox.
   2. If you are using Linux I'll assume you don't need any help.

**MySQL**

1 - Installing MySQL server and workbench 8+ using MySQL Installer (https://dev.mysql.com/downloads/installer/)

2 - Download MySQL installer and install MySQL server 8+, Workbench 8+,

1 - If you are installing MySQL from scratch, during the installation choose custom.

      1. from server, choose MySQL server version 8 (newest).
      
      2. from application choose Workbench version 8 (newest).
      
      3. from connectors choose Connector/J version 8 (newest).

2 - During the installation if you get port already in use error for 3306, you have 2 options:
    
    1. (Recommended) Figure out what other application is using port 3306 and stop/remove it.
        
        1. Download TCPView from https://docs.microsoft.com/en-us/sysinternals/downloads/tcpview
        
        2. This application will show you what port is being used by what application.
        
        3. Sort by Local ports and find the port you are looking for.
        
        4. Then right click the application and end process or just uninstall it if you can. I do recommend uninstalling if you can as the application might start again and you have to do this again.
    
    2. You can simply use a different port like 3307.

3 - If you have version 5, you must uninstall it first.

4 - When installed run workbench click on your connection.

**JDK 11 LTS**

1- Read Java 11 module for instructions.
    
    1. Important -> Use the Adopt installer and during the installation click on the crosses that will pop in the 3rd window and select the first option. This way the installer will automatically set you environment variables as well.

2 - You need to download JDK 11.
     
     1. If you have another JDK 11 version installed already you don't need you change it.
     
     2. Edit you System Environment Variables and make JDK 11 the default.

3 - Open up CMD or powershell and type in "java -version". It should say jdk 11.
     1. If you've done the steps above and it is still an older jdk, make sure you setup your environment variables and have restarted your computer.

**IDE**

In this course we use eclipse enterprise. Intelij or other IDE's can also be used but it is up to you to make them work.

**Eclipse Latest**

1 - Eclipse Enterprise Edition (EE) (https://www.eclipse.org/downloads/)

2 - Adding JDK 11 to eclipse (https://simply-how.com/getting-started-with-java-11#section-3)

3 - As long as you have one of the new Eclipse which supports JDK 11 and Junit 5, you don't need to update your eclipse.
    1. However, updating eclipse is always good.

**Payara 5.x**

1 - Download and unzip Payara somewhere you won't change for duration of this course.

2 - Adding Payara to eclipse

3 - After creating a payara server in eclipse, make sure to a the ConnectorJ jar file to the the payara domain.
    
    1. Adding MySQL ConnectorJ to Payara

4 - If you are getting an error of missing files when starting your payara it means windows defender has deleted your files.

**Windows Defender Fix for possible false flag**


