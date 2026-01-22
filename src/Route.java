import java.util.ArrayList;
import java.util.List;

/**
 * Represents a travel route as a sequence of geographic coordinates.
 * A route consists of multiple waypoints that define a path from
 * a starting point to a destination.
 */
public class Route {
    private List<Coordinate> waypoints;
    private String routeName;
    
    /**
     * Constructor to create an empty route.
     * @param routeName A descriptive name for the route
     */
    public Route(String routeName) {
        this.routeName = routeName;
        this.waypoints = new ArrayList<>();
    }
    
    /**
     * Add a waypoint to the end of the route.
     * @param coordinate The coordinate to add
     */
    public void addWaypoint(Coordinate coordinate) {
        waypoints.add(coordinate);
    }
    
    /**
     * Add a waypoint using latitude and longitude values.
     * @param latitude The latitude in degrees
     * @param longitude The longitude in degrees
     */
    public void addWaypoint(double latitude, double longitude) {
        waypoints.add(new Coordinate(latitude, longitude));
    }
    
    /**
     * Get all waypoints in the route.
     * @return list of coordinates
     */
    public List<Coordinate> getWaypoints() {
        return waypoints;
    }
    
    /**
     * Get the number of waypoints in the route.
     * @return number of waypoints
     */
    public int getWaypointCount() {
        return waypoints.size();
    }
    
    /**
     * Get the route name.
     * @return route name
     */
    public String getRouteName() {
        return routeName;
    }
    
    /**
     * Check if the route has at least two points (minimum for a valid route).
     * @return true if route is valid
     */
    public boolean isValid() {
        return waypoints.size() >= 2;
    }
    
    /**
     * String representation of the route.
     * @return formatted route description
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Route: %s (%d waypoints)\n", routeName, waypoints.size()));
        for (int i = 0; i < waypoints.size(); i++) {
            sb.append(String.format("  Point %d: %s\n", i + 1, waypoints.get(i)));
        }
        return sb.toString();
    }
}
