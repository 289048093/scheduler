package test.com.mokylin.gm.scheduler;

import com.mokylin.gm.scheduler.rpc.SchedulerService;
import com.mokylin.gm.scheduler.rpc.dto.Page;
import com.mokylin.gm.scheduler.rpc.dto.SchedulerDTO;
import com.mokylin.gm.scheduler.rpc.exception.RPCException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>一月 9, 2015</pre>
 */
public class ClientTest {
    @Test
    public void testDubboRpc() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"consumer.xml"});
        context.start();

        SchedulerService demoService = (SchedulerService)context.getBean("schedulerService"); // 获取远程服务代理

        try {
//            long testdata123213 = demoService.addScheduler("com.test.HelloJob", "testdata8888", "0/3 * * * * ?", false);
//            System.out.println(testdata123213);
//            demoService.startJob(30);
//            demoService.updateJob(30,"test666","0/1 * * * * ?","com.test.HelloJob");
//            demoService.stopJob(30);
            Page<SchedulerDTO> list = demoService.list(null, null, true, null, 20, 1);
            System.out.println(list);
        } catch (RPCException e) {
            e.printStackTrace();
        }
    }

} 
