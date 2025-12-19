package gr.hua.coach.parser;

import gr.hua.coach.model.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TCXParser {
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'");
    
    public List<Activity> parse(File file) throws Exception {
        List<Activity> activities = new ArrayList<>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        
        NodeList activityNodes = doc.getElementsByTagName("Activity");
        
        for (int i = 0; i < activityNodes.getLength(); i++) {
            Element activityElement = (Element) activityNodes.item(i);
            Activity activity = parseActivity(activityElement);
            if (activity != null) {
                activities.add(activity);
            }
        }
        
        return activities;
    }
    
    private Activity parseActivity(Element activityElement) {
        String sport = activityElement.getAttribute("Sport");
        if (sport == null || sport.isEmpty()) {
            sport = "Unknown";
        }
        
        Activity activity = new Activity();
        activity.setSport(sport);
        
        // Parse Id as start time
        NodeList idNodes = activityElement.getElementsByTagName("Id");
        if (idNodes.getLength() > 0) {
            String idText = idNodes.item(0).getTextContent();
            try {
                LocalDateTime startTime = LocalDateTime.parse(idText, formatter);
                activity.setStartTime(startTime);
            } catch (Exception e) {
                // If parsing fails, use current time
                activity.setStartTime(LocalDateTime.now());
            }
        }
        
        // Parse Laps
        NodeList lapNodes = activityElement.getElementsByTagName("Lap");
        for (int i = 0; i < lapNodes.getLength(); i++) {
            Element lapElement = (Element) lapNodes.item(i);
            Lap lap = parseLap(lapElement);
            if (lap != null) {
                activity.addLap(lap);
            }
        }
        
        return activity;
    }
    
    private Lap parseLap(Element lapElement) {
        Lap lap = new Lap();
        
        // Parse lap start time
        String startTimeStr = lapElement.getAttribute("StartTime");
        if (startTimeStr != null && !startTimeStr.isEmpty()) {
            try {
                LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
                lap.setStartTime(startTime);
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        // Parse Tracks
        NodeList trackNodes = lapElement.getElementsByTagName("Track");
        for (int i = 0; i < trackNodes.getLength(); i++) {
            Element trackElement = (Element) trackNodes.item(i);
            Track track = parseTrack(trackElement);
            if (track != null) {
                lap.addTrack(track);
            }
        }
        
        return lap;
    }
    
    private Track parseTrack(Element trackElement) {
        Track track = new Track();
        
        NodeList trackpointNodes = trackElement.getElementsByTagName("Trackpoint");
        for (int i = 0; i < trackpointNodes.getLength(); i++) {
            Element trackpointElement = (Element) trackpointNodes.item(i);
            TrackPoint trackPoint = parseTrackPoint(trackpointElement);
            if (trackPoint != null) {
                track.addTrackPoint(trackPoint);
            }
        }
        
        return track;
    }
    
    private TrackPoint parseTrackPoint(Element trackpointElement) {
        TrackPoint point = new TrackPoint();
        
        // Parse time
        NodeList timeNodes = trackpointElement.getElementsByTagName("Time");
        if (timeNodes.getLength() > 0) {
            String timeText = timeNodes.item(0).getTextContent();
            try {
                LocalDateTime time = LocalDateTime.parse(timeText, formatter);
                point.setTime(time);
            } catch (Exception e) {
                // Ignore parsing error
            }
        }
        
        // Parse position
        NodeList positionNodes = trackpointElement.getElementsByTagName("Position");
        if (positionNodes.getLength() > 0) {
            Element positionElement = (Element) positionNodes.item(0);
            
            NodeList latNodes = positionElement.getElementsByTagName("LatitudeDegrees");
            if (latNodes.getLength() > 0) {
                try {
                    double latitude = Double.parseDouble(latNodes.item(0).getTextContent());
                    point.setLatitude(latitude);
                } catch (NumberFormatException e) {
                    // Ignore parsing error
                }
            }
            
            NodeList lonNodes = positionElement.getElementsByTagName("LongitudeDegrees");
            if (lonNodes.getLength() > 0) {
                try {
                    double longitude = Double.parseDouble(lonNodes.item(0).getTextContent());
                    point.setLongitude(longitude);
                } catch (NumberFormatException e) {
                    // Ignore parsing error
                }
            }
        }
        
        // Parse altitude
        NodeList altNodes = trackpointElement.getElementsByTagName("AltitudeMeters");
        if (altNodes.getLength() > 0) {
            try {
                double altitude = Double.parseDouble(altNodes.item(0).getTextContent());
                point.setAltitude(altitude);
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        // Parse distance
        NodeList distNodes = trackpointElement.getElementsByTagName("DistanceMeters");
        if (distNodes.getLength() > 0) {
            try {
                double distance = Double.parseDouble(distNodes.item(0).getTextContent());
                point.setDistance(distance);
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        // Parse heart rate
        NodeList hrNodes = trackpointElement.getElementsByTagName("HeartRateBpm");
        if (hrNodes.getLength() > 0) {
            Element hrElement = (Element) hrNodes.item(0);
            NodeList valueNodes = hrElement.getElementsByTagName("Value");
            if (valueNodes.getLength() > 0) {
                try {
                    int heartRate = Integer.parseInt(valueNodes.item(0).getTextContent());
                    point.setHeartRate(heartRate);
                } catch (NumberFormatException e) {
                    // Ignore parsing error
                }
            }
        }
        
        // Parse cadence
        NodeList cadenceNodes = trackpointElement.getElementsByTagName("Cadence");
        if (cadenceNodes.getLength() > 0) {
            try {
                double cadence = Double.parseDouble(cadenceNodes.item(0).getTextContent());
                point.setCadence(cadence);
            } catch (NumberFormatException e) {
                // Ignore parsing error
            }
        }
        
        return point;
    }
}