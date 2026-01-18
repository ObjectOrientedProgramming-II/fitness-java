package gr.hua.coach.UI;

import gr.hua.coach.calculator.CaloriesCalculator;
import gr.hua.coach.calculator.StatisticsCalculator;
import gr.hua.coach.model.Activity;
import gr.hua.coach.service.StatsService;
import gr.hua.coach.state.AppState;

import java.util.List;

/**
 * Command-line interface for displaying activity statistics.
 * Uses services for calculations, handles only output formatting.
 */
public class CLI implements IUI {
    
    private final StatsService statsService;
    
    public CLI() {
        // Create lightweight state just for CLI
        AppState state = new AppState();
        this.statsService = new StatsService(state);
    }
    
    @Override
    public void displayStatistics(List<Activity> activities, int weight) {
        if (activities.isEmpty()) {
            System.out.println("No activities found in the provided files.");
            return;
        }
        
        // Set profile if weight provided
        if (weight > 0) {
            statsService.setProfile(weight, 0, "Male", false);
        }
        
        // Use calculator directly for CLI (simpler)
        StatisticsCalculator statsCalc = new StatisticsCalculator();
        CaloriesCalculator caloriesCalc = new CaloriesCalculator();
        
        for (Activity activity : activities) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Activity: " + activity.getSport());
            System.out.println("=".repeat(50));
            
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
            
            if (weight > 0) {
                double calories = caloriesCalc.calculateCaloriesSimple(activity, weight);
                System.out.printf("Calories burned: %.0f kcal%n", calories);
            }
        }
    }
    
    @Override
    public void showUsage() {
        System.out.println("Fitness Activity Analyzer");
        System.out.println("==========================");
        System.out.println("Usage: java -jar fitness-coach.jar [options] <file1.tcx> [file2.tcx ...]");
        System.out.println("\nOptions:");
        System.out.println("  -w <weight>   Weight in kg (for calorie calculation)");
        System.out.println("  --gui, -g     Launch graphical interface");
        System.out.println("\nExamples:");
        System.out.println("  java -jar fitness-coach.jar activity.tcx");
        System.out.println("  java -jar fitness-coach.jar -w 70 run1.tcx run2.tcx");
        System.out.println("  java -jar fitness-coach.jar --gui");
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
}