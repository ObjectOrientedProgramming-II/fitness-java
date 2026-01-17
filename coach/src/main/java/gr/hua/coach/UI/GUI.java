package gr.hua.coach.UI;

import gr.hua.coach.calculator.CaloriesCalculator;
import gr.hua.coach.calculator.StatisticsCalculator;
import gr.hua.coach.model.Activity;
import gr.hua.coach.parser.TCXParser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class GUI extends Application implements IUI {
    
    private List<Activity> activities = new ArrayList<>();
    private double userWeight = 0;
    private int userAge = 0;
    private String userGender = "Male";
    private boolean useAdvancedCalories = false;
    
    private ListView<String> activityListView;
    private TextArea statsTextArea;
    private TextField weightField;
    private TextField ageField;
    private TextField dailyGoalField;
    private ComboBox<String> genderComboBox;
    private CheckBox advancedCaloriesCheckBox;
    private Label goalStatusLabel;
    
    private StatisticsCalculator statsCalc = new StatisticsCalculator();
    private CaloriesCalculator caloriesCalc = new CaloriesCalculator();
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fitness Activity Coach");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        root.setTop(createMenuBar(primaryStage));
        root.setLeft(createLeftPanel(primaryStage));
        root.setCenter(createCenterPanel());
        root.setRight(createRightPanel());
        
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        
        MenuItem loadItem = new MenuItem("Load TCX Files...");
        loadItem.setOnAction(e -> loadFiles(stage));
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        
        fileMenu.getItems().addAll(loadItem, exitItem);
        menuBar.getMenus().add(fileMenu);
        
        return menuBar;
    }
    
    private VBox createLeftPanel(Stage stage) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(250);
        
        Label title = new Label("Activities");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        activityListView = new ListView<>();
        activityListView.setPrefHeight(500);
        
        Button loadBtn = new Button("Load TCX Files");
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setOnAction(e -> loadFiles(stage));
        
        Button addBtn = new Button("Add Activity");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addActivity());
        
        Button clearBtn = new Button("Clear All");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> {
            activities.clear();
            refreshAll();
        });
        
        box.getChildren().addAll(title, activityListView, loadBtn, addBtn, clearBtn);
        return box;
    }
    
    private VBox createCenterPanel() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        Label title = new Label("Statistics");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        statsTextArea = new TextArea("Load activities to view statistics.");
        statsTextArea.setEditable(false);
        statsTextArea.setStyle("-fx-font-family: 'Courier New';");
        
        Button refreshBtn = new Button("Refresh Statistics");
        refreshBtn.setOnAction(e -> refreshStats());
        
        VBox.setVgrow(statsTextArea, Priority.ALWAYS);
        box.getChildren().addAll(title, statsTextArea, refreshBtn);
        
        return box;
    }
    
    private VBox createRightPanel() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        box.setPrefWidth(280);
        
        Label profileTitle = new Label("User Profile");
        profileTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        weightField = new TextField();
        weightField.setPromptText("70");
        
        ageField = new TextField();
        ageField.setPromptText("25");
        
        genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female");
        genderComboBox.setValue("Male");
        
        grid.add(new Label("Weight (kg):"), 0, 0);
        grid.add(weightField, 1, 0);
        grid.add(new Label("Age:"), 0, 1);
        grid.add(ageField, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(genderComboBox, 1, 2);
        
        Button saveBtn = new Button("Save Profile");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> saveProfile());
        
        advancedCaloriesCheckBox = new CheckBox("Use Advanced Calorie Calculation (Heart Rate)");
        advancedCaloriesCheckBox.setWrapText(true);
        advancedCaloriesCheckBox.setOnAction(e -> {
            useAdvancedCalories = advancedCaloriesCheckBox.isSelected();
            refreshStats();
        });
        
        Separator sep = new Separator();
        
        Label goalTitle = new Label("Daily Goal");
        goalTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox goalBox = new HBox(5);
        dailyGoalField = new TextField();
        dailyGoalField.setPromptText("500");
        dailyGoalField.setPrefWidth(100);
        
        Button setGoalBtn = new Button("Set");
        setGoalBtn.setOnAction(e -> updateGoal());
        
        goalBox.getChildren().addAll(dailyGoalField, setGoalBtn);
        
        goalStatusLabel = new Label("No goal set");
        goalStatusLabel.setWrapText(true);
        
        box.getChildren().addAll(
            profileTitle, grid, saveBtn,
            new Separator(),
            advancedCaloriesCheckBox,
            sep,
            goalTitle, goalBox, goalStatusLabel
        );
        
        return box;
    }
    
    private void loadFiles(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select TCX Files");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TCX Files", "*.tcx"));
        
        List<File> files = fc.showOpenMultipleDialog(stage);
        if (files == null || files.isEmpty()) return;
        
        Task<List<Activity>> task = new Task<List<Activity>>() {
            @Override
            protected List<Activity> call() throws Exception {
                List<Activity> loaded = new ArrayList<>();
                TCXParser parser = new TCXParser();
                for (File file : files) {
                    try {
                        loaded.addAll(parser.parse(file));
                    } catch (Exception e) {
                        System.err.println("Error: " + file.getName() + " - " + e.getMessage());
                    }
                }
                return loaded;
            }
        };
        
        task.setOnSucceeded(e -> {
            activities.addAll(task.getValue());
            refreshAll();
            showInfo("Loaded " + task.getValue().size() + " activities from " + files.size() + " file(s)");
        });
        
        task.setOnFailed(e -> showError("Failed to load files"));
        
        new Thread(task).start();
    }
    
    private void addActivity() {
        Dialog<Activity> dialog = new Dialog<>();
        dialog.setTitle("Add Activity");
        
        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<String> sportCombo = new ComboBox<>();
        sportCombo.getItems().addAll("Running", "Cycling", "Walking", "Swimming", "Other");
        sportCombo.setValue("Running");
        
        TextField durationField = new TextField();
        TextField distanceField = new TextField();
        TextField hrField = new TextField();
        
        grid.add(new Label("Sport:"), 0, 0);
        grid.add(sportCombo, 1, 0);
        grid.add(new Label("Duration (min):"), 0, 1);
        grid.add(durationField, 1, 1);
        grid.add(new Label("Distance (km):"), 0, 2);
        grid.add(distanceField, 1, 2);
        grid.add(new Label("Heart Rate (bpm):"), 0, 3);
        grid.add(hrField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                try {
                    double duration = Double.parseDouble(durationField.getText());
                    double distance = distanceField.getText().isEmpty() ? 0 : Double.parseDouble(distanceField.getText());
                    int hr = hrField.getText().isEmpty() ? 0 : Integer.parseInt(hrField.getText());
                    
                    return createActivity(sportCombo.getValue(), duration, distance, hr);
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Invalid input"));
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(activity -> {
            activities.add(activity);
            refreshAll();
        });
    }
    
    private Activity createActivity(String sport, double minutes, double km, int hr) {
        Activity activity = new Activity();
        activity.setSport(sport);
        activity.setStartTime(LocalDateTime.now());
        
        gr.hua.coach.model.Lap lap = new gr.hua.coach.model.Lap();
        lap.setStartTime(LocalDateTime.now());
        
        gr.hua.coach.model.Track track = new gr.hua.coach.model.Track();
        
        gr.hua.coach.model.TrackPoint start = new gr.hua.coach.model.TrackPoint();
        start.setTime(LocalDateTime.now());
        start.setDistance(0.0);
        if (hr > 0) start.setHeartRate(hr);
        
        gr.hua.coach.model.TrackPoint end = new gr.hua.coach.model.TrackPoint();
        end.setTime(LocalDateTime.now().plusMinutes((long)minutes));
        end.setDistance(km * 1000);
        if (hr > 0) end.setHeartRate(hr);
        
        track.addTrackPoint(start);
        track.addTrackPoint(end);
        lap.addTrack(track);
        activity.addLap(lap);
        
        return activity;
    }
    
    private void saveProfile() {
        try {
            if (!weightField.getText().trim().isEmpty()) {
                userWeight = Double.parseDouble(weightField.getText().trim());
            }
            if (!ageField.getText().trim().isEmpty()) {
                userAge = Integer.parseInt(ageField.getText().trim());
            }
            userGender = genderComboBox.getValue();
            
            refreshStats();
            updateGoal();
            showInfo("Profile saved");
        } catch (Exception e) {
            showError("Invalid input");
        }
    }
    
    private void refreshAll() {
        updateActivityList();
        refreshStats();
        updateGoal();
    }
    
    private void updateActivityList() {
        activityListView.getItems().clear();
        for (int i = 0; i < activities.size(); i++) {
            Activity a = activities.get(i);
            activityListView.getItems().add(String.format("[%d] %s - %s", 
                i + 1, a.getSport(), 
                a.getStartTime() != null ? a.getStartTime().toLocalDate() : "Unknown"));
        }
    }
    
    private void refreshStats() {
        if (activities.isEmpty()) {
            statsTextArea.setText("No activities loaded.");
            return;
        }
        
        Task<String> task = new Task<String>() {
            @Override
            protected String call() {
                StringBuilder sb = new StringBuilder();
                sb.append("═══════════════════════════════════════════════════\n");
                sb.append("              ACTIVITY STATISTICS\n");
                sb.append("═══════════════════════════════════════════════════\n\n");
                
                double totalCal = 0, totalTime = 0, totalDist = 0;
                
                for (int i = 0; i < activities.size(); i++) {
                    Activity a = activities.get(i);
                    
                    double time = statsCalc.calculateTotalTime(a);
                    double dist = statsCalc.calculateTotalDistance(a);
                    double speed = statsCalc.calculateAverageSpeed(a);
                    double pace = statsCalc.calculateAveragePace(a);
                    double hr = statsCalc.calculateAverageHeartRate(a);
                    double cal = calculateCalories(a, hr);
                    
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
                
                return sb.toString();
            }
        };
        
        task.setOnSucceeded(e -> statsTextArea.setText(task.getValue()));
        new Thread(task).start();
    }
    
    private double calculateCalories(Activity activity, double hr) {
        if (userWeight <= 0) return 0;
        
        if (!useAdvancedCalories || hr <= 0 || userAge <= 0) {
            return caloriesCalc.calculateCaloriesSimple(activity, userWeight);
        }
        
        double minutes = statsCalc.calculateTotalTime(activity) / 60.0;
        return caloriesCalc.calculateCaloriesAdvanced(minutes, hr, userAge, minutes, userGender);
    }
    
    private void updateGoal() {
        String goalText = dailyGoalField.getText().trim();
        if (goalText.isEmpty()) {
            goalStatusLabel.setText("No goal set");
            return;
        }
        
        try {
            double goal = Double.parseDouble(goalText);
            
            Map<LocalDate, Double> daily = new HashMap<>();
            for (Activity a : activities) {
                if (a.getStartTime() != null) {
                    LocalDate date = a.getStartTime().toLocalDate();
                    double hr = statsCalc.calculateAverageHeartRate(a);
                    double cal = calculateCalories(a, hr);
                    daily.merge(date, cal, Double::sum);
                }
            }
            
            LocalDate today = LocalDate.now();
            double todayCal = daily.getOrDefault(today, 0.0);
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Goal: %.0f kcal\n", goal));
            sb.append(String.format("Today: %.0f kcal\n", todayCal));
            
            if (todayCal >= goal) {
                sb.append("✓ Goal achieved!");
                goalStatusLabel.setStyle("-fx-text-fill: green;");
            } else {
                sb.append(String.format("Remaining: %.0f kcal", goal - todayCal));
                goalStatusLabel.setStyle("-fx-text-fill: orange;");
            }
            
            goalStatusLabel.setText(sb.toString());
            
        } catch (Exception e) {
            showError("Invalid goal value");
        }
    }
    
    private String formatTime(double seconds) {
        int h = (int)(seconds / 3600);
        int m = (int)((seconds % 3600) / 60);
        int s = (int)(seconds % 60);
        return h > 0 ? String.format("%02d:%02d:%02d", h, m, s) : String.format("%02d:%02d", m, s);
    }
    
    private void showInfo(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }
    
    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }
    
    @Override
    public void displayStatistics(List<Activity> activities, int weight) {
        this.activities = activities;
        if (weight > 0) this.userWeight = weight;
    }
    
    @Override
    public void showUsage() {
        // De to xrhsimopoioume sto GUI
    }
}