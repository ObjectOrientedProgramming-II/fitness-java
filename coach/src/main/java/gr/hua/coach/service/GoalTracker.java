// Save as: src/main/java/gr/hua/coach/service/GoalTracker.java
package gr.hua.coach.service;

import gr.hua.coach.event.Events;

import java.time.LocalDate;
import java.util.*;

/**
 * Tracks daily calorie goals - simple version
 */
public class GoalTracker {
    
    private double dailyGoal = 0;
    private final Events events = Events.get();
    
    // Store calories per day
    private final Map<LocalDate, Double> dailyCalories = new HashMap<>();
    
    public void setDailyGoal(double goal) {
        this.dailyGoal = goal;
        events.fire(new Events.GoalSet(goal));
    }
    
    public double getDailyGoal() {
        return dailyGoal;
    }
    
    public boolean hasGoal() {
        return dailyGoal > 0;
    }
    
    public void recordCalories(LocalDate date, double calories) {
        dailyCalories.put(date, dailyCalories.getOrDefault(date, 0.0) + calories);
    }
    
    public void clearCalories() {
        dailyCalories.clear();
    }
    
    public double getRemainingForToday() {
        if (!hasGoal()) return 0;
        
        LocalDate today = LocalDate.now();
        double todayCalories = dailyCalories.getOrDefault(today, 0.0);
        return Math.max(0, dailyGoal - todayCalories);
    }
    
    public String generateReport() {
        if (!hasGoal()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("           DAILY GOAL REPORT\n");
        sb.append("═══════════════════════════════════════════\n");
        sb.append(String.format("Daily Goal: %.0f kcal\n", dailyGoal));
        
        if (dailyCalories.isEmpty()) {
            sb.append("No activity data recorded yet.\n");
            return sb.toString();
        }
        
        int achieved = 0;
        int total = dailyCalories.size();
        
        // Today's status
        LocalDate today = LocalDate.now();
        if (dailyCalories.containsKey(today)) {
            double todayCal = dailyCalories.get(today);
            sb.append("\n📅 TODAY:\n");
            sb.append(String.format("   %s\n", today));
            sb.append(String.format("   Calories: %.0f / %.0f kcal\n", todayCal, dailyGoal));
            if (todayCal >= dailyGoal) {
                sb.append("   ✓ Goal Achieved!\n");
                achieved++;
            } else {
                sb.append(String.format("   ✗ Remaining: %.0f kcal\n", dailyGoal - todayCal));
            }
        }
        
        // History
        sb.append("\nHISTORY:\n");
        sb.append("───────────────────────────────────────────\n");
        
        dailyCalories.entrySet().stream()
            .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
            .forEach(entry -> {
                LocalDate date = entry.getKey();
                double cal = entry.getValue();
                boolean goalAchieved = cal >= dailyGoal;
                
                String status = goalAchieved ? "✓" : "✗";
                sb.append(String.format("%s %s: %.0f/%.0f kcal", 
                    status, date, cal, dailyGoal));
                
                if (!goalAchieved) {
                    sb.append(String.format(" (-%.0f)", dailyGoal - cal));
                }
                sb.append("\n");
            });
        
        // Count achievements (excluding today if already counted)
        for (Map.Entry<LocalDate, Double> entry : dailyCalories.entrySet()) {
            if (!entry.getKey().equals(today) && entry.getValue() >= dailyGoal) {
                achieved++;
            }
        }
        
        sb.append("───────────────────────────────────────────\n");
        sb.append(String.format("Total Days: %d\n", total));
        sb.append(String.format("Achieved: %d (%.1f%%)\n", 
            achieved, total > 0 ? (achieved * 100.0 / total) : 0));
        
        return sb.toString();
    }
}