package com.mining.mining.activity.task;

import com.mining.mining.databinding.ActivityMainBinding;

import java.util.List;

public class ActivityTask {
    private ActivityMainBinding binding;

    public void setBinding(ActivityMainBinding binding) {
        this.binding = binding;
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    public static ActivityTask getInstance() {
        return MyTask.getActivityTask();
    }

    private static class MyTask {
        private static ActivityTask activityTask;

        public static ActivityTask getActivityTask() {
            if (activityTask == null) {
                activityTask = new ActivityTask();
            }
            return activityTask;
        }
    }
}
