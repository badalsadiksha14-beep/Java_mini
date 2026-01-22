import java.util.ArrayList;
import java.util.List;

public class RiskAnalyzer {
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double LOW_RISK_THRESHOLD = 30.0;
    private static final double HIGH_RISK_THRESHOLD = 60.0;

    public static double calculateDistance(Coordinate coord1, Coordinate coord2) {
        double lat1Rad = Math.toRadians(coord1.getLatitude());
        double lat2Rad = Math.toRadians(coord2.getLatitude());
        double lon1Rad = Math.toRadians(coord1.getLongitude());
        double lon2Rad = Math.toRadians(coord2.getLongitude());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public static double calculateRouteDistance(Route route) {
        List<Coordinate> waypoints = route.getWaypoints();
        double totalDistance = 0.0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            totalDistance += calculateDistance(waypoints.get(i), waypoints.get(i + 1));
        }

        return totalDistance;
    }

    public static Coordinate calculateMidpoint(Coordinate coord1, Coordinate coord2) {
        double midLat = (coord1.getLatitude() + coord2.getLatitude()) / 2.0;
        double midLon = (coord1.getLongitude() + coord2.getLongitude()) / 2.0;
        return new Coordinate(midLat, midLon);
    }

    public static double calculateProximity(Coordinate point, HazardZone zone) {
        double distance = calculateDistance(point, zone.getCenter());

        if (distance > zone.getRadiusKm()) {
            return 0.0;
        }

        return 1.0 - (distance / zone.getRadiusKm());
    }

    public static RiskAnalysisResult analyzeRoute(Route route, List<HazardZone> hazardZones) {
        if (!route.isValid()) {
            throw new IllegalArgumentException("Route must have at least 2 waypoints");
        }

        List<Coordinate> waypoints = route.getWaypoints();
        double totalRouteDistance = calculateRouteDistance(route);
        double totalRisk = 0.0;
        int affectedSegments = 0;
        List<String> riskFactors = new ArrayList<>();

        boolean[] zoneAffected = new boolean[hazardZones.size()];

        for (int i = 0; i < waypoints.size() - 1; i++) {
            Coordinate start = waypoints.get(i);
            Coordinate end = waypoints.get(i + 1);

            double segmentLength = calculateDistance(start, end);
            Coordinate segmentMidpoint = calculateMidpoint(start, end);

            double segmentRisk = 0.0;
            boolean segmentAffected = false;
            List<String> segmentHazards = new ArrayList<>();

            for (int j = 0; j < hazardZones.size(); j++) {
                HazardZone zone = hazardZones.get(j);
                double proximity = calculateProximity(segmentMidpoint, zone);

                if (proximity > 0) {
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

        double normalizedRiskScore = totalRouteDistance > 0
                ? (totalRisk / totalRouteDistance) * 100.0
                : 0.0;

        normalizedRiskScore = Math.min(normalizedRiskScore, 100.0);

        String riskLevel;
        if (normalizedRiskScore <= LOW_RISK_THRESHOLD) {
            riskLevel = "LOW";
        } else if (normalizedRiskScore <= HIGH_RISK_THRESHOLD) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "HIGH";
        }

        int zonesAffecting = 0;
        for (boolean affected : zoneAffected) {
            if (affected)
                zonesAffecting++;
        }

        if (riskFactors.isEmpty()) {
            riskFactors.add("Route does not pass through any hazard zones");
        }

        return new RiskAnalysisResult(totalRouteDistance, normalizedRiskScore, riskLevel,
                affectedSegments, zonesAffecting, riskFactors);
    }

    public static class RiskAnalysisResult {
        private double totalDistance;
        private double riskScore;
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

        public String getReport() {
            StringBuilder sb = new StringBuilder();
            
            sb.append("\n");
            sb.append("═".repeat(80)).append("\n");
            sb.append("   AUTOMATIC ROUTE RISK ANALYSIS REPORT\n");
            sb.append("═".repeat(80)).append("\n\n");
            
            String riskIcon = riskLevel.equals("LOW") ? "✅" : riskLevel.equals("MEDIUM") ? "⚠️" : "🚫";
            String riskBar = generateRiskBar(riskScore);
            
            sb.append("📏 DISTANCE & RISK METRICS\n\n");
            sb.append(String.format("   Total Route Distance: %.2f km\n", totalDistance));
            sb.append(String.format("   Risk Score: %.2f / 100\n", riskScore));
            sb.append(String.format("   Risk Level: %s %s\n\n", riskIcon, riskLevel));
            sb.append("   Risk Visualization:\n");
            sb.append("   ").append(riskBar).append("\n\n");
            
            sb.append("📊 SEGMENT ANALYSIS\n\n");
            sb.append(String.format("   Route Segments Affected: %d\n", affectedSegments));
            sb.append(String.format("   Hazard Zones Affecting Route: %d\n\n", zonesAffecting));
            
            if (!riskFactors.isEmpty() && !riskFactors.get(0).contains("does not pass")) {
                sb.append("🔍 AFFECTED SEGMENTS DETAILS\n\n");
                for (String factor : riskFactors) {
                    sb.append("   • ").append(factor).append("\n");
                }
                sb.append("\n");
            } else {
                sb.append("   ✅ No segments affected by hazard zones\n\n");
            }

            sb.append("━".repeat(80)).append("\n\n");
            sb.append("📋 RISK ASSESSMENT\n\n");
            if (riskLevel.equals("LOW")) {
                sb.append("   ✅ SAFE ROUTE (Risk Score: 0-30)\n\n");
                sb.append("   └─ The route avoids hazard zones or has minimal exposure.\n");
                sb.append("   └─ Normal travel precautions are sufficient.\n");
                sb.append("   └─ Recommended: Proceed with standard safety measures.\n");
            } else if (riskLevel.equals("MEDIUM")) {
                sb.append("   ⚠️  MODERATE RISK ROUTE (Risk Score: 31-60)\n\n");
                sb.append("   └─ The route passes through or near hazard zones.\n");
                sb.append("   └─ Recommended: Consider alternative paths.\n");
                sb.append("   └─ If unavoidable: Take additional safety precautions.\n");
            } else {
                sb.append("   🚫 HIGH RISK ROUTE (Risk Score: 61-100)\n\n");
                sb.append("   └─ The route has significant exposure to hazard zones.\n");
                sb.append("   └─ Strongly Recommended: Seek an alternative route.\n");
                sb.append("   └─ If unavoidable: Implement comprehensive safety measures.\n");
                sb.append("   └─ Warning: Exercise extreme caution on this route.\n");
            }

            sb.append("\n");
            sb.append("━".repeat(80)).append("\n\n");
            sb.append("🔬 CALCULATION METHOD\n\n");
            sb.append("   Algorithm: Geospatial Proximity-Based Risk Assessment\n");
            sb.append("   Formula: (Σ segment_length × proximity) / total_distance × 100\n");
            sb.append("   Proximity: 1 - (distance_to_zone / zone_radius) when inside zone\n");
            sb.append("   Threshold: LOW [0-30], MEDIUM [31-60], HIGH [61-100]\n");

            return sb.toString();
        }
        
        private String generateRiskBar(double score) {
            int filled = (int) Math.round(score / 2.5);
            int empty = 40 - filled;
            String bar = "[";
            for (int i = 0; i < filled; i++) {
                bar += "█";
            }
            for (int i = 0; i < empty; i++) {
                bar += "░";
            }
            bar += String.format("] %.1f%%", score);
            return bar;
        }
    }
}
