package test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/6.
 */

public class HelloJob implements Job {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("say hello :"+getName());
    }
}