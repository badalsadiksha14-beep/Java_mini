@echo off
REM Route Risk Analysis System - Windows Launch Script
REM This script compiles and runs the application

echo ========================================
echo Route Risk Analysis System
echo ========================================
echo.

echo Compiling Java files...
javac Coordinate.java HazardZone.java Route.java RiskAnalyzer.java MainGUI.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo Launching application...
    java MainGUI
) else (
    echo.
    echo ERROR: Compilation failed!
    echo Please make sure Java JDK is installed and in your PATH.
    echo.
    pause
)
