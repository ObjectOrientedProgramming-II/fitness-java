package gr.hua.coach.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Lap {
    private LocalDateTime startTime;
    private List<Track> tracks;
    
    public Lap() {
        this.tracks = new ArrayList<>();
    }
    
    // Getters and setters
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public List<Track> getTracks() { return tracks; }
    public void setTracks(List<Track> tracks) { this.tracks = tracks; }
    
    public void addTrack(Track track) { this.tracks.add(track); }
}