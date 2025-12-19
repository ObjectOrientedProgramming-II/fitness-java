package gr.hua.coach;

import gr.hua.coach.model.Activity;
import gr.hua.coach.parser.TCXParser;
import gr.hua.coach.calculator.StatisticsCalculator;
import gr.hua.coach.calculator.CaloriesCalculator;

import java.util.*;
import java.io.File;

public class Main {
    private static double weight = -1;
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        List<String> tcxFiles = new ArrayList<>();
        
        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-w") && i + 1 < args.length) {
                try {
                    weight = Double.parseDouble(args[i + 1]);
                    i++; // Skip next argument since it's the weight value
                } catch (NumberFormatException e) {
                    System.err.println("Error: Weight must be a number");
                    return;
                }
            } else if (args[i].endsWith(".tcx")) {
                tcxFiles.add(args[i]);
            }
        }
        
        if (tcxFiles.isEmpty()) {
            System.err.println("Error: No .tcx files provided");
            printUsage();
            return;
        }
        
        // Process each file
        List<Activity> allActivities = new ArrayList<>();
        TCXParser parser = new TCXParser();
        
        for (String filePath : tcxFiles) {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Warning: File not found - " + filePath);
                continue;
            }
            
            try {
                List<Activity> activities = parser.parse(file);
                allActivities.addAll(activities);
                System.out.println("Successfully parsed: " + filePath);
            } catch (Exception e) {
                System.err.println("Error parsing " + filePath + ": " + e.getMessage());
            }
        }
        
        // Display statistics for all activities
        displayStatistics(allActivities);
    }
    
    private static void displayStatistics(List<Activity> activities) {
        StatisticsCalculator statsCalc = new StatisticsCalculator();
        CaloriesCalculator caloriesCalc = new CaloriesCalculator();
        
        for (Activity activity : activities) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Activity: " + activity.getSport());
            System.out.println("=".repeat(50));
            
            // Calculate and display statistics
            double totalTime = statsCalc.calculateTotalTime(activity);
            double totalDistance = statsCalc.calculateTotalDistance(activity);
            double avgSpeed = statsCalc.calculateAverageSpeed(activity);
            double avgHeartRate = statsCalc.calculateAverageHeartRate(activity);
            double avgPace = statsCalc.calculateAveragePace(activity);
            
            System.out.printf("Total Time: %s%n", formatTime(totalTime));
            
            if (totalDistance > 0) {
                System.out.printf("Total Distance: %.2f km%n", totalDistance / 1000);
                
                if (activity.getSport().toLowerCase().contains("run") || 
                    activity.getSport().toLowerCase().contains("walk")) {
                    System.out.printf("Avg Pace: %.2f min/km%n", avgPace);
                } else {
                    System.out.printf("Avg Speed: %.2f km/h%n", avgSpeed);
                }
            }
            
            if (avgHeartRate > 0) {
                System.out.printf("Avg Heart Rate: %.0f bpm%n", avgHeartRate);
            }
            
            // Calculate calories if weight is provided
            if (weight > 0) {
                double calories = caloriesCalc.calculateCaloriesSimple(activity, weight);
                System.out.printf("Calories burned: %.0f kcal%n", calories);
            }
        }
        
        if (activities.isEmpty()) {
            System.out.println("No activities found in the provided files.");
        }
    }
    
    private static String formatTime(double seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
    
    private static void printUsage() {
        System.out.println("Usage: java -jar coach.jar [options] <file1.tcx> [file2.tcx ...]");
        System.out.println("\nOptions:");
        System.out.println("  -w <weight>   Weight in kg (for calorie calculation)");
        System.out.println("\nExamples:");
        System.out.println("  java -jar coach.jar activity.tcx");
        System.out.println("  java -jar coach.jar -w 70.5 run1.tcx run2.tcx");
    }
}