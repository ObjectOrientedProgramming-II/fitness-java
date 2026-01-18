package gr.hua.coach.service;

import gr.hua.coach.event.Events;
import gr.hua.coach.model.Activity;
import gr.hua.coach.state.AppState;

import java.util.*;

public class ActivityManager {
    
    private final List<Activity> activities = new ArrayList<>();
    private final AppState state;
    private final Events events = Events.get();
    
    public ActivityManager(AppState state) {
        this.state = state;
    }
    
    public void add(Activity activity) {
        activities.add(activity);
        state.transitionTo(AppState.State.HAS_DATA);
        events.fire(new Events.ActivityAdded(activity.getSport()));
    }
    
    public void addAll(List<Activity> newActivities) {
        activities.addAll(newActivities);
        state.transitionTo(AppState.State.HAS_DATA);
        events.fire(new Events.ActivitiesLoaded(newActivities.size()));
    }
    
    public void clear() {
        if (!state.canClear()) {
            throw new IllegalStateException("Cannot clear activities now");
        }
        activities.clear();
        state.transitionTo(AppState.State.EMPTY);
        events.fire(new Events.ActivitiesCleared());
    }
    
    public List<Activity> getAll() {
        return Collections.unmodifiableList(activities);
    }
    
    public int count() {
        return activities.size();
    }
    
    public boolean isEmpty() {
        return activities.isEmpty();
    }
}