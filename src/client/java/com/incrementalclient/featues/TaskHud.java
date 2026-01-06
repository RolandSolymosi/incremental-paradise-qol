package com.incrementalclient.featues;

import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.services.TaskMonitor;

import java.util.List;

public class TaskHud implements Observer<List<TaskMonitor.TaskState>> {

    public TaskHud(){

    }

    @Override
    public void onEvent(List<TaskMonitor.TaskState> result) {

    }
}
