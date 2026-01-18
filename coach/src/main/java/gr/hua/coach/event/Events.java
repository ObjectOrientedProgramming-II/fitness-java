package gr.hua.coach.event;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
    Event bus me publish-subscribe pattern
*/
public class Events {
    
    private static final Events INSTANCE = new Events();
    
    private final Map<Class<?>, List<Handler<?>>> handlers = new HashMap<>();
    
    private Events() {}
    
    public static Events get() {
        return INSTANCE;
    }
    
    @FunctionalInterface
    public interface Handler<T> {
        void handle(T event);
    }

    // Subscribe
    public <T> void on(Class<T> eventType, Handler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }
    
    // Publish
    @SuppressWarnings("unchecked")
    public <T> void fire(T event) {
        List<Handler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (Handler<?> handler : eventHandlers) {
                try {
                    ((Handler<T>) handler).handle(event);
                } catch (Exception e) {
                    System.err.println("Event handler error: " + e.getMessage());
                }
            }
        }
    }
    
    public static class ActivitiesLoaded {
        public final int count;
        public ActivitiesLoaded(int count) { this.count = count; }
    }
    
    public static class ActivityAdded {
        public final String sport;
        public ActivityAdded(String sport) { this.sport = sport; }
    }
    
    public static class ActivitiesCleared {
    }
    
    public static class StatsComputed {
        public final String statsText;
        public StatsComputed(String statsText) { this.statsText = statsText; }
    }
    
    public static class ProfileUpdated {
        public final double weight;
        public final int age;
        public ProfileUpdated(double weight, int age) { 
            this.weight = weight; 
            this.age = age; 
        }
    }
    
    public static class GoalSet {
        public final double target;
        public GoalSet(double target) { this.target = target; }
    }
}