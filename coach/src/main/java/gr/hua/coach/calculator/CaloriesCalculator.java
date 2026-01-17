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

    public double calculateCaloriesAdvanced(
        double weight,
        double hr,
        int age,
        double minutes,
        String gender
    ) {
        if ("Male".equals(gender)) {
            return Math.max(
                (-55.0969 + (0.6309 * hr) + (0.1966 * weight) + (0.2017 * age)) * minutes / 4.184,
                0
            );
        } 
        return Math.max(
            (-20.4022 + (0.4472 * hr) + (0.1263 * weight) + (0.074 * age)) * minutes / 4.184,
            0
        );
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