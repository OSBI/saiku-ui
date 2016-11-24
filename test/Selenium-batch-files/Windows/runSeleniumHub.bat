@ECHO OFF
ECHO *******************************
ECHO * WINDOWS SELENIUM SERVER HUB *
ECHO *******************************
ECHO.

c:
cd %CD%
ECHO Starting hub
call java -jar selenium-server-standalone.jar -role hub
ECHO.
PAUSE
