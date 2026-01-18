package gr.hua.coach.UI;

import javafx.application.Application;

public class UIProvider {
    
    public IUI get() {
        return new CLI();
    }
    
    public static void launchGUI(String[] args) {
        Application.launch(GUI.class, args);
    }
}