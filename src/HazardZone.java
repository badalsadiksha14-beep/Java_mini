/**
 * Represents a circular hazard zone on a geographic map.
 * Hazard zones represent areas of risk such as earthquake zones,
 * terrain hazards, or other geographic dangers.
 * 
 * The zone is defined as a circle with:
 * - Center point (latitude, longitude)
 * - Radius of influence in kilometers
 * 
 * AUTOMATIC RISK CALCULATION:
 * Risk is computed automatically based on geospatial proximity.
 * No manual risk weights are needed.
 */
public class HazardZone {
    private Coordinate center;
    private double radiusKm;
    private String name;

    /**
     * Constructor to create a hazard zone.
     * 
     * @param center   The center coordinate of the hazard zone
     * @param radiusKm The radius of influence in kilometers (must be positive)
     * @param name     A descriptive name for the hazard zone
     * @throws IllegalArgumentException if radius is invalid
     */
    public HazardZone(Coordinate center, double radiusKm, String name) {
        if (radiusKm <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
        this.center = center;
        this.radiusKm = radiusKm;
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
        return String.format("%s: Center %s, Radius %.2f km",
                name, center, radiusKm);
    }
}
