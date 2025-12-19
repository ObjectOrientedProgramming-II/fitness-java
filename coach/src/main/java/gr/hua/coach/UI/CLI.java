package gr.hua.coach.UI;

import java.util.List;

import gr.hua.coach.calculator.CaloriesCalculator;
import gr.hua.coach.calculator.StatisticsCalculator;
import gr.hua.coach.model.Activity;

public class CLI implements IUI {
    public CLI() {
        
    }

    @Override
    public void displayStatistics(
        List<Activity> activities,
        int weight
    ) {
        StatisticsCalculator statsCalc = new StatisticsCalculator();
        CaloriesCalculator caloriesCalc = new CaloriesCalculator();
        
        for (Activity activity : activities) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Activity: " + activity.getSport());
            System.out.println("=".repeat(50));
            
            // Ypologismos kai emfanisi statistikon
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
            
            // Ypologismos kai emfanisi thermidon an exei dothei varos
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

    public void showUsage() {
        System.out.println("Fitness Activity Analyzer");
        System.out.println("==========================");
        System.out.println("Usage: java -cp target/classes gr.hua.coach.Main [options] <file1.tcx> [file2.tcx ...]");
        System.out.println("\nOptions:");
        System.out.println("  -w <weight>   Weight in kg (for calorie calculation)");
        System.out.println("\nExamples:");
        System.out.println("  java -cp target/classes gr.hua.coach.Main activity.tcx");
        System.out.println("  java -cp target/classes gr.hua.coach.Main -w 70 run1.tcx run2.tcx");
        System.out.println("  java -cp target/classes gr.hua.coach.Main -w 65 *.tcx");
    }
}
