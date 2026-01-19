package gr.hua.coach.UI;

import gr.hua.coach.event.Events;
import gr.hua.coach.model.Activity;
import gr.hua.coach.service.*;
import gr.hua.coach.state.AppState;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class GUI extends Application {
    
    private final AppState state = new AppState();
    private final ActivityManager activities = new ActivityManager(state);
    private final StatsService stats = new StatsService(state);
    private final FileLoader fileLoader = new FileLoader();
    private final Events events = Events.get();
    
    private ListView<String> activityListView;
    private TextArea statsTextArea;
    private TextField weightField, ageField, goalField, rhrField;
    private ComboBox<String> genderCombo;
    private CheckBox advancedCheck, zoneAnalysisCheck, vo2maxCheck;
    private Label goalLabel, stateLabel;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        subscribeToEvents();
        
        stage.setTitle("Fitness Coach - Clean Architecture");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        root.setTop(createMenu(stage));
        root.setLeft(createActivityPanel(stage));
        root.setCenter(createStatsPanel());
        root.setRight(createProfilePanel());
        root.setBottom(createStatusBar());
        
        stage.setScene(new Scene(root, 1400, 750));
        stage.show();
    }
    
    private void subscribeToEvents() {
        events.on(Events.ActivitiesLoaded.class, e -> Platform.runLater(this::refreshList));
        events.on(Events.ActivityAdded.class, e -> Platform.runLater(this::refreshList));
        events.on(Events.ActivitiesCleared.class, e -> Platform.runLater(this::refreshList));
        
        events.on(Events.StatsComputed.class, e -> {
            Platform.runLater(() -> {
                statsTextArea.setText(e.statsText);
                updateGoalLabel();
            });
        });
        
        state.setListener(newState -> 
            Platform.runLater(() -> stateLabel.setText("State: " + newState)));
    }
    
    private MenuBar createMenu(Stage stage) {
        MenuBar bar = new MenuBar();
        Menu file = new Menu("File");
        
        MenuItem load = new MenuItem("Load TCX Files...");
        load.setOnAction(e -> loadFiles(stage));
        
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> Platform.exit());
        
        file.getItems().addAll(load, exit);
        bar.getMenus().add(file);
        return bar;
    }
    
    private VBox createActivityPanel(Stage stage) {
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
        
        Button addBtn = new Button("Add Manual Activity");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addManualActivity());
        
        Button clearBtn = new Button("Clear All");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> clearActivities());
        
        box.getChildren().addAll(title, activityListView, loadBtn, addBtn, clearBtn);
        return box;
    }
    
    private VBox createStatsPanel() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        Label title = new Label("Statistics");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        statsTextArea = new TextArea("Load activities to view statistics.");
        statsTextArea.setEditable(false);
        statsTextArea.setStyle("-fx-font-family: 'Courier New';");
        
        Button refreshBtn = new Button("Refresh Statistics");
        refreshBtn.setOnAction(e -> computeStats());
        
        VBox.setVgrow(statsTextArea, Priority.ALWAYS);
        box.getChildren().addAll(title, statsTextArea, refreshBtn);
        return box;
    }
    
    private VBox createProfilePanel() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        box.setPrefWidth(320);
        
        Label profileTitle = new Label("User Profile");
        profileTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        weightField = new TextField();
        weightField.setPromptText("70");
        ageField = new TextField();
        ageField.setPromptText("25");
        rhrField = new TextField();
        rhrField.setPromptText("60");
        genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female");
        genderCombo.setValue("Male");
        
        grid.add(new Label("Weight (kg):"), 0, 0);
        grid.add(weightField, 1, 0);
        grid.add(new Label("Age:"), 0, 1);
        grid.add(ageField, 1, 1);
        grid.add(new Label("Gender:"), 0, 2);
        grid.add(genderCombo, 1, 2);
        grid.add(new Label("Resting HR:"), 0, 3);
        grid.add(rhrField, 1, 3);
        
        Button saveBtn = new Button("Save Profile");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> saveProfile());
        
        Label calcOptionsTitle = new Label("Calorie Calculation Options");
        calcOptionsTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        
        advancedCheck = new CheckBox("Use Advanced (HR-based)");
        advancedCheck.setWrapText(true);
        advancedCheck.setOnAction(e -> {
            if (advancedCheck.isSelected()) {
                zoneAnalysisCheck.setSelected(false);
                vo2maxCheck.setSelected(false);
            }
            saveProfile();
        });
        
        zoneAnalysisCheck = new CheckBox("Use Zone Analysis (Bonus)");
        zoneAnalysisCheck.setWrapText(true);
        zoneAnalysisCheck.setOnAction(e -> {
            if (zoneAnalysisCheck.isSelected()) {
                advancedCheck.setSelected(false);
                vo2maxCheck.setSelected(false);
            }
            saveProfile();
        });
        
        vo2maxCheck = new CheckBox("Use VO2 Max (Bonus)");
        vo2maxCheck.setWrapText(true);
        vo2maxCheck.setOnAction(e -> {
            if (vo2maxCheck.isSelected()) {
                advancedCheck.setSelected(false);
                zoneAnalysisCheck.setSelected(false);
            }
            saveProfile();
        });
        
        Label note = new Label("Note: Resting HR required for VO2 Max");
        note.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        note.setWrapText(true);
        
        Label goalTitle = new Label("Daily Goal");
        goalTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox goalBox = new HBox(5);
        goalField = new TextField();
        goalField.setPromptText("500");
        goalField.setPrefWidth(100);
        Button setGoalBtn = new Button("Set");
        setGoalBtn.setOnAction(e -> setGoal());
        goalBox.getChildren().addAll(goalField, setGoalBtn);
        
        goalLabel = new Label("No goal set");
        goalLabel.setWrapText(true);
        goalLabel.setStyle("-fx-font-size: 12px;");
        
        box.getChildren().addAll(
            profileTitle, grid, saveBtn,
            new Separator(),
            calcOptionsTitle,
            advancedCheck,
            zoneAnalysisCheck,
            vo2maxCheck,
            note,
            new Separator(),
            goalTitle, goalBox, goalLabel
        );
        
        return box;
    }
    
    private HBox createStatusBar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(5, 10, 5, 10));
        bar.setStyle("-fx-background-color: #f0f0f0;");
        
        stateLabel = new Label("State: EMPTY");
        stateLabel.setStyle("-fx-font-weight: bold;");
        
        bar.getChildren().add(stateLabel);
        return bar;
    }
        
    private void loadFiles(Stage stage) {
        if (!state.canLoadFiles()) {
            showError("Cannot load files right now");
            return;
        }
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Select TCX Files");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("TCX Files", "*.tcx")
        );
        
        List<File> files = fc.showOpenMultipleDialog(stage);
        if (files == null || files.isEmpty()) return;
        
        fileLoader.loadAsync(files).thenAccept(result -> {
            Platform.runLater(() -> {
                if (!result.activities.isEmpty()) {
                    activities.addAll(result.activities);
                    showInfo("Loaded " + result.activities.size() + " activities");
                    computeStats();
                }
                if (result.hasErrors()) {
                    showWarning("Some files had errors:\n" + 
                        String.join("\n", result.errors));
                }
            });
        });
    }
    
    private void addManualActivity() {
        Dialog<ActivityData> dialog = new Dialog<>();
        dialog.setTitle("Add Activity");
        
        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<String> sportCombo = new ComboBox<>();
        sportCombo.getItems().addAll("Running", "Cycling", "Walking", "Swimming");
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
        grid.add(new Label("Heart Rate:"), 0, 3);
        grid.add(hrField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn == addBtn) {
                try {
                    return new ActivityData(
                        sportCombo.getValue(),
                        parseDouble(durationField.getText()),
                        parseDouble(distanceField.getText()),
                        parseInt(hrField.getText())
                    );
                } catch (Exception e) {
                    showError("Invalid input");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(data -> {
            activities.add(createActivity(data));
            computeStats();
        });
    }
    
    private void clearActivities() {
        try {
            activities.clear();
            statsTextArea.setText("No activities loaded.");
            goalLabel.setText("No goal set");
        } catch (IllegalStateException e) {
            showError(e.getMessage());
        }
    }
    
    private void saveProfile() {
        try {
            double weight = parseDouble(weightField.getText());
            int age = parseInt(ageField.getText());
            String gender = genderCombo.getValue();
            boolean advanced = advancedCheck.isSelected();
            int rhr = parseInt(rhrField.getText());
            
            stats.setProfile(weight, age, gender, advanced);
            
            if (rhr > 0) {
                stats.setRestingHeartRate(rhr);
            }
            
            stats.setUseZoneAnalysis(zoneAnalysisCheck.isSelected());
            stats.setUseVO2Max(vo2maxCheck.isSelected());
            
            if (!activities.isEmpty()) {
                computeStats();
            }
            
            showInfo("Profile saved");
        } catch (Exception e) {
            showError("Invalid input: " + e.getMessage());
        }
    }
    
    private void computeStats() {
        if (activities.isEmpty()) {
            statsTextArea.setText("No activities to analyze.");
            return;
        }
        
        new Thread(() -> {
            try {
                String result = stats.compute(activities.getAll());
                Platform.runLater(() -> statsTextArea.setText(result));
            } catch (Exception e) {
                Platform.runLater(() -> showError("Stats error: " + e.getMessage()));
            }
        }).start();
    }
    
    private void setGoal() {
        try {
            double goal = parseDouble(goalField.getText());
            stats.getGoalTracker().setDailyGoal(goal);
            
            updateGoalLabel();
            
            if (!activities.isEmpty()) {
                computeStats();
            }
            
            showInfo("Daily goal set to " + (int)goal + " kcal");
            
        } catch (Exception e) {
            showError("Invalid goal");
        }
    }
    
    private void updateGoalLabel() {
        if (!stats.getGoalTracker().hasGoal()) {
            goalLabel.setText("No goal set");
            return;
        }
        
        double goal = stats.getGoalTracker().getDailyGoal();
        double remaining = stats.getGoalTracker().getRemainingForToday();
        
        StringBuilder text = new StringBuilder();
        text.append(String.format("Goal: %.0f kcal\n", goal));
        
        if (remaining > 0) {
            text.append(String.format("Remaining today: %.0f kcal", remaining));
        } else {
            text.append("✓ Goal achieved today!");
        }
        
        goalLabel.setText(text.toString());
    }
    
    /*helpers */    
    private void refreshList() {
        activityListView.getItems().clear();
        List<Activity> acts = activities.getAll();
        for (int i = 0; i < acts.size(); i++) {
            Activity a = acts.get(i);
            activityListView.getItems().add(
                String.format("[%d] %s - %s", i + 1, a.getSport(),
                    a.getStartTime() != null ? a.getStartTime().toLocalDate() : "Unknown")
            );
        }
    }
    
    private Activity createActivity(ActivityData data) {
        Activity a = new Activity();
        a.setSport(data.sport);
        a.setStartTime(LocalDateTime.now());
        
        gr.hua.coach.model.Lap lap = new gr.hua.coach.model.Lap();
        lap.setStartTime(LocalDateTime.now());
        
        gr.hua.coach.model.Track track = new gr.hua.coach.model.Track();
        
        gr.hua.coach.model.TrackPoint start = new gr.hua.coach.model.TrackPoint();
        start.setTime(LocalDateTime.now());
        start.setDistance(0.0);
        if (data.hr > 0) start.setHeartRate(data.hr);
        
        gr.hua.coach.model.TrackPoint end = new gr.hua.coach.model.TrackPoint();
        end.setTime(LocalDateTime.now().plusMinutes((long)data.duration));
        end.setDistance(data.distance * 1000);
        if (data.hr > 0) end.setHeartRate(data.hr);
        
        track.addTrackPoint(start);
        track.addTrackPoint(end);
        lap.addTrack(track);
        a.addLap(lap);
        
        return a;
    }
    
    private double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Double.parseDouble(s.trim());
    }
    
    private int parseInt(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Integer.parseInt(s.trim());
    }
    
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    private static class ActivityData {
        final String sport;
        final double duration, distance;
        final int hr;
        
        ActivityData(String sport, double duration, double distance, int hr) {
            this.sport = sport;
            this.duration = duration;
            this.distance = distance;
            this.hr = hr;
        }
    }
}