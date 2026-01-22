# Route Risk Analysis System - Mathematical Formulas Reference

## Overview

This document provides a comprehensive mathematical reference for all formulas used in the automatic Route Risk Analysis System. The system uses purely geospatial calculations to determine route risk without requiring manual risk weight inputs.

---

## 1. Haversine Distance Formula

**Purpose:** Calculate the great-circle distance between two geographic coordinates on Earth's surface.

**Formula:**

```
a = sin²(Δlat/2) + cos(lat₁) × cos(lat₂) × sin²(Δlon/2)
c = 2 × atan2(√a, √(1−a))
distance = R × c
```

**Where:**

- `lat₁, lon₁` = Latitude and longitude of first point (in radians)
- `lat₂, lon₂` = Latitude and longitude of second point (in radians)
- `Δlat = lat₂ - lat₁` = Difference in latitude
- `Δlon = lon₂ - lon₁` = Difference in longitude
- `R = 6371 km` = Earth's mean radius
- `distance` = Great-circle distance in kilometers

**Conversion to Radians:**

```
radians = degrees × (π / 180)
```

**Example Calculation:**

Point A: (27.7172°N, 85.3240°E) - Kathmandu  
Point B: (27.6000°N, 84.5000°E) - Mugling

Step 1: Convert to radians

```
lat₁ = 27.7172 × (π/180) = 0.48389 rad
lon₁ = 85.3240 × (π/180) = 1.48903 rad
lat₂ = 27.6000 × (π/180) = 0.48185 rad
lon₂ = 84.5000 × (π/180) = 1.47465 rad
```

Step 2: Calculate differences

```
Δlat = 0.48185 - 0.48389 = -0.00204 rad
Δlon = 1.47465 - 1.48903 = -0.01438 rad
```

Step 3: Apply Haversine formula

```
a = sin²(-0.00204/2) + cos(0.48389) × cos(0.48185) × sin²(-0.01438/2)
a = 0.0000010404 + 0.8834 × 0.8834 × 0.00005181
a = 0.0000010404 + 0.00004041
a = 0.00004145

c = 2 × atan2(√0.00004145, √0.99995855)
c = 2 × atan2(0.00644, 0.99998)
c = 2 × 0.00644
c = 0.01288 rad

distance = 6371 × 0.01288 = 82.06 km
```

**Result:** Distance from Kathmandu to Mugling ≈ 82 km

---

## 2. Segment Midpoint Calculation

**Purpose:** Find the geographic center point of a route segment.

**Formula:**

```
midpoint_latitude = (lat₁ + lat₂) / 2
midpoint_longitude = (lon₁ + lon₂) / 2
```

**Where:**

- `lat₁, lon₁` = Start point of segment (in degrees)
- `lat₂, lon₂` = End point of segment (in degrees)

**Note:** This is a simple averaging approach suitable for small to medium distances. For large distances spanning significant portions of Earth's surface, more sophisticated methods (e.g., spherical midpoint) should be used.

**Example:**

Segment: Kathmandu (27.7172°N, 85.3240°E) → Mugling (27.6000°N, 84.5000°E)

```
midpoint_lat = (27.7172 + 27.6000) / 2 = 27.6586°N
midpoint_lon = (85.3240 + 84.5000) / 2 = 84.9120°E
```

**Result:** Segment midpoint at (27.6586°N, 84.9120°E)

---

## 3. Proximity Factor Calculation

**Purpose:** Determine how close a point is to the center of a hazard zone, normalized to a 0-1 scale.

**Formula:**

```
If distance_to_zone_center > zone_radius:
    proximity = 0  (outside hazard zone, no risk)

If distance_to_zone_center ≤ zone_radius:
    proximity = 1 - (distance_to_zone_center / zone_radius)
```

**Where:**

- `distance_to_zone_center` = Haversine distance from point to hazard zone center (km)
- `zone_radius` = Radius of hazard zone influence (km)
- `proximity` = Risk factor from 0.0 (no risk) to 1.0 (maximum risk)

**Proximity Interpretation:**

- `proximity = 1.0` → Point at zone center (maximum risk)
- `proximity = 0.5` → Point halfway between center and edge
- `proximity = 0.0` → Point at zone edge or beyond (no risk)

**Example 1:** Point inside hazard zone

Zone: Main Himalayan Thrust (center: 27.8°N, 85.5°E, radius: 120 km)  
Point: Kathmandu midpoint (27.6586°N, 84.9120°E)  
Distance to center: 56.2 km (calculated via Haversine)

```
proximity = 1 - (56.2 / 120)
proximity = 1 - 0.468
proximity = 0.532
```

**Result:** Proximity factor = 0.532 (53.2% risk intensity)

**Example 2:** Point outside hazard zone

Zone: Bardiya Zone (center: 28.5°N, 82.0°E, radius: 80 km)  
Point: Kathmandu (27.7172°N, 85.3240°E)  
Distance to center: 350 km (calculated via Haversine)

```
Since 350 > 80:
    proximity = 0
```

**Result:** Proximity factor = 0 (no risk)

---

## 4. Segment Risk Calculation

**Purpose:** Calculate the risk contribution of a single route segment based on its exposure to hazard zones.

**Formula for Single Hazard Zone:**

```
segment_risk = segment_length × proximity
```

**Formula for Multiple Hazard Zones:**

```
segment_risk = Σ(segment_length × proximity_i) for all zones i
```

**Where:**

- `segment_length` = Length of route segment in kilometers
- `proximity` = Proximity factor to hazard zone (0.0 to 1.0)
- `segment_risk` = Risk contribution from this segment (km-based units)

**Rationale:** Longer segments through high-proximity areas contribute more risk than shorter segments or segments with low proximity.

**Example 1:** Segment through one hazard zone

Segment: Kathmandu → Mugling  
Length: 82.06 km  
Proximity to Main Himalayan Thrust: 0.532

```
segment_risk = 82.06 × 0.532
segment_risk = 43.66 km
```

**Example 2:** Segment affected by multiple hazard zones

Segment: Mugling → Chitwan  
Length: 65.0 km  
Proximity to Mahabharat Landslide Zone: 0.68  
Proximity to Rapti Flood Plain: 0.42

```
segment_risk = (65.0 × 0.68) + (65.0 × 0.42)
segment_risk = 44.2 + 27.3
segment_risk = 71.5 km
```

**Result:** This segment has higher risk due to exposure to two hazard zones.

---

## 5. Total Route Risk Calculation

**Purpose:** Sum all segment risks to get the total cumulative risk for the entire route.

**Formula:**

```
total_risk = Σ(segment_risk_i) for all segments i
```

**Where:**

- `segment_risk_i` = Risk contribution from segment i
- `total_risk` = Sum of all segment risks (km-based units)

**Example:** Route with 6 segments

```
Segment 1 risk: 43.66 km
Segment 2 risk: 71.50 km
Segment 3 risk: 28.30 km
Segment 4 risk: 15.80 km
Segment 5 risk: 32.10 km
Segment 6 risk: 19.45 km

total_risk = 43.66 + 71.50 + 28.30 + 15.80 + 32.10 + 19.45
total_risk = 210.81 km
```

---

## 6. Normalized Risk Score (0-100 Scale)

**Purpose:** Convert the raw risk value into a normalized 0-100 score that accounts for route length.

**Formula:**

```
normalized_risk_score = (total_risk / total_route_length) × 100

If normalized_risk_score > 100:
    normalized_risk_score = 100  (capped at maximum)
```

**Where:**

- `total_risk` = Sum of all segment risks (km)
- `total_route_length` = Total length of route (km)
- `normalized_risk_score` = Final risk score from 0 to 100

**Rationale:**

- Normalization ensures that longer routes don't automatically get higher scores
- A route completely within a hazard zone at its center would score ~100
- A route completely outside hazard zones would score 0
- The score represents the average risk intensity along the route

**Example:**

Route from Kathmandu to Bardiya:

- Total route length: 450.0 km
- Total risk: 210.81 km

```
normalized_risk_score = (210.81 / 450.0) × 100
normalized_risk_score = 0.4685 × 100
normalized_risk_score = 46.85
```

**Result:** Risk score = 46.85 / 100

---

## 7. Risk Level Classification

**Purpose:** Categorize the normalized risk score into interpretable risk levels.

**Classification Rules:**

```
If normalized_risk_score ≤ 30:
    risk_level = "LOW"

If 30 < normalized_risk_score ≤ 60:
    risk_level = "MEDIUM"

If normalized_risk_score > 60:
    risk_level = "HIGH"
```

**Risk Level Thresholds:**

| Score Range | Risk Level | Interpretation                          |
| ----------- | ---------- | --------------------------------------- |
| 0 - 30      | **LOW**    | Safe route with minimal hazard exposure |
| 31 - 60     | **MEDIUM** | Moderate risk - consider precautions    |
| 61 - 100    | **HIGH**   | Significant risk - avoid if possible    |

**Example:**

Score: 46.85

```
Since 30 < 46.85 ≤ 60:
    risk_level = "MEDIUM"
```

**Result:** Route classified as MEDIUM risk

---

## 8. Complete Calculation Example

### Scenario: Simple Route Through One Hazard Zone

**Route:**

- Point A: (27.70°N, 85.00°E)
- Point B: (27.80°N, 85.10°E)
- Point C: (27.90°N, 85.20°E)

**Hazard Zone:**

- Name: Earthquake Zone
- Center: (27.80°N, 85.10°E)
- Radius: 20 km

### Step-by-Step Calculation:

#### **Segment 1: A → B**

1. Calculate segment length (Haversine):

   ```
   length₁ = haversine_distance((27.70, 85.00), (27.80, 85.10))
   length₁ ≈ 15.4 km
   ```

2. Calculate midpoint:

   ```
   midpoint₁ = ((27.70 + 27.80)/2, (85.00 + 85.10)/2)
   midpoint₁ = (27.75, 85.05)
   ```

3. Calculate distance from midpoint to zone center:

   ```
   dist₁ = haversine_distance((27.75, 85.05), (27.80, 85.10))
   dist₁ ≈ 7.7 km
   ```

4. Calculate proximity:

   ```
   proximity₁ = 1 - (7.7 / 20)
   proximity₁ = 1 - 0.385
   proximity₁ = 0.615
   ```

5. Calculate segment risk:
   ```
   risk₁ = 15.4 × 0.615
   risk₁ = 9.47 km
   ```

#### **Segment 2: B → C**

1. Calculate segment length:

   ```
   length₂ = haversine_distance((27.80, 85.10), (27.90, 85.20))
   length₂ ≈ 15.4 km
   ```

2. Calculate midpoint:

   ```
   midpoint₂ = (27.85, 85.15)
   ```

3. Calculate distance from midpoint to zone center:

   ```
   dist₂ = haversine_distance((27.85, 85.15), (27.80, 85.10))
   dist₂ ≈ 7.7 km
   ```

4. Calculate proximity:

   ```
   proximity₂ = 1 - (7.7 / 20) = 0.615
   ```

5. Calculate segment risk:
   ```
   risk₂ = 15.4 × 0.615 = 9.47 km
   ```

#### **Total Route Analysis**

1. Total route length:

   ```
   total_length = 15.4 + 15.4 = 30.8 km
   ```

2. Total risk:

   ```
   total_risk = 9.47 + 9.47 = 18.94 km
   ```

3. Normalized risk score:

   ```
   score = (18.94 / 30.8) × 100
   score = 0.615 × 100
   score = 61.5
   ```

4. Risk classification:
   ```
   Since 61.5 > 60:
       risk_level = "HIGH"
   ```

### **Final Result:**

- **Route Distance:** 30.8 km
- **Risk Score:** 61.5 / 100
- **Risk Level:** HIGH
- **Interpretation:** Route passes directly through earthquake zone with significant exposure

---

## 9. Formula Constants

**Earth Model Constants:**

```
EARTH_RADIUS_KM = 6371.0 km  (mean radius)
PI = 3.14159265358979323846
DEGREES_TO_RADIANS = PI / 180 = 0.017453292519943295
```

**Risk Classification Thresholds:**

```
LOW_RISK_THRESHOLD = 30.0
HIGH_RISK_THRESHOLD = 60.0
```

**Derived Ranges:**

- LOW: [0, 30]
- MEDIUM: (30, 60]
- HIGH: (60, 100]

---

## 10. Implementation Notes

### **Coordinate Validation:**

- Latitude must be in range [-90°, +90°]
- Longitude must be in range [-180°, +180°]

### **Edge Cases:**

1. **Zero-length segments:**

   ```
   If segment_length = 0:
       segment_risk = 0 (no distance traveled, no risk)
   ```

2. **Points exactly at zone center:**

   ```
   If distance_to_center = 0:
       proximity = 1.0 (maximum risk)
   ```

3. **Multiple zones overlapping:**

   ```
   Each zone contributes independently to segment risk
   Total = sum of all individual zone contributions
   ```

4. **Route entirely outside hazard zones:**
   ```
   All proximity values = 0
   Total risk = 0
   Normalized score = 0
   Risk level = LOW
   ```

### **Precision Considerations:**

- All calculations use double-precision floating-point (64-bit)
- Haversine formula is accurate to ~0.5% for most Earth distances
- For very short distances (<1 km), simple Euclidean approximation may suffice
- For very long distances (>1000 km), spherical excess should be considered

---

## 11. Advantages of This Approach

✓ **Objective:** No subjective risk weight assignments needed  
✓ **Reproducible:** Same inputs always produce same outputs  
✓ **Transparent:** All calculations are documented and verifiable  
✓ **Scalable:** Works for any route length or number of hazard zones  
✓ **Normalized:** Fair comparison between routes of different lengths  
✓ **Intuitive:** 0-100 scale is easy to understand  
✓ **Geospatially Sound:** Based on actual distances and positions

---

## 12. References

**Mathematical Foundations:**

- Haversine Formula: R.W. Sinnott, "Virtues of the Haversine", Sky and Telescope, vol. 68, no. 2, 1984, p. 159
- Spherical Geometry: Vincenty, T. (1975), "Direct and Inverse Solutions of Geodesics on the Ellipsoid"

**Implementation:**

- Java Math Library: `Math.sin()`, `Math.cos()`, `Math.atan2()`, `Math.sqrt()`, `Math.toRadians()`
- Coordinate System: WGS84 (World Geodetic System 1984)

---

**Document Version:** 1.0  
**Last Updated:** January 23, 2026  
**Project:** Route Risk Analysis System  
**Course:** University Geospatial Project
