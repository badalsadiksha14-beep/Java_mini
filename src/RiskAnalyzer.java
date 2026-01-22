import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes routes for risk based on proximity to hazard zones.
 * Uses the Haversine formula to calculate great-circle distances
 * between geographic coordinates on a spherical Earth model.
 * 
 * AUTOMATIC RISK CALCULATION ALGORITHM:
 * 1. Break the route into consecutive segments between waypoints
 * 2. For each segment:
 * a. Calculate segment length using Haversine formula
 * b. Calculate segment midpoint
 * c. Measure distance from midpoint to each hazard zone center
 * d. If distance ≤ radius: proximity = 1 - (distance / radius)
 * e. Calculate segment risk: segmentLength × proximity
 * 3. Sum all segment risks to get total risk
 * 4. Normalize: (totalRisk / totalRouteLength) × 100
 * 5. Classify: 0-30 = LOW, 31-60 = MEDIUM, 61-100 = HIGH
 * 
 * NO MANUAL RISK WEIGHTS REQUIRED - All calculations are purely geospatial.
 */
public class RiskAnalyzer {
    // Earth's radius in kilometers (mean radius)
    private static final double EARTH_RADIUS_KM = 6371.0;

    // Automatic risk classification thresholds (normalized 0-100 scale)
    private static final double LOW_RISK_THRESHOLD = 30.0;
    private static final double HIGH_RISK_THRESHOLD = 60.0;

    /**
     * Calculate the great-circle distance between two coordinates using the
     * Haversine formula.
     * 
     * The Haversine formula determines the shortest distance over the Earth's
     * surface,
     * giving an "as-the-crow-flies" distance between the points (ignoring any
     * hills).
     * 
     * Formula:
     * a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
     * c = 2 * atan2(√a, √(1−a))
     * distance = R * c
     * 
     * Where R is Earth's radius, Δlat is the difference in latitude,
     * and Δlon is the difference in longitude.
     * 
     * @param coord1 First coordinate
     * @param coord2 Second coordinate
     * @return Distance in kilometers
     */
    public static double calculateDistance(Coordinate coord1, Coordinate coord2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(coord1.getLatitude());
        double lat2Rad = Math.toRadians(coord2.getLatitude());
        double lon1Rad = Math.toRadians(coord1.getLongitude());
        double lon2Rad = Math.toRadians(coord2.getLongitude());

        // Calculate differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate the total distance of a route by summing distances between
     * consecutive waypoints.
     * 
     * @param route The route to measure
     * @return Total distance in kilometers
     */
    public static double calculateRouteDistance(Route route) {
        List<Coordinate> waypoints = route.getWaypoints();
        double totalDistance = 0.0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            totalDistance += calculateDistance(waypoints.get(i), waypoints.get(i + 1));
        }

        return totalDistance;
    }

    /**
     * Calculate the midpoint coordinate between two points.
     * Uses simple averaging for small distances (acceptable approximation).
     * 
     * @param coord1 First coordinate
     * @param coord2 Second coordinate
     * @return Midpoint coordinate
     */
    public static Coordinate calculateMidpoint(Coordinate coord1, Coordinate coord2) {
        double midLat = (coord1.getLatitude() + coord2.getLatitude()) / 2.0;
        double midLon = (coord1.getLongitude() + coord2.getLongitude()) / 2.0;
        return new Coordinate(midLat, midLon);
    }

    /**
     * Calculate the proximity factor for a point relative to a hazard zone.
     * 
     * PROXIMITY FORMULA:
     * - If distance > radius: proximity = 0 (no risk)
     * - If distance ≤ radius: proximity = 1 - (distance / radius)
     * - At center (distance = 0): proximity = 1.0 (maximum risk)
     * - At edge (distance = radius): proximity = 0.0 (minimal risk)
     * 
     * @param point The coordinate to check
     * @param zone  The hazard zone
     * @return Proximity factor (0.0 to 1.0)
     */
    public static double calculateProximity(Coordinate point, HazardZone zone) {
        double distance = calculateDistance(point, zone.getCenter());

        if (distance > zone.getRadiusKm()) {
            return 0.0; // Outside hazard zone - no risk
        }

        // Inside hazard zone - calculate proximity (1.0 at center, 0.0 at edge)
        return 1.0 - (distance / zone.getRadiusKm());
    }

    /**
     * Analyze a route against multiple hazard zones using automatic risk
     * calculation.
     * 
     * AUTOMATIC SEGMENT-BASED ANALYSIS:
     * 1. Divide route into segments between consecutive waypoints
     * 2. For each segment:
     * - Calculate segment length
     * - Find segment midpoint
     * - Calculate proximity to each hazard zone
     * - Segment risk = segment length × proximity
     * 3. Total risk = sum of all segment risks
     * 4. Normalized score = (total risk / total route length) × 100
     * 
     * @param route       The route to analyze
     * @param hazardZones List of hazard zones to check against
     * @return RiskAnalysisResult containing the analysis details
     */
    public static RiskAnalysisResult analyzeRoute(Route route, List<HazardZone> hazardZones) {
        if (!route.isValid()) {
            throw new IllegalArgumentException("Route must have at least 2 waypoints");
        }

        List<Coordinate> waypoints = route.getWaypoints();
        double totalRouteDistance = calculateRouteDistance(route);
        double totalRisk = 0.0;
        int affectedSegments = 0;
        List<String> riskFactors = new ArrayList<>();

        // Track which zones affect the route
        boolean[] zoneAffected = new boolean[hazardZones.size()];

        // Analyze each segment of the route
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Coordinate start = waypoints.get(i);
            Coordinate end = waypoints.get(i + 1);

            // Calculate segment properties
            double segmentLength = calculateDistance(start, end);
            Coordinate segmentMidpoint = calculateMidpoint(start, end);

            double segmentRisk = 0.0;
            boolean segmentAffected = false;
            List<String> segmentHazards = new ArrayList<>();

            // Check each hazard zone's effect on this segment
            for (int j = 0; j < hazardZones.size(); j++) {
                HazardZone zone = hazardZones.get(j);
                double proximity = calculateProximity(segmentMidpoint, zone);

                if (proximity > 0) {
                    // This segment is affected by this hazard zone
                    double zoneSegmentRisk = segmentLength * proximity;
                    segmentRisk += zoneSegmentRisk;
                    segmentAffected = true;
                    zoneAffected[j] = true;

                    segmentHazards.add(String.format("%s (proximity: %.2f)",
                            zone.getName(), proximity));
                }
            }

            if (segmentAffected) {
                affectedSegments++;
                riskFactors.add(String.format("Segment %d→%d (%.2f km): Affected by %s",
                        i + 1, i + 2, segmentLength,
                        String.join(", ", segmentHazards)));
            }

            totalRisk += segmentRisk;
        }

        // Normalize risk score to 0-100 scale
        // Formula: (totalRisk / totalRouteLength) × 100
        double normalizedRiskScore = totalRouteDistance > 0
                ? (totalRisk / totalRouteDistance) * 100.0
                : 0.0;

        // Cap at 100 for display purposes
        normalizedRiskScore = Math.min(normalizedRiskScore, 100.0);

        // Determine risk level based on normalized score
        String riskLevel;
        if (normalizedRiskScore <= LOW_RISK_THRESHOLD) {
            riskLevel = "LOW";
        } else if (normalizedRiskScore <= HIGH_RISK_THRESHOLD) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "HIGH";
        }

        // Count how many zones affected the route
        int zonesAffecting = 0;
        for (boolean affected : zoneAffected) {
            if (affected)
                zonesAffecting++;
        }

        // Add summary if no risk factors found
        if (riskFactors.isEmpty()) {
            riskFactors.add("Route does not pass through any hazard zones");
        }

        return new RiskAnalysisResult(totalRouteDistance, normalizedRiskScore, riskLevel,
                affectedSegments, zonesAffecting, riskFactors);
    }

    /**
     * Inner class to hold the results of an automatic risk analysis.
     * Risk score is normalized to 0-100 scale based on geospatial factors only.
     */
    public static class RiskAnalysisResult {
        private double totalDistance;
        private double riskScore; // Normalized 0-100
        private String riskLevel;
        private int affectedSegments;
        private int zonesAffecting;
        private List<String> riskFactors;

        public RiskAnalysisResult(double totalDistance, double riskScore, String riskLevel,
                int affectedSegments, int zonesAffecting,
                List<String> riskFactors) {
            this.totalDistance = totalDistance;
            this.riskScore = riskScore;
            this.riskLevel = riskLevel;
            this.affectedSegments = affectedSegments;
            this.zonesAffecting = zonesAffecting;
            this.riskFactors = riskFactors;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public double getRiskScore() {
            return riskScore;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public int getAffectedSegments() {
            return affectedSegments;
        }

        public int getZonesAffecting() {
            return zonesAffecting;
        }

        public List<String> getRiskFactors() {
            return riskFactors;
        }

        /**
         * Generate a formatted report of the automatic risk analysis.
         * 
         * @return formatted string report
         */
        public String getReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== AUTOMATIC ROUTE RISK ANALYSIS REPORT ===\n\n");
            sb.append(String.format("Total Route Distance: %.2f km\n", totalDistance));
            sb.append(String.format("Computed Risk Score: %.2f / 100\n", riskScore));
            sb.append(String.format("Risk Level: %s\n\n", riskLevel));
            sb.append(String.format("Route Segments Affected: %d\n", affectedSegments));
            sb.append(String.format("Hazard Zones Affecting Route: %d\n\n", zonesAffecting));
            sb.append("Affected Segments:\n");
            for (String factor : riskFactors) {
                sb.append("  • ").append(factor).append("\n");
            }
            sb.append("\n");

            // Add interpretation based on automatic calculation
            sb.append("Risk Assessment:\n");
            if (riskLevel.equals("LOW")) {
                sb.append("  ✓ This route is SAFE (Risk Score: 0-30)\n");
                sb.append("  The route avoids hazard zones or has minimal exposure.\n");
                sb.append("  Normal travel precautions are sufficient.");
            } else if (riskLevel.equals("MEDIUM")) {
                sb.append("  ⚠ This route has MODERATE RISK (Risk Score: 31-60)\n");
                sb.append("  The route passes through or near hazard zones.\n");
                sb.append("  Consider alternative paths or take safety precautions.");
            } else {
                sb.append("  ⛔ This route is HIGH RISK (Risk Score: 61-100)\n");
                sb.append("  The route has significant exposure to hazard zones.\n");
                sb.append("  Strongly consider an alternative route or implement\n");
                sb.append("  comprehensive safety measures if this route is necessary.");
            }

            sb.append("\n");
            sb.append("Calculation Method:\n");
            sb.append("  Risk computed automatically from geospatial proximity.\n");
            sb.append("  Formula: (Σ segment_length × proximity) / total_distance × 100\n");
            sb.append("  Proximity = 1 - (distance_to_zone / zone_radius) when inside zone\n");

            return sb.toString();
        }
    }
}
