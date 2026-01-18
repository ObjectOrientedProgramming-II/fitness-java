package gr.hua.coach.service;

import gr.hua.coach.model.Activity;
import gr.hua.coach.parser.TCXParser;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FileLoader {
    
    private final TCXParser parser = new TCXParser();
    
    public CompletableFuture<LoadResult> loadAsync(List<File> files) {
        return CompletableFuture.supplyAsync(() -> {
            List<Activity> loaded = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            for (File file : files) {
                try {
                    loaded.addAll(parser.parse(file));
                } catch (Exception e) {
                    errors.add(file.getName() + ": " + e.getMessage());
                }
            }
            
            return new LoadResult(loaded, errors);
        });
    }
    
    public static class LoadResult {
        public final List<Activity> activities;
        public final List<String> errors;
        
        public LoadResult(List<Activity> activities, List<String> errors) {
            this.activities = activities;
            this.errors = errors;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}