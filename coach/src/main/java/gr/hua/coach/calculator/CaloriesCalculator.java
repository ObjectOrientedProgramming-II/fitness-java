package gr.hua.coach.calculator;

import gr.hua.coach.model.Activity;

public class CaloriesCalculator {
    private static final double RUNNING_MULTIPLIER = 10.0;
    private static final double CYCLING_MULTIPLIER = 8.0;
    private static final double WALKING_MULTIPLIER = 5.0;
    private static final double SWIMMING_MULTIPLIER = 12.0;
    private static final double DEFAULT_MULTIPLIER = 7.0;
    
    public double calculateCaloriesSimple(Activity activity, double weight) {
        double multiplier = getMultiplierForSport(activity.getSport());
        double totalTimeHours = calculateTotalTimeHours(activity);
        
        return multiplier * weight * totalTimeHours;
    }
    
    private double getMultiplierForSport(String sport) {
        if (sport == null) {
            return DEFAULT_MULTIPLIER;
        }
        
        String sportLower = sport.toLowerCase();
        
        if (sportLower.contains("run")) {
            return RUNNING_MULTIPLIER;
        } else if (sportLower.contains("cycl") || sportLower.contains("bik")) {
            return CYCLING_MULTIPLIER;
        } else if (sportLower.contains("walk")) {
            return WALKING_MULTIPLIER;
        } else if (sportLower.contains("swim")) {
            return SWIMMING_MULTIPLIER;
        } else {
            return DEFAULT_MULTIPLIER;
        }
    }
    
    private double calculateTotalTimeHours(Activity activity) {
        StatisticsCalculator statsCalc = new StatisticsCalculator();
        double totalSeconds = statsCalc.calculateTotalTime(activity);
        return totalSeconds / 3600.0;
    }
}