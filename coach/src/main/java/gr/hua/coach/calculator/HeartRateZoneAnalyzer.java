package gr.hua.coach.calculator;

import gr.hua.coach.model.Activity;
import gr.hua.coach.model.Lap;
import gr.hua.coach.model.Track;
import gr.hua.coach.model.TrackPoint;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeartRateZoneAnalyzer {
    
    public enum Zone {
        ZONE_1(0.50, 0.60, 0.07, "Very Light"),
        ZONE_2(0.60, 0.70, 0.10, "Light"),
        ZONE_3(0.70, 0.80, 0.13, "Moderate"),
        ZONE_4(0.80, 0.90, 0.16, "Hard"),
        ZONE_5(0.90, 1.00, 0.20, "Maximum");
        
        private final double minPercent;
        private final double maxPercent;
        private final double calorieEfficiency;
        private final String description;
        
        Zone(double minPercent, double maxPercent, double calorieEfficiency, String description) {
            this.minPercent = minPercent;
            this.maxPercent = maxPercent;
            this.calorieEfficiency = calorieEfficiency;
            this.description = description;
        }
        
        public double getMinPercent() { return minPercent; }
        public double getMaxPercent() { return maxPercent; }
        public double getCalorieEfficiency() { return calorieEfficiency; }
        public String getDescription() { return description; }
        
        public boolean contains(double hr, double mhr) {
            double percent = hr / mhr;
            return percent >= minPercent && percent < maxPercent;
        }
        
        public int getMinBpm(double mhr) {
            return (int) Math.round(minPercent * mhr);
        }
        
        public int getMaxBpm(double mhr) {
            return (int) Math.round(maxPercent * mhr);
        }
    }
    
    private final int age;
    private final double maxHeartRate;
    
    public HeartRateZoneAnalyzer(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        this.age = age;
        this.maxHeartRate = 220 - age;
    }
    
    public double getMaxHeartRate() {
        return maxHeartRate;
    }
    
    public ZoneAnalysis analyze(Activity activity) {
        Map<Zone, Double> timeInZones = new HashMap<>();
        for (Zone zone : Zone.values()) {
            timeInZones.put(zone, 0.0);
        }
        
        for (Lap lap : activity.getLaps()) {
            for (Track track : lap.getTracks()) {
                analyzeTrack(track, timeInZones);
            }
        }
        
        return new ZoneAnalysis(timeInZones, maxHeartRate);
    }
    
    private void analyzeTrack(Track track, Map<Zone, Double> timeInZones) {
        List<TrackPoint> points = track.getTrackPoints();
        
        for (int i = 1; i < points.size(); i++) {
            TrackPoint prev = points.get(i - 1);
            TrackPoint curr = points.get(i);
            
            if (prev.getHeartRate() == null || curr.getHeartRate() == null ||
                prev.getTime() == null || curr.getTime() == null) {
                continue;
            }
            
            double avgHr = (prev.getHeartRate() + curr.getHeartRate()) / 2.0;
            double seconds = Duration.between(prev.getTime(), curr.getTime()).getSeconds();
            
            Zone zone = getZoneForHeartRate(avgHr);
            if (zone != null) {
                timeInZones.merge(zone, seconds, Double::sum);
            }
        }
    }
    
    private Zone getZoneForHeartRate(double hr) {
        for (Zone zone : Zone.values()) {
            if (zone.contains(hr, maxHeartRate)) {
                return zone;
            }
        }
        return null;
    }
    
    public double calculateCaloriesFromZones(ZoneAnalysis analysis, double weight) {
        double totalCalories = 0;
        
        for (Zone zone : Zone.values()) {
            double minutes = analysis.getTimeInZone(zone) / 60.0;
            totalCalories += minutes * zone.getCalorieEfficiency() * weight;
        }
        
        return totalCalories;
    }
    
    public static class ZoneAnalysis {
        private final Map<Zone, Double> timeInZones;
        private final double maxHeartRate;
        
        public ZoneAnalysis(Map<Zone, Double> timeInZones, double maxHeartRate) {
            this.timeInZones = new HashMap<>(timeInZones);
            this.maxHeartRate = maxHeartRate;
        }
        
        public double getTimeInZone(Zone zone) {
            return timeInZones.getOrDefault(zone, 0.0);
        }
        
        public double getTotalTime() {
            return timeInZones.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        
        public double getPercentageInZone(Zone zone) {
            double total = getTotalTime();
            if (total == 0) return 0;
            return (getTimeInZone(zone) / total) * 100;
        }
        
        public double getMaxHeartRate() {
            return maxHeartRate;
        }
        
        public Map<Zone, Double> getAllZones() {
            return new HashMap<>(timeInZones);
        }
        
        public String formatReport() {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append("        HEART RATE ZONE ANALYSIS\n");
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append(String.format("Maximum Heart Rate: %.0f bpm (age-based)\n\n", maxHeartRate));
            
            for (Zone zone : Zone.values()) {
                double seconds = getTimeInZone(zone);
                double percentage = getPercentageInZone(zone);
                
                sb.append(String.format("%s (%s):\n", zone.name().replace("_", " "), zone.getDescription()));
                sb.append(String.format("  Range: %d-%d bpm (%.0f%%-%.0f%% MHR)\n",
                    zone.getMinBpm(maxHeartRate),
                    zone.getMaxBpm(maxHeartRate),
                    zone.getMinPercent() * 100,
                    zone.getMaxPercent() * 100));
                sb.append(String.format("  Time: %s (%.1f%%)\n",
                    formatTime(seconds),
                    percentage));
                sb.append(String.format("  Efficiency: %.2f\n\n", zone.getCalorieEfficiency()));
            }
            
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append(String.format("Total Time with HR Data: %s\n", formatTime(getTotalTime())));
            
            return sb.toString();
        }
        
        private String formatTime(double seconds) {
            int h = (int) (seconds / 3600);
            int m = (int) ((seconds % 3600) / 60);
            int s = (int) (seconds % 60);
            if (h > 0) {
                return String.format("%02d:%02d:%02d", h, m, s);
            } else {
                return String.format("%02d:%02d", m, s);
            }
        }
    }
}