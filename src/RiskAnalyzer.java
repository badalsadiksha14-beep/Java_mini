import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes routes for risk based on proximity to hazard zones.
 * Uses the Haversine formula to calculate great-circle distances
 * between geographic coordinates on a spherical Earth model.
 * 
 * Risk Analysis Algorithm:
 * 1. Calculate distance between each route point and each hazard zone center
 * 2. Determine if the point is inside or near the hazard zone
 * 3. Calculate risk score based on:
 *    - Number of intersections with hazard zones
 *    - Distance traveled inside hazard zones
 *    - Risk weight of each hazard zone
 * 4. Classify overall risk as LOW, MEDIUM, or HIGH
 */
public class RiskAnalyzer {
    // Earth's radius in kilometers (mean radius)
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    // Risk classification thresholds
    private static final double LOW_RISK_THRESHOLD = 50.0;
    private static final double HIGH_RISK_THRESHOLD = 200.0;
    
    /**
     * Calculate the great-circle distance between two coordinates using the Haversine formula.
     * 
     * The Haversine formula determines the shortest distance over the Earth's surface,
     * giving an "as-the-crow-flies" distance between the points (ignoring any hills).
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
     * Calculate the total distance of a route by summing distances between consecutive waypoints.
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
     * Check if a coordinate is inside a hazard zone.
     * @param coord The coordinate to check
     * @param zone The hazard zone
     * @return true if the coordinate is inside the zone
     */
    public static boolean isInsideHazardZone(Coordinate coord, HazardZone zone) {
        double distance = calculateDistance(coord, zone.getCenter());
        return distance <= zone.getRadiusKm();
    }
    
    /**
     * Analyze a route against multiple hazard zones and calculate risk.
     * @param route The route to analyze
     * @param hazardZones List of hazard zones to check against
     * @return RiskAnalysisResult containing the analysis details
     */
    public static RiskAnalysisResult analyzeRoute(Route route, List<HazardZone> hazardZones) {
        if (!route.isValid()) {
            throw new IllegalArgumentException("Route must have at least 2 waypoints");
        }
        
        List<Coordinate> waypoints = route.getWaypoints();
        double totalDistance = calculateRouteDistance(route);
        double riskScore = 0.0;
        int intersectionCount = 0;
        double distanceInHazardZones = 0.0;
        List<String> riskFactors = new ArrayList<>();
        
        // Track which zones have been intersected (to count unique intersections)
        boolean[] zoneIntersected = new boolean[hazardZones.size()];
        
        // Analyze each segment of the route
        for (int i = 0; i < waypoints.size(); i++) {
            Coordinate point = waypoints.get(i);
            
            // Check each hazard zone
            for (int j = 0; j < hazardZones.size(); j++) {
                HazardZone zone = hazardZones.get(j);
                double distanceToZone = calculateDistance(point, zone.getCenter());
                
                // Check if point is inside hazard zone
                if (distanceToZone <= zone.getRadiusKm()) {
                    // Mark this zone as intersected
                    if (!zoneIntersected[j]) {
                        zoneIntersected[j] = true;
                        intersectionCount++;
                        riskFactors.add(String.format("Route passes through '%s' (risk weight: %.1f)", 
                                                     zone.getName(), zone.getRiskWeight()));
                    }
                    
                    // Calculate distance inside the zone for this segment
                    if (i > 0) {
                        double segmentDistance = calculateDistance(waypoints.get(i - 1), point);
                        distanceInHazardZones += segmentDistance;
                    }
                    
                    // Add to risk score: closer to center = higher risk
                    double penetrationDepth = zone.getRadiusKm() - distanceToZone;
                    double penetrationRatio = penetrationDepth / zone.getRadiusKm();
                    riskScore += zone.getRiskWeight() * penetrationRatio * 10;
                }
                // Check if point is near the hazard zone (within 20% of radius outside)
                else if (distanceToZone <= zone.getRadiusKm() * 1.2) {
                    double proximity = (zone.getRadiusKm() * 1.2 - distanceToZone) / (zone.getRadiusKm() * 0.2);
                    riskScore += zone.getRiskWeight() * proximity * 2;
                    
                    if (!zoneIntersected[j]) {
                        riskFactors.add(String.format("Route passes near '%s' (within %.1f km)", 
                                                     zone.getName(), distanceToZone));
                    }
                }
            }
        }
        
        // Add risk for number of different zones intersected
        riskScore += intersectionCount * 20;
        
        // Determine risk level
        String riskLevel;
        if (riskScore < LOW_RISK_THRESHOLD) {
            riskLevel = "LOW";
        } else if (riskScore < HIGH_RISK_THRESHOLD) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "HIGH";
        }
        
        // Add default message if no risk factors found
        if (riskFactors.isEmpty()) {
            riskFactors.add("Route does not pass through or near any hazard zones");
        }
        
        return new RiskAnalysisResult(totalDistance, riskScore, riskLevel, 
                                     intersectionCount, distanceInHazardZones, riskFactors);
    }
    
    /**
     * Inner class to hold the results of a risk analysis.
     */
    public static class RiskAnalysisResult {
        private double totalDistance;
        private double riskScore;
        private String riskLevel;
        private int intersectionCount;
        private double distanceInHazardZones;
        private List<String> riskFactors;
        
        public RiskAnalysisResult(double totalDistance, double riskScore, String riskLevel,
                                 int intersectionCount, double distanceInHazardZones,
                                 List<String> riskFactors) {
            this.totalDistance = totalDistance;
            this.riskScore = riskScore;
            this.riskLevel = riskLevel;
            this.intersectionCount = intersectionCount;
            this.distanceInHazardZones = distanceInHazardZones;
            this.riskFactors = riskFactors;
        }
        
        public double getTotalDistance() { return totalDistance; }
        public double getRiskScore() { return riskScore; }
        public String getRiskLevel() { return riskLevel; }
        public int getIntersectionCount() { return intersectionCount; }
        public double getDistanceInHazardZones() { return distanceInHazardZones; }
        public List<String> getRiskFactors() { return riskFactors; }
        
        /**
         * Generate a formatted report of the analysis.
         * @return formatted string report
         */
        public String getReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== ROUTE RISK ANALYSIS REPORT ===\n\n");
            sb.append(String.format("Total Route Distance: %.2f km\n", totalDistance));
            sb.append(String.format("Risk Score: %.2f\n", riskScore));
            sb.append(String.format("Risk Level: %s\n\n", riskLevel));
            sb.append(String.format("Hazard Zone Intersections: %d\n", intersectionCount));
            sb.append(String.format("Distance Inside Hazard Zones: %.2f km\n\n", distanceInHazardZones));
            sb.append("Risk Factors:\n");
            for (String factor : riskFactors) {
                sb.append("  • ").append(factor).append("\n");
            }
            sb.append("\n");
            
            // Add interpretation
            sb.append("Interpretation:\n");
            if (riskLevel.equals("LOW")) {
                sb.append("  This route is relatively safe. It avoids major hazard zones\n");
                sb.append("  or only passes near them with minimal exposure.");
            } else if (riskLevel.equals("MEDIUM")) {
                sb.append("  This route has moderate risk. Consider alternative paths\n");
                sb.append("  or take appropriate safety precautions.");
            } else {
                sb.append("  This route is HIGH RISK! It passes through significant hazard\n");
                sb.append("  zones. Strongly consider an alternative route or take extensive\n");
                sb.append("  safety measures if this route must be used.");
            }
            
            return sb.toString();
        }
    }
}
