package gr.hua.coach.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Activity {
    private String sport;
    private LocalDateTime startTime;
    private List<Lap> laps;
    
    public Activity() {
        this.laps = new ArrayList<>();
    }
    
    // Getters and setters
    public String getSport() { return sport; }
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