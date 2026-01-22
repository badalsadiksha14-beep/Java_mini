import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<Coordinate> waypoints;
    private String routeName;

    public Route(String routeName) {
        this.routeName = routeName;
        this.waypoints = new ArrayList<>();
    }

    public void addWaypoint(Coordinate coordinate) {
        waypoints.add(coordinate);
    }

    public void addWaypoint(double latitude, double longitude) {
        waypoints.add(new Coordinate(latitude, longitude));
    }

    public List<Coordinate> getWaypoints() {
        return waypoints;
    }

    public int getWaypointCount() {
        return waypoints.size();
    }

    public String getRouteName() {
        return routeName;
    }

    public boolean isValid() {
        return waypoints.size() >= 2;
    }

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
