package gr.hua.coach.service;

import gr.hua.coach.calculator.*;
import gr.hua.coach.event.Events;
import gr.hua.coach.model.Activity;
import gr.hua.coach.state.AppState;

import java.util.List;

public class StatsService {
    
    private final StatisticsCalculator statsCalc = new StatisticsCalculator();
    private final CaloriesCalculator calCalc = new CaloriesCalculator();
    private final AppState state;
    private final Events events = Events.get();
    private final GoalTracker goalTracker = new GoalTracker();
    
    private double weight = 0;
    private int age = 0;
    private String gender = "Male";
    private boolean useAdvanced = false;
    
    public StatsService(AppState state) {
        this.state = state;
    }
    
    public void setProfile(double weight, int age, String gender, boolean useAdvanced) {
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        this.useAdvanced = useAdvanced;
        events.fire(new Events.ProfileUpdated(weight, age));
    }
    
    public GoalTracker getGoalTracker() {
        return goalTracker;
    }
    
    public String compute(List<Activity> activities) {
        if (!state.canCompute()) {
            throw new IllegalStateException("Cannot compute stats now");
        }
        
        state.transitionTo(AppState.State.COMPUTING);
        
        try {
            String result = formatStats(activities);
            events.fire(new Events.StatsComputed(result));
            state.transitionTo(AppState.State.HAS_DATA);
            return result;
        } catch (Exception e) {
            state.transitionTo(AppState.State.ERROR);
            throw e;
        }
    }
    
    private String formatStats(List<Activity> activities) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("              ACTIVITY STATISTICS\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        double totalCal = 0, totalTime = 0, totalDist = 0;
        
        goalTracker.clearCalories();
        
        for (int i = 0; i < activities.size(); i++) {
            Activity a = activities.get(i);
            
            double time = statsCalc.calculateTotalTime(a);
            double dist = statsCalc.calculateTotalDistance(a);
            double speed = statsCalc.calculateAverageSpeed(a);
            double pace = statsCalc.calculateAveragePace(a);
            double hr = statsCalc.calculateAverageHeartRate(a);
            double cal = calculateCalories(a, hr);
            
            if (a.getStartTime() != null && cal > 0) {
                goalTracker.recordCalories(a.getStartTime().toLocalDate(), cal);
            }
            
            sb.append(String.format("Activity #%d: %s\n", i + 1, a.getSport()));
            sb.append("─────────────────────────────────────────────────\n");
            sb.append(String.format("  Time:       %s\n", formatTime(time)));
            
            if (dist > 0) {
                sb.append(String.format("  Distance:   %.2f km\n", dist / 1000));
                if (a.getSport().toLowerCase().contains("run") || 
                    a.getSport().toLowerCase().contains("walk")) {
                    sb.append(String.format("  Pace:       %.2f min/km\n", pace));
                } else {
                    sb.append(String.format("  Speed:      %.2f km/h\n", speed));
                }
            }
            
            if (hr > 0) {
                sb.append(String.format("  Heart Rate: %.0f bpm\n", hr));
            }
            
            if (cal > 0) {
                sb.append(String.format("  Calories:   %.0f kcal\n", cal));
                totalCal += cal;
            }
            
            totalTime += time;
            totalDist += dist;
            sb.append("\n");
        }
        
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("                    SUMMARY\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append(String.format("Total Activities:  %d\n", activities.size()));
        sb.append(String.format("Total Time:        %s\n", formatTime(totalTime)));
        sb.append(String.format("Total Distance:    %.2f km\n", totalDist / 1000));
        sb.append(String.format("Total Calories:    %.0f kcal\n", totalCal));
        
        if (goalTracker.hasGoal()) {
            sb.append("\n");
            sb.append(goalTracker.generateReport());
        }
        
        return sb.toString();
    }
    
    private double calculateCalories(Activity activity, double hr) {
        if (weight <= 0) return 0;
        
        if (!useAdvanced || hr <= 0 || age <= 0) {
            return calCalc.calculateCaloriesSimple(activity, weight);
        }
        
        double minutes = statsCalc.calculateTotalTime(activity) / 60.0;
        return calCalc.calculateCaloriesAdvanced(weight, hr, age, minutes, gender);
    }
    
    private String formatTime(double seconds) {
        int h = (int)(seconds / 3600);
        int m = (int)((seconds % 3600) / 60);
        int s = (int)(seconds % 60);
        return h > 0 ? String.format("%02d:%02d:%02d", h, m, s) : 
                      String.format("%02d:%02d", m, s);
    }
    
    public double getWeight() { return weight; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public boolean isUseAdvanced() { return useAdvanced; }
}