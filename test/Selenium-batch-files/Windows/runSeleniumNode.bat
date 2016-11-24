@ECHO OFF
ECHO *********************************
ECHO * WINDOWS SELENIUM SERVER NODE  *
ECHO *********************************
ECHO.
ECHO Starting node
cd %CD%
call java ^
 -Dwebdriver.chrome.driver=chromedriver.exe ^
 -Dwebdriver.ie.driver=IEDriverServer.exe  ^
 -jar selenium-server-standalone.jar ^
 -role node -hub http://192.168.128.73:4444/grid/register ^
 -browser "browserName=internet explorer,version=9,platform=WINDOWS" ^
 -browser "browserName=chrome,platform=WINDOWS" ^
 -browser "browserName=firefox,platform=WINDOWS"
ECHO.
PAUSE
