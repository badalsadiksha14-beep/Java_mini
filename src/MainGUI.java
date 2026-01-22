import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI application for the Route Risk Analysis System.
 * Provides a Swing-based interface for:
 * - Inputting route coordinates
 * - Defining hazard zones
 * - Analyzing route risk
 * - Displaying results
 * 
 * Layout:
 * - Input panel for route and hazard zone data
 * - Control buttons
 * - Output panel for analysis results
 */
public class MainGUI extends JFrame {
    // GUI Components
    private JTextArea routeInputArea;
    private JTextArea hazardZoneInputArea;
    private JTextArea outputArea;
    private JButton analyzeButton;
    private JButton clearButton;
    private JButton loadSampleDataButton;

    // Data storage
    private Route currentRoute;
    private List<HazardZone> hazardZones;

    /**
     * Constructor to create and set up the GUI.
     */
    public MainGUI() {
        super("Route Risk Analysis System - University Geospatial Project");
        hazardZones = new ArrayList<>();
        initializeGUI();
    }

    /**
     * Initialize and layout all GUI components.
     */
    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout(10, 10));

        // Create main panels
        JPanel topPanel = createInputPanel();
        JPanel centerPanel = createOutputPanel();
        JPanel bottomPanel = createButtonPanel();

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Center the window on screen
        setLocationRelativeTo(null);
    }

    /**
     * Create the input panel for route coordinates and hazard zones.
     * 
     * @return configured JPanel with input components
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Route input section
        JPanel routePanel = new JPanel(new BorderLayout(5, 5));
        routePanel.setBorder(BorderFactory.createTitledBorder("Route Coordinates"));

        JLabel routeLabel = new JLabel("<html><b>Enter route coordinates (one per line):</b><br>" +
                "Format: latitude,longitude<br>" +
                "Example: 34.0522,-118.2437</html>");
        routeInputArea = new JTextArea(8, 30);
        routeInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane routeScroll = new JScrollPane(routeInputArea);

        routePanel.add(routeLabel, BorderLayout.NORTH);
        routePanel.add(routeScroll, BorderLayout.CENTER);

        // Hazard zone input section
        JPanel hazardPanel = new JPanel(new BorderLayout(5, 5));
        hazardPanel.setBorder(BorderFactory.createTitledBorder("Hazard Zones"));

        JLabel hazardLabel = new JLabel("<html><b>Enter hazard zones (one per line):</b><br>" +
                "Format: name,latitude,longitude,radius_km<br>" +
                "Example: Earthquake Zone,34.05,-118.25,50<br>" +
                "<i>Risk calculated automatically from proximity</i></html>");
        hazardZoneInputArea = new JTextArea(8, 30);
        hazardZoneInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane hazardScroll = new JScrollPane(hazardZoneInputArea);

        hazardPanel.add(hazardLabel, BorderLayout.NORTH);
        hazardPanel.add(hazardScroll, BorderLayout.CENTER);

        panel.add(routePanel);
        panel.add(hazardPanel);

        return panel;
    }

    /**
     * Create the output panel for displaying analysis results.
     * 
     * @return configured JPanel with output components
     */
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel outputLabel = new JLabel("Analysis Results:");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 14));

        outputArea = new JTextArea(20, 70);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(new Color(245, 245, 245));
        JScrollPane outputScroll = new JScrollPane(outputArea);

        panel.add(outputLabel, BorderLayout.NORTH);
        panel.add(outputScroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create the button panel with control buttons.
     * 
     * @return configured JPanel with buttons
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Analyze button
        analyzeButton = new JButton("Analyze Route Risk");
        analyzeButton.setFont(new Font("Arial", Font.BOLD, 14));
        analyzeButton.setBackground(new Color(70, 130, 180));
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setFocusPainted(false);
        analyzeButton.addActionListener(e -> analyzeRoute());

        // Clear button
        clearButton = new JButton("Clear All");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.addActionListener(e -> clearAll());

        // Load sample data button
        loadSampleDataButton = new JButton("Load Sample Data");
        loadSampleDataButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loadSampleDataButton.addActionListener(e -> loadSampleData());

        // Load Nepal data button
        JButton loadNepalDataButton = new JButton("Load Nepal Data");
        loadNepalDataButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loadNepalDataButton.addActionListener(e -> loadNepalData());

        panel.add(loadSampleDataButton);
        panel.add(loadNepalDataButton);
        panel.add(analyzeButton);
        panel.add(clearButton);

        return panel;
    }

    /**
     * Parse route coordinates from the input text area.
     * 
     * @return true if parsing was successful
     */
    private boolean parseRouteInput() {
        try {
            currentRoute = new Route("User Route");
            String[] lines = routeInputArea.getText().trim().split("\n");

            if (lines.length < 2) {
                showError("Route must have at least 2 coordinate points.");
                return false;
            }

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length != 2) {
                    showError("Invalid route format on line: " + line +
                            "\nExpected format: latitude,longitude");
                    return false;
                }

                double lat = Double.parseDouble(parts[0].trim());
                double lon = Double.parseDouble(parts[1].trim());
                currentRoute.addWaypoint(lat, lon);
            }

            if (!currentRoute.isValid()) {
                showError("Route must have at least 2 valid waypoints.");
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            showError("Invalid number format in route coordinates.\n" +
                    "Please use decimal numbers (e.g., 34.0522, -118.2437)");
            return false;
        } catch (IllegalArgumentException e) {
            showError("Invalid coordinates: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parse hazard zones from the input text area.
     * AUTOMATIC RISK CALCULATION - No risk weight needed.
     * 
     * @return true if parsing was successful
     */
    private boolean parseHazardZoneInput() {
        try {
            hazardZones.clear();
            String[] lines = hazardZoneInputArea.getText().trim().split("\n");

            if (lines.length == 0 || (lines.length == 1 && lines[0].trim().isEmpty())) {
                showError("At least one hazard zone must be defined.");
                return false;
            }

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length != 4) {
                    showError("Invalid hazard zone format on line: " + line +
                            "\nExpected format: name,latitude,longitude,radius_km");
                    return false;
                }

                String name = parts[0].trim();
                double lat = Double.parseDouble(parts[1].trim());
                double lon = Double.parseDouble(parts[2].trim());
                double radius = Double.parseDouble(parts[3].trim());

                Coordinate center = new Coordinate(lat, lon);
                HazardZone zone = new HazardZone(center, radius, name);
                hazardZones.add(zone);
            }

            return true;

        } catch (NumberFormatException e) {
            showError("Invalid number format in hazard zone data.\n" +
                    "Please use decimal numbers for lat/lon/radius/weight.");
            return false;
        } catch (IllegalArgumentException e) {
            showError("Invalid hazard zone: " + e.getMessage());
            return false;
        }
    }

    /**
     * Perform the route risk analysis.
     */
    private void analyzeRoute() {
        outputArea.setText("Analyzing route...\n");

        // Parse inputs
        if (!parseRouteInput()) {
            return;
        }

        if (!parseHazardZoneInput()) {
            return;
        }

        try {
            // Perform analysis
            RiskAnalyzer.RiskAnalysisResult result = RiskAnalyzer.analyzeRoute(currentRoute, hazardZones);

            // Display results
            outputArea.setText(result.getReport());

            // Add additional information
            outputArea.append("\n=== INPUT SUMMARY ===\n\n");
            outputArea.append(String.format("Route waypoints: %d\n", currentRoute.getWaypointCount()));
            outputArea.append(String.format("Hazard zones: %d\n\n", hazardZones.size()));

            outputArea.append("Hazard Zones:\n");
            for (HazardZone zone : hazardZones) {
                outputArea.append("  • " + zone.toString() + "\n");
            }

        } catch (Exception e) {
            showError("Error during analysis: " + e.getMessage());
        }
    }

    /**
     * Clear all input and output fields.
     */
    private void clearAll() {
        routeInputArea.setText("");
        hazardZoneInputArea.setText("");
        outputArea.setText("");
        currentRoute = null;
        hazardZones.clear();
    }

    /**
     * Load sample data for demonstration purposes.
     * This simulates a route in Southern California with earthquake zones.
     * AUTOMATIC RISK CALCULATION - No risk weights needed in input.
     */
    private void loadSampleData() {
        // Sample route: Los Angeles to San Diego
        routeInputArea.setText(
                "34.0522,-118.2437\n" + // Los Angeles
                        "33.8121,-117.9190\n" + // Anaheim
                        "33.6846,-117.8265\n" + // Irvine
                        "33.4936,-117.1484\n" + // Temecula
                        "33.1959,-117.3795\n" + // Escondido
                        "32.7157,-117.1611" // San Diego
        );

        // Sample hazard zones (no risk weights - calculated automatically)
        hazardZoneInputArea.setText(
                "San Andreas Fault Zone,34.00,-118.00,80\n" +
                        "Newport-Inglewood Fault,33.85,-118.10,45\n" +
                        "Elsinore Fault Zone,33.50,-117.30,60\n" +
                        "Rose Canyon Fault,32.85,-117.20,35");

        outputArea.setText("Sample data loaded.\n\n" +
                "This example shows a route from Los Angeles to San Diego\n" +
                "with major earthquake fault zones in Southern California.\n\n" +
                "Risk will be calculated AUTOMATICALLY from geospatial proximity.\n" +
                "No manual risk weights needed!\n\n" +
                "Click 'Analyze Route Risk' to see the automatic analysis.");
    }

    /**
     * Load Nepal sample data for demonstration purposes.
     * Route: Kathmandu → Chitwan → Lumbini → Bardiya National Park
     * Hazards: Earthquake zones, landslide areas, flood zones
     * AUTOMATIC RISK CALCULATION - No risk weights needed in input.
     */
    private void loadNepalData() {
        // Sample route: Major highway route through Nepal
        // Kathmandu → Chitwan → Lumbini → Bardiya
        routeInputArea.setText(
                "27.7172,85.3240\n" + // Kathmandu (capital city)
                        "27.6000,84.5000\n" + // Mugling (junction town)
                        "27.5291,84.3542\n" + // Chitwan (national park area)
                        "27.4833,83.5500\n" + // Butwal (city)
                        "27.5167,83.4500\n" + // Lumbini (Buddha's birthplace)
                        "28.0000,82.0000\n" + // Dang Valley
                        "28.3305,81.6084" // Bardiya (western region)
        );

        // Nepal hazard zones (earthquakes, landslides, floods)
        hazardZoneInputArea.setText(
                "Main Himalayan Thrust,27.8,85.5,120\n" + // Major seismic zone near Kathmandu
                        "Central Nepal Earthquake Zone,28.0,84.5,100\n" + // 2015 earthquake epicenter region
                        "Mahabharat Range Landslide Zone,27.5,84.7,45\n" + // Landslide-prone hills
                        "Siwalik Hills Landslide Area,27.6,84.0,35\n" + // Monsoon landslide zone
                        "Rapti River Flood Plain,27.5,84.4,30\n" + // Seasonal flooding area
                        "Terai Flood Zone,27.4,83.5,50\n" + // Monsoon flood plains
                        "Western Nepal Seismic Belt,28.5,82.0,80" // Western earthquake activity
        );

        outputArea.setText("Nepal route data loaded! 🇳🇵\n\n" +
                "Route: Kathmandu → Chitwan → Lumbini → Bardiya\n" +
                "Distance: ~450 km through diverse terrain\n\n" +
                "Hazard Zones:\n" +
                "• Main Himalayan Thrust (120 km radius) - Major earthquake zone\n" +
                "• Central Nepal Earthquake Zone (100 km) - 2015 earthquake region\n" +
                "• Landslide zones in hilly areas (35-45 km)\n" +
                "• Flood zones in Terai plains (30-50 km)\n" +
                "• Western seismic belt (80 km)\n\n" +
                "Risk calculated AUTOMATICALLY from geospatial proximity!\n\n" +
                "Click 'Analyze Route Risk' to see the automatic analysis.\n\n" +
                "नोट: यो नेपालको प्रमुख राजमार्ग मार्ग हो।");
    }

    /**
     * Display an error message dialog.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error",
                JOptionPane.ERROR_MESSAGE);
        outputArea.setText("Error: " + message);
    }

    /**
     * Main method to launch the application.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Use the Event Dispatch Thread for Swing components
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to the system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // If setting look and feel fails, use default
                e.printStackTrace();
            }

            // Create and display the GUI
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
