# Route Risk Analysis System - README

## System Requirements

- Java Runtime Environment (JRE) 8 or higher
- Windows, macOS, or Linux

## Overview

This system provides **AUTOMATIC risk calculation** for travel routes based purely on geospatial proximity to hazard zones. No manual risk weights or severity ratings are needed - the system calculates everything automatically from location data.

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
3. **Click "Analyze Route Risk"** to view the automatic risk analysis
4. **Or enter your own data:**
   - Route format: `latitude,longitude` (one per line)
   - Hazard zone format: `name,lat,lon,radius_km` (one per line)
   - **No risk weights needed** - calculated automatically!

## Automatic Risk Calculation Algorithm

### How the System Computes Risk (No Manual Input Required)

The system uses a **segment-based automatic analysis** that computes risk purely from geospatial factors:

#### Step 1: **Route Segmentation**

The route is divided into consecutive segments between each pair of waypoints.

#### Step 2: **Segment Analysis**

For each segment:

1. **Calculate Segment Length** using the Haversine formula:

   ```
   segment_length = haversineDistance(point_i, point_i+1)
   ```

2. **Calculate Segment Midpoint**:

   ```
   midpoint_lat = (lat_1 + lat_2) / 2
   midpoint_lon = (lon_1 + lon_2) / 2
   ```

3. **Measure Distance from Midpoint to Each Hazard Zone Center**:

   ```
   distance_to_zone = haversineDistance(segment_midpoint, zone_center)
   ```

4. **Calculate Proximity Factor**:

   ```
   If distance_to_zone > zone_radius:
       proximity = 0 (no risk - outside zone)

   If distance_to_zone ≤ zone_radius:
       proximity = 1 - (distance_to_zone / zone_radius)

   At center (distance = 0): proximity = 1.0 (maximum risk)
   At edge (distance = radius): proximity = 0.0 (minimal risk)
   ```

5. **Calculate Segment Risk**:

   ```
   segment_risk = segment_length × proximity
   ```

6. **Handle Multiple Zones**: If a segment is affected by multiple hazard zones, sum all their risks:
   ```
   total_segment_risk = Σ (segment_length × proximity_i) for all zones
   ```

#### Step 3: **Total Risk Calculation**

Sum all segment risks:

```
total_risk = Σ segment_risk for all segments
```

#### Step 4: **Normalization (0-100 Scale)**

Normalize the risk score to a 0-100 scale:

```
normalized_risk_score = (total_risk / total_route_length) × 100
```

This formula ensures:

- Short routes through hazards get appropriate scores
- Long routes with limited hazard exposure don't get artificially high scores
- Score is capped at 100 for display

#### Step 5: **Risk Level Classification**

| Risk Score | Risk Level | Interpretation                                         |
| ---------- | ---------- | ------------------------------------------------------ |
| 0 - 30     | **LOW**    | Route is safe - minimal hazard exposure                |
| 31 - 60    | **MEDIUM** | Moderate risk - consider alternatives or precautions   |
| 61 - 100   | **HIGH**   | Significant risk - strongly consider alternative route |

### Complete Example: Automatic Calculation

**Scenario:** 3-point route through 1 hazard zone

- **Route:**
  - Point A: (34.00, -118.00)
  - Point B: (34.05, -118.05)
  - Point C: (34.10, -118.10)

- **Hazard Zone:**
  - Name: Earthquake Zone
  - Center: (34.05, -118.05)
  - Radius: 10 km

**Calculations:**

**Segment 1: A → B**

- Length: 7.85 km (Haversine)
- Midpoint: (34.025, -118.025)
- Distance to zone center: 3.5 km
- Proximity: 1 - (3.5 / 10) = **0.65**
- Segment risk: 7.85 × 0.65 = **5.10**

**Segment 2: B → C**

- Length: 7.85 km
- Midpoint: (34.075, -118.075)
- Distance to zone center: 3.5 km
- Proximity: 1 - (3.5 / 10) = **0.65**
- Segment risk: 7.85 × 0.65 = **5.10**

**Total Risk:** 5.10 + 5.10 = **10.20**  
**Total Route Distance:** 7.85 + 7.85 = **15.70 km**  
**Normalized Risk Score:** (10.20 / 15.70) × 100 = **64.97**  
**Risk Level:** **HIGH** (score > 60)

### Key Advantages of Automatic Calculation

✓ **No Subjective Input** - Risk weights not needed  
✓ **Pure Geospatial Analysis** - Based only on location and proximity  
✓ **Consistent Results** - Same inputs always produce same outputs  
✓ **Easy to Use** - Just input locations, not severity ratings  
✓ **Academically Sound** - Transparent, reproducible methodology

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
**Purpose:** Automatic Route Risk Analysis for hazard zones  
**Technology:** Java Swing (no external dependencies)  
**Algorithm:** Haversine formula + Segment-based proximity analysis  
**Risk Calculation:** Fully automatic - no manual weights required

## Contact & Support

For questions or issues with this application, refer to the source code comments
which contain detailed explanations of the automatic geospatial risk algorithms used.
