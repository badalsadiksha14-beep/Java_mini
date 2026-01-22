# Route Risk Analysis System - README

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- Windows, macOS, or Linux

## How to Run

### Option 1: Using the Launch Script (Easiest)

**On Windows:**

1. Double-click `run.bat`
   OR open Command Prompt and run: `run.bat`

**On Linux/Mac:**

1. Open Terminal
2. Make script executable: `chmod +x run.sh`
3. Run: `./run.sh`

### Option 2: Using JAR File

If a JAR file is included:

1. Double-click `RouteRiskAnalyzer.jar`
   OR from command line: `java -jar RouteRiskAnalyzer.jar`

### Option 3: Manual Compilation

```bash
# Compile all files
javac Coordinate.java HazardZone.java Route.java RiskAnalyzer.java MainGUI.java

# Run the application
java MainGUI
```

## Quick Start Guide

1. **Launch the application** using any method above
2. **Click "Load Sample Data"** to see an example with earthquake zones
3. **Click "Analyze Route Risk"** to view the risk analysis
4. **Or enter your own data:**
   - Route format: `latitude,longitude` (one per line)
   - Hazard zone format: `name,lat,lon,radius_km,risk_weight` (one per line)

## File List

### Required Java Files:

- `Coordinate.java` - Geographic coordinate representation
- `HazardZone.java` - Hazard zone definition
- `Route.java` - Route waypoint management
- `RiskAnalyzer.java` - Risk calculation engine
- `MainGUI.java` - Main application interface

### Helper Scripts:

- `run.bat` - Windows launch script
- `run.sh` - Linux/Mac launch script
- `create-jar.bat` - Create standalone JAR file

## Troubleshooting

**"Java not found" error:**

- Install Java JDK from: https://www.oracle.com/java/technologies/downloads/
- Or OpenJDK from: https://adoptium.net/

**"Class not found" error:**

- Make sure all 5 .java files are in the same directory
- Recompile using the launch script or manual commands

**GUI doesn't appear:**

- Make sure you're running `MainGUI`, not `Main`
- Check that Java GUI libraries are available (usually included with standard JDK)

## Project Information

**Course:** University Geospatial Project
**Purpose:** Route Risk Analysis for hazard zones
**Technology:** Java Swing (no external dependencies)
**Algorithm:** Haversine formula for distance calculations

## Contact & Support

For questions or issues with this application, refer to the source code comments
which contain detailed explanations of the geospatial algorithms used.
