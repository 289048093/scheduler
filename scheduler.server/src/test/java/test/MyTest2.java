package test;

import com.mokylin.gm.scheduler.jobloader.DynamicClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class MyTest2 {

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        DynamicClassLoader dClassLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
        dClassLoader.addFolder("F:\\test2");
        Class<?> aClass = dClassLoader.loadClass("com.lz.MyTest");
        Object o = aClass.newInstance();
        Method sayHello = aClass.getDeclaredMethod("sayHello");
        sayHello.invoke(o);
        dClassLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
        while(true) {
            try {
                dClassLoader.addFolder("F:\\test");
                Class<?> aClass1 = dClassLoader.loadClass("com.test.TUtil");
                Method nextString = aClass1.getDeclaredMethod("nextString");
                System.out.println(nextString.invoke(null));

                aClass = dClassLoader.loadClass("com.lz.MyTest");
                o = aClass.newInstance();
                sayHello = aClass.getDeclaredMethod("sayHello");
                Object invoke = sayHello.invoke(o);
                System.out.println(invoke);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test1(){
        System.out.println(StringUtils.countMatches("asdf=gds=asw==qer","="));
    }
}
