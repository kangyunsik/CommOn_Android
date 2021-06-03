package com.example.test;


import com.firebase.jobdispatcher.JobService;

public class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }
}