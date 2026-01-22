@echo off
REM Create executable JAR file for distribution

echo Creating executable JAR file...
echo.

REM Compile all files
echo Step 1: Compiling...
javac Coordinate.java HazardZone.java Route.java RiskAnalyzer.java MainGUI.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create manifest file
echo Step 2: Creating manifest...
echo Main-Class: MainGUI > manifest.txt

REM Create JAR file
echo Step 3: Creating JAR...
jar cvfm RouteRiskAnalyzer.jar manifest.txt *.class

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo SUCCESS!
    echo ========================================
    echo JAR file created: RouteRiskAnalyzer.jar
    echo.
    echo To run on any computer with Java:
    echo   java -jar RouteRiskAnalyzer.jar
    echo.
    echo Or just double-click RouteRiskAnalyzer.jar
    echo.
    
    REM Clean up
    del manifest.txt
) else (
    echo JAR creation failed!
)

pause
