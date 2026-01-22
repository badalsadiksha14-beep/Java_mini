/**
 * Represents a geographic coordinate with latitude and longitude.
 * Used to define points in a route or centers of hazard zones.
 * 
 * Coordinate system: WGS84 (World Geodetic System 1984)
 * Latitude: -90 to +90 degrees (South to North)
 * Longitude: -180 to +180 degrees (West to East)
 */
public class Coordinate {
    private double latitude;
    private double longitude;
    
    /**
     * Constructor to create a coordinate with validation.
     * @param latitude The latitude in degrees (-90 to +90)
     * @param longitude The longitude in degrees (-180 to +180)
     * @throws IllegalArgumentException if coordinates are out of valid range
     */
    public Coordinate(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    /**
     * Get the latitude value.
     * @return latitude in degrees
     */
    public double getLatitude() {
        return latitude;
    }
    
    /**
     * Get the longitude value.
     * @return longitude in degrees
     */
    public double getLongitude() {
        return longitude;
    }
    
    /**
     * String representation of the coordinate.
     * @return formatted coordinate string
     */
    @Override
    public String toString() {
        return String.format("(%.6f, %.6f)", latitude, longitude);
    }
}
