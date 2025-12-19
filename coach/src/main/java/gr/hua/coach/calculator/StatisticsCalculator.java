package gr.hua.coach.calculator;

import gr.hua.coach.model.*;

import java.time.Duration;
import java.util.List;

public class StatisticsCalculator {
    
    public double calculateTotalTime(Activity activity) {
        double totalSeconds = 0;
        
        for (Lap lap : activity.getLaps()) {
            for (Track track : lap.getTracks()) {
                List<TrackPoint> points = track.getTrackPoints();
                if (points.size() >= 2) {
                    TrackPoint first = points.get(0);
                    TrackPoint last = points.get(points.size() - 1);
                    
                    if (first.getTime() != null && last.getTime() != null) {
                        Duration duration = Duration.between(first.getTime(), last.getTime());
                        totalSeconds += duration.getSeconds();
                    }
                }
            }
        }
        
        return totalSeconds;
    }
    
    public double calculateTotalDistance(Activity activity) {
        double totalDistance = 0;
        
        for (Lap lap : activity.getLaps()) {
            for (Track track : lap.getTracks()) {
                List<TrackPoint> points = track.getTrackPoints();
                if (!points.isEmpty()) {
                    // Use the last trackpoint's distance if available
                    TrackPoint lastPoint = points.get(points.size() - 1);
                    if (lastPoint.getDistance() != null) {
                        totalDistance += lastPoint.getDistance();
                    } else {
                        // Calculate distance from coordinates if no distance field
                        totalDistance += calculateDistanceFromCoordinates(points);
                    }
                }
            }
        }
        
        return totalDistance;
    }
    
    private double calculateDistanceFromCoordinates(List<TrackPoint> points) {
        double distance = 0;
        
        for (int i = 1; i < points.size(); i++) {
            TrackPoint prev = points.get(i - 1);
            TrackPoint curr = points.get(i);
            
            if (prev.getLatitude() != null && prev.getLongitude() != null &&
                curr.getLatitude() != null && curr.getLongitude() != null) {
                
                distance += haversineDistance(
                    prev.getLatitude(), prev.getLongitude(),
                    curr.getLatitude(), curr.getLongitude()
                );
            }
        }
        
        return distance;
    }
    
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    public double calculateAverageSpeed(Activity activity) {
        double totalDistance = calculateTotalDistance(activity);
        double totalTime = calculateTotalTime(activity);
        
        if (totalTime > 0) {
            return (totalDistance / 1000) / (totalTime / 3600); // km/h
        }
        
        return 0;
    }
    
    public double calculateAveragePace(Activity activity) {
        double totalDistance = calculateTotalDistance(activity);
        double totalTime = calculateTotalTime(activity);
        
        if (totalDistance > 0) {
            return (totalTime / 60) / (totalDistance / 1000); // min/km
        }
        
        return 0;
    }
    
    public double calculateAverageHeartRate(Activity activity) {
        int totalHeartRate = 0;
        int count = 0;
        
        for (Lap lap : activity.getLaps()) {
            for (Track track : lap.getTracks()) {
                for (TrackPoint point : track.getTrackPoints()) {
                    if (point.getHeartRate() != null) {
                        totalHeartRate += point.getHeartRate();
                        count++;
                    }
                }
            }
        }
        
        if (count > 0) {
            return (double) totalHeartRate / count;
        }
        
        return 0;
    }
}