package gr.hua.coach.model;

import java.util.ArrayList;
import java.util.List;

public class Track {
    private List<TrackPoint> trackPoints;
    
    public Track() {
        this.trackPoints = new ArrayList<>();
    }
    
    // Getters kai setters
    public List<TrackPoint> getTrackPoints() { return trackPoints; }
    public void setTrackPoints(List<TrackPoint> trackPoints) { this.trackPoints = trackPoints; }
    
    public void addTrackPoint(TrackPoint point) { this.trackPoints.add(point); }
}