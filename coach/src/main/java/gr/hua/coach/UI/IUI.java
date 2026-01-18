package gr.hua.coach.UI;

import gr.hua.coach.model.Activity;
import java.util.List;

public interface IUI {
    void displayStatistics(List<Activity> activities, int weight);
    
    void showUsage();
}