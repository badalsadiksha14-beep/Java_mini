import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainGUI extends JFrame {
    private JTextArea routeInputArea;
    private JTextArea hazardZoneInputArea;
    private JTextArea outputArea;
    private JButton analyzeButton;
    private JButton clearButton;
    private JButton loadSampleDataButton;

    private Route currentRoute;
    private List<HazardZone> hazardZones;

    public MainGUI() {
        super("Route Risk Analysis System - University Geospatial Project");
        hazardZones = new ArrayList<>();
        initializeGUI();
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 242, 245));

        JPanel topPanel = createInputPanel();
        JPanel centerPanel = createOutputPanel();
        JPanel bottomPanel = createButtonPanel();

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(new Color(240, 242, 245));

        JPanel routePanel = new JPanel(new BorderLayout(8, 8));
        routePanel.setBackground(Color.WHITE);
        routePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel routeLabel = new JLabel("<html><div style='font-size:11px;'><b style='font-size:13px; color:#2c3e50;'>📍 Route Coordinates</b><br><br>" +
                "<span style='color:#555;'>Format: <code>latitude,longitude</code></span><br>" +
                "<span style='color:#888;'>Example: 34.0522,-118.2437</span></div></html>");
        routeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        routeInputArea = new JTextArea(9, 35);
        routeInputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        routeInputArea.setLineWrap(false);
        routeInputArea.setMargin(new Insets(8, 8, 8, 8));
        routeInputArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
        JScrollPane routeScroll = new JScrollPane(routeInputArea);
        routeScroll.setBorder(BorderFactory.createEmptyBorder());

        routePanel.add(routeLabel, BorderLayout.NORTH);
        routePanel.add(routeScroll, BorderLayout.CENTER);

        JPanel hazardPanel = new JPanel(new BorderLayout(8, 8));
        hazardPanel.setBackground(Color.WHITE);
        hazardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel hazardLabel = new JLabel("<html><div style='font-size:11px;'><b style='font-size:13px; color:#2c3e50;'>⚠️ Hazard Zones</b><br><br>" +
                "<span style='color:#555;'>Format: <code>name,latitude,longitude,radius_km</code></span><br>" +
                "<span style='color:#888;'>Example: Earthquake Zone,34.05,-118.25,50</span><br><br>" +
                "<i style='color:#3498db;'>Risk calculated automatically from proximity</i></div></html>");
        hazardLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        hazardZoneInputArea = new JTextArea(9, 35);
        hazardZoneInputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        hazardZoneInputArea.setLineWrap(false);
        hazardZoneInputArea.setMargin(new Insets(8, 8, 8, 8));
        hazardZoneInputArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
        JScrollPane hazardScroll = new JScrollPane(hazardZoneInputArea);
        hazardScroll.setBorder(BorderFactory.createEmptyBorder());

        hazardPanel.add(hazardLabel, BorderLayout.NORTH);
        hazardPanel.add(hazardScroll, BorderLayout.CENTER);

        panel.add(routePanel);
        panel.add(hazardPanel);

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        panel.setBackground(new Color(240, 242, 245));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        
        JLabel outputLabel = new JLabel("📊 Analysis Results");
        outputLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        outputLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(outputLabel);

        outputArea = new JTextArea(22, 80);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setBackground(Color.WHITE);
        outputArea.setForeground(new Color(33, 33, 33));
        outputArea.setMargin(new Insets(15, 15, 15, 15));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
            BorderFactory.createEmptyBorder()));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(outputScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        panel.setBackground(new Color(240, 242, 245));

        loadSampleDataButton = createStyledButton("🌎 Load Sample Data", new Color(95, 99, 104), Color.WHITE);
        loadSampleDataButton.addActionListener(e -> loadSampleData());

        JButton loadNepalDataButton = createStyledButton("🏔️ Load Nepal Data", new Color(95, 99, 104), Color.WHITE);
        loadNepalDataButton.addActionListener(e -> loadNepalData());

        analyzeButton = createStyledButton("🔍 Analyze Route Risk", new Color(46, 125, 50), Color.WHITE);
        analyzeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        analyzeButton.addActionListener(e -> analyzeRoute());

        clearButton = createStyledButton("🗑️ Clear All", new Color(211, 47, 47), Color.WHITE);
        clearButton.addActionListener(e -> clearAll());

        panel.add(loadSampleDataButton);
        panel.add(loadNepalDataButton);
        panel.add(analyzeButton);
        panel.add(clearButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

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

    private void analyzeRoute() {
        outputArea.setText("⏳ Analyzing route...\n");

        if (!parseRouteInput()) {
            return;
        }

        if (!parseHazardZoneInput()) {
            return;
        }

        try {
            RiskAnalyzer.RiskAnalysisResult result = RiskAnalyzer.analyzeRoute(currentRoute, hazardZones);

            StringBuilder output = new StringBuilder();
            output.append(result.getReport());
            
            output.append("\n");
            output.append("═".repeat(80)).append("\n");
            output.append("\n📋 INPUT SUMMARY\n\n");
            output.append(String.format("   Route Waypoints: %d points\n", currentRoute.getWaypointCount()));
            output.append(String.format("   Hazard Zones: %d zones\n", hazardZones.size()));
            output.append(String.format("   Total Segments: %d segments\n", currentRoute.getWaypointCount() - 1));
            
            output.append("\n🗺️  ROUTE WAYPOINTS\n\n");
            List<Coordinate> waypoints = currentRoute.getWaypoints();
            for (int i = 0; i < waypoints.size(); i++) {
                Coordinate coord = waypoints.get(i);
                output.append(String.format("   Point %d: %s\n", i + 1, coord.toString()));
            }
            
            output.append("\n⚠️  HAZARD ZONES\n\n");
            for (int i = 0; i < hazardZones.size(); i++) {
                HazardZone zone = hazardZones.get(i);
                output.append(String.format("   %d. %s\n", i + 1, zone.toString()));
            }
            
            output.append("\n");
            output.append("═".repeat(80)).append("\n");
            output.append("\n✅ Analysis completed successfully\n");
            
            outputArea.setText(output.toString());
            outputArea.setCaretPosition(0);

        } catch (Exception e) {
            showError("Error during analysis: " + e.getMessage());
        }
    }

    private void clearAll() {
        routeInputArea.setText("");
        hazardZoneInputArea.setText("");
        outputArea.setText("");
        currentRoute = null;
        hazardZones.clear();
    }

    private void loadSampleData() {
        routeInputArea.setText(
                "34.0522,-118.2437\n" +
                        "33.8121,-117.9190\n" +
                        "33.6846,-117.8265\n" +
                        "33.4936,-117.1484\n" +
                        "33.1959,-117.3795\n" +
                        "32.7157,-117.1611");

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

    private void loadNepalData() {
        routeInputArea.setText(
                "27.7172,85.3240\n" +
                        "27.6000,84.5000\n" +
                        "27.5291,84.3542\n" +
                        "27.4833,83.5500\n" +
                        "27.5167,83.4500\n" +
                        "28.0000,82.0000\n" +
                        "28.3305,81.6084");

        hazardZoneInputArea.setText(
                "Main Himalayan Thrust,27.8,85.5,120\n" +
                        "Central Nepal Earthquake Zone,28.0,84.5,100\n" +
                        "Mahabharat Range Landslide Zone,27.5,84.7,45\n" +
                        "Siwalik Hills Landslide Area,27.6,84.0,35\n" +
                        "Rapti River Flood Plain,27.5,84.4,30\n" +
                        "Terai Flood Zone,27.4,83.5,50\n" +
                        "Western Nepal Seismic Belt,28.5,82.0,80");

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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error",
                JOptionPane.ERROR_MESSAGE);
        outputArea.setText("Error: " + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
