package gr.hua.coach;

import gr.hua.coach.UI.UIProvider;
import gr.hua.coach.UI.IUI;
import gr.hua.coach.model.Activity;
import gr.hua.coach.parser.TCXParser;
import java.util.*;
import java.io.File;

public class Main {
    /*
    AM: it2024028
    AM: it2024100
    */
    private static int weight = -1;
    
    public static void main(String[] args) {
        UIProvider uiProvider = new UIProvider();
        IUI ui = uiProvider.get();

        
        if (args.length == 0) {
            ui.showUsage();
            return;
        }
        
        List<String> tcxFiles = parseCommandLineArguments(args);
        
        if (tcxFiles.isEmpty()) {
            ui.showUsage();
            return;
        }
        
        
        List<Activity> activities = parseActivities(tcxFiles);

        ui.displayStatistics(activities, weight);
    }
    
    private static List<String> parseCommandLineArguments(String[] args) {
        List<String> tcxFiles = new ArrayList<>();
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-w") && i + 1 < args.length) {
                try {

                    weight = Integer.parseInt(args[i + 1]);
                    i++; 

                } catch (NumberFormatException e) {

                    System.err.println("Error: Weight must be a number");
                    System.exit(1);
                    
                }
            } else if (args[i].endsWith(".tcx")) {
                tcxFiles.add(args[i]);
            }
        }
        
        return tcxFiles;
    }
    
    private static List<Activity> parseActivities(List<String> tcxFilePaths) {
        List<Activity> allActivities = new ArrayList<>();
        TCXParser parser = new TCXParser();
        
        for (String filePath : tcxFilePaths) {
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
        
        return allActivities;
    }
}