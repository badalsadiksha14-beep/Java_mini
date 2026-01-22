/**
 * Represents a circular hazard zone on a geographic map.
 * Hazard zones represent areas of risk such as earthquake zones,
 * terrain hazards, or other geographic dangers.
 * 
 * The zone is defined as a circle with:
 * - Center point (latitude, longitude)
 * - Radius in kilometers
 * - Risk weight (higher value = more dangerous)
 */
public class HazardZone {
    private Coordinate center;
    private double radiusKm;
    private double riskWeight;
    private String name;

    /**
     * Constructor to create a hazard zone.
     * 
     * @param center     The center coordinate of the hazard zone
     * @param radiusKm   The radius of the zone in kilometers (must be positive)
     * @param riskWeight The risk weight/severity (1.0 = low, 5.0 = high, 10.0 =
     *                   extreme)
     * @param name       A descriptive name for the hazard zone
     * @throws IllegalArgumentException if radius or risk weight are invalid
     */
    public HazardZone(Coordinate center, double radiusKm, double riskWeight, String name) {
        if (radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        if (riskWeight <= 0) {
            throw new IllegalArgumentException("Risk weight must be positive");
        }
        this.center = center;
        this.radiusKm = radiusKm;
        this.riskWeight = riskWeight;
        this.name = name;
    }

    /**
     * Get the center coordinate of the hazard zone.
     * 
     * @return center coordinate
     */
    public Coordinate getCenter() {
        return center;
    }

    /**
     * Get the radius of the hazard zone in kilometers.
     * 
     * @return radius in km
     */
    public double getRadiusKm() {
        return radiusKm;
    }

    /**
     * Get the risk weight of the hazard zone.
     * 
     * @return risk weight value
     */
    public double getRiskWeight() {
        return riskWeight;
    }

    /**
     * Get the name of the hazard zone.
     * 
     * @return name of the zone
     */
    public String getName() {
        return name;
    }

    /**
     * String representation of the hazard zone.
     * 
     * @return formatted hazard zone description
     */
    @Override
    public String toString() {
        return String.format("%s: Center %s, Radius %.2f km, Risk Weight %.1f",
                name, center, radiusKm, riskWeight);
    }
}
