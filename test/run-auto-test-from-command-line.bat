@echo off
echo   ********************************
echo   * SAIKU WEB UI AUTOMATION TEST *
echo   *           USING              *
echo   *      FOODMART DATABASE       *
echo   ********************************
REM create variables that stores the project folder path and Selenium Jar path. This variables will used in the subsequent statements.
set javaTestProjectPath=C:\Users\admin\Test\Saiku-UI-Tests-Source-Files
set javaSeleniumJarPath=C:\Users\admin\Test\selenium-2.32.0-Jar

REM move to the project folder
c:
cd %javaTestProjectPath%

REM set path to dir that contains javac.exe and java.exe
set path=C:\Program Files\Java\jdk1.7.0_25\bin

REM set the classpath, this tells java where to look for the library files, and the project bin folder is added as it will store the .class file after compile
set classpath=%javaSeleniumJarPath%\*;%javaSeleniumJarPath%\libs\*;%javaTestProjectPath%\bin

REM compile the dataProviderExample.java file, the -d parameter tells javac where to put the .class file that is created on compile
javac -verbose %javaTestProjectPath%\src\login\* %javaTestProjectPath%\src\main\* %javaTestProjectPath%\src\query\*  -d %javaTestProjectPath%\bin

REM execute testng framework by giving the path of the testng.xml file as a parameter. The xml file tells testng what test to run
java org.testng.TestNG %javaTestProjectPath%\testng.xml

