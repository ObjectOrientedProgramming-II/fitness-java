package gr.hua.coach.UI;

import javafx.application.Application;

public class UIProvider {
    
    /**
     * Returns CLI interface by default for command-line usage
     */
    public IUI get() {
        return new CLI();
    }
    
    /**
     * Launches the GUI application
     * Call this method when you want to start the GUI mode
     */
    public static void launchGUI(String[] args) {
        Application.launch(GUI.class, args);
    }
}