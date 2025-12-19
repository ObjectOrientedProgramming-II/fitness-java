package gr.hua.coach.UI;

import java.util.List;

import gr.hua.coach.model.Activity;

public interface IUI {
    public void displayStatistics(
        List<Activity> activities,
        int weight
    );


    public void showUsage();
}
