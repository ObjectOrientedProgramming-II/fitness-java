package gr.hua.coach.calculator;

public class VO2MaxCalculator {
    
    public enum FitnessLevel {
        EXCELLENT("Excellent"),
        GOOD("Good"),
        AVERAGE("Average"),
        BELOW_AVERAGE("Below Average"),
        POOR("Poor");
        
        private final String description;
        
        FitnessLevel(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    private final int age;
    private final String gender;
    private final int restingHeartRate;
    
    public VO2MaxCalculator(int age, String gender, int restingHeartRate) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        if (restingHeartRate <= 0) {
            throw new IllegalArgumentException("Resting heart rate must be positive");
        }
        if (gender == null || (!gender.equals("Male") && !gender.equals("Female"))) {
            throw new IllegalArgumentException("Gender must be 'Male' or 'Female'");
        }
        
        this.age = age;
        this.gender = gender;
        this.restingHeartRate = restingHeartRate;
    }
    
    public double calculateMaxHeartRate() {
        return 220 - age;
    }
    
    public double calculateVO2Max() {
        double mhr = calculateMaxHeartRate();
        return 15.3 * (mhr / restingHeartRate);
    }
    
    public FitnessLevel evaluateVO2Max(double vo2max) {
        if (gender.equals("Male")) {
            return evaluateMale(vo2max);
        } else {
            return evaluateFemale(vo2max);
        }
    }
    
    private FitnessLevel evaluateMale(double vo2max) {
        if (age < 30) {
            if (vo2max >= 55) return FitnessLevel.EXCELLENT;
            if (vo2max >= 48) return FitnessLevel.GOOD;
            if (vo2max >= 42) return FitnessLevel.AVERAGE;
            if (vo2max >= 36) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else if (age < 40) {
            if (vo2max >= 52) return FitnessLevel.EXCELLENT;
            if (vo2max >= 45) return FitnessLevel.GOOD;
            if (vo2max >= 39) return FitnessLevel.AVERAGE;
            if (vo2max >= 33) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else if (age < 50) {
            if (vo2max >= 49) return FitnessLevel.EXCELLENT;
            if (vo2max >= 42) return FitnessLevel.GOOD;
            if (vo2max >= 36) return FitnessLevel.AVERAGE;
            if (vo2max >= 30) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else if (age < 60) {
            if (vo2max >= 45) return FitnessLevel.EXCELLENT;
            if (vo2max >= 38) return FitnessLevel.GOOD;
            if (vo2max >= 33) return FitnessLevel.AVERAGE;
            if (vo2max >= 27) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else {
            if (vo2max >= 41) return FitnessLevel.EXCELLENT;
            if (vo2max >= 35) return FitnessLevel.GOOD;
            if (vo2max >= 29) return FitnessLevel.AVERAGE;
            if (vo2max >= 24) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        }
    }
    
    private FitnessLevel evaluateFemale(double vo2max) {
        if (age < 30) {
            if (vo2max >= 49) return FitnessLevel.EXCELLENT;
            if (vo2max >= 42) return FitnessLevel.GOOD;
            if (vo2max >= 36) return FitnessLevel.AVERAGE;
            if (vo2max >= 30) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else if (age < 40) {
            if (vo2max >= 45) return FitnessLevel.EXCELLENT;
            if (vo2max >= 38) return FitnessLevel.GOOD;
            if (vo2max >= 33) return FitnessLevel.AVERAGE;
            if (vo2max >= 27) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else if (age < 50) {
            if (vo2max >= 42) return FitnessLevel.EXCELLENT;
            if (vo2max >= 35) return FitnessLevel.GOOD;
            if (vo2max >= 30) return FitnessLevel.AVERAGE;
            if (vo2max >= 24) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else if (age < 60) {
            if (vo2max >= 38) return FitnessLevel.EXCELLENT;
            if (vo2max >= 32) return FitnessLevel.GOOD;
            if (vo2max >= 27) return FitnessLevel.AVERAGE;
            if (vo2max >= 21) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        } else {
            if (vo2max >= 35) return FitnessLevel.EXCELLENT;
            if (vo2max >= 29) return FitnessLevel.GOOD;
            if (vo2max >= 24) return FitnessLevel.AVERAGE;
            if (vo2max >= 19) return FitnessLevel.BELOW_AVERAGE;
            return FitnessLevel.POOR;
        }
    }
    
    public double calculateCaloriesFromVO2Max(double vo2max, double weight, double timeMinutes) {
        return vo2max * weight * timeMinutes / 200.0;
    }
    
    public VO2MaxReport generateReport() {
        double mhr = calculateMaxHeartRate();
        double vo2max = calculateVO2Max();
        FitnessLevel level = evaluateVO2Max(vo2max);
        
        return new VO2MaxReport(age, gender, restingHeartRate, mhr, vo2max, level);
    }
    
    public static class VO2MaxReport {
        private final int age;
        private final String gender;
        private final int restingHeartRate;
        private final double maxHeartRate;
        private final double vo2max;
        private final FitnessLevel fitnessLevel;
        
        public VO2MaxReport(int age, String gender, int restingHeartRate,
                           double maxHeartRate, double vo2max, FitnessLevel fitnessLevel) {
            this.age = age;
            this.gender = gender;
            this.restingHeartRate = restingHeartRate;
            this.maxHeartRate = maxHeartRate;
            this.vo2max = vo2max;
            this.fitnessLevel = fitnessLevel;
        }
        
        public int getAge() { return age; }
        public String getGender() { return gender; }
        public int getRestingHeartRate() { return restingHeartRate; }
        public double getMaxHeartRate() { return maxHeartRate; }
        public double getVO2Max() { return vo2max; }
        public FitnessLevel getFitnessLevel() { return fitnessLevel; }
        
        public String format() {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append("           VO2 MAX ASSESSMENT\n");
            sb.append("═══════════════════════════════════════════════════\n");
            sb.append(String.format("Age: %d years\n", age));
            sb.append(String.format("Gender: %s\n", gender));
            sb.append(String.format("Resting Heart Rate: %d bpm\n", restingHeartRate));
            sb.append(String.format("Maximum Heart Rate: %.0f bpm\n\n", maxHeartRate));
            
            sb.append(String.format("VO2 Max: %.1f ml/kg/min\n", vo2max));
            sb.append(String.format("Fitness Level: %s\n\n", fitnessLevel.getDescription()));
            
            sb.append("What this means:\n");
            sb.append(getFitnessLevelExplanation());
            sb.append("\n═══════════════════════════════════════════════════\n");
            
            return sb.toString();
        }
        
        private String getFitnessLevelExplanation() {
            switch (fitnessLevel) {
                case EXCELLENT:
                    return "  Outstanding aerobic fitness! You're in the top tier\n" +
                           "  for your age and gender. Keep up the excellent work!";
                case GOOD:
                    return "  Above average fitness level. You have good cardiovascular\n" +
                           "  health and endurance capacity.";
                case AVERAGE:
                    return "  Average fitness for your age and gender. There's room\n" +
                           "  for improvement with consistent training.";
                case BELOW_AVERAGE:
                    return "  Below average fitness. Consider increasing your aerobic\n" +
                           "  exercise to improve cardiovascular health.";
                case POOR:
                    return "  Low fitness level. Consult with a healthcare professional\n" +
                           "  and gradually increase physical activity.";
                default:
                    return "";
            }
        }
    }
}