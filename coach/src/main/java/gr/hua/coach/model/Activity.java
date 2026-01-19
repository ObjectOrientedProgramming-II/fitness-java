package gr.hua.coach.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
// Model klaseis pou anaparistoun TCX data (Activity -> Lap -> Track -> TrackPoint).
// Einai katharo data model xoris business logic gia na menei to system testable kai modular.

public class Activity {
    private String sport;
    private LocalDateTime startTime; // Start time tou activity
    private List<Lap> laps;
    
    public Activity() {
        this.laps = new ArrayList<>();
    }
    
    public String getSport() { return sport; }  // Eidos sport 
    public void setSport(String sport) { this.sport = sport; } 
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public List<Lap> getLaps() { return laps; }
    public void setLaps(List<Lap> laps) { this.laps = laps; }
    
    public void addLap(Lap lap) { this.laps.add(lap); }
    
    public boolean hasDistance() {
        return getSport() != null && 
               (getSport().equalsIgnoreCase("Running") ||
                getSport().equalsIgnoreCase("Cycling") ||
                getSport().equalsIgnoreCase("Walking") ||
                getSport().equalsIgnoreCase("Swimming"));
    }
}