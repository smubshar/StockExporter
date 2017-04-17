package com.shahrukhm;

import com.shahrukhm.model.BackgroundRequestStream;
import org.quartz.*;

/**
 * Class of job objects.
 */
public class MyJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            BackgroundRequestStream backgroundRequestStream =
                    (BackgroundRequestStream) schedulerContext.get("backgroundRequestStream");
            boolean b = backgroundRequestStream.requestDownloadAndUpload(BackgroundRequestStream.Request.AUTO_GET);
        } catch (SchedulerException e) {
        }
    }
}
