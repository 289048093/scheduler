package test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/7.
 */

public class Job2 implements Job {

    private String addr;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("job2:"+getAddr());
    }
}
