package gr.hua.coach.model;

import java.time.LocalDateTime;

public class TrackPoint {
    private LocalDateTime time;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double distance;
    private Integer heartRate;
    private Double cadence;
    
    // Getters kai setters
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Double getAltitude() { return altitude; }
    public void setAltitude(Double altitude) { this.altitude = altitude; }
    
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    
    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }
    
    public Double getCadence() { return cadence; }
    public void setCadence(Double cadence) { this.cadence = cadence; }
}