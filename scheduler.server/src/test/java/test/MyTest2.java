package test;

import com.mokylin.gm.scheduler.jobloader.DynamicClassLoader;
import com.mokylin.gm.scheduler.util.ConfigInfo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class MyTest2 {

    public static void main2(String[] args) throws IOException {
//        URL resource = MyTest2.class.getClassLoader().getResource("E:\\workspace\\gm_platform\\gm_gccp\\code\\trunk\\scheduler\\scheduler.server\\src/main/filter/filter_daily.properties");
        URL resource = new File("E:\\workspace\\gm_platform\\gm_gccp\\code\\trunk\\gm-parent\\gm-job\\gmjob-impl\\target\\gmjob-impl-1.0-SNAPSHOT.jar").toURI().toURL();
        System.out.println(resource.getFile());
        JarFile jar =new JarFile(resource.getFile());

        JarEntry jarEntry = jar.getJarEntry("gm_consumer.xml");
        InputStream inputStream = jar.getInputStream(jarEntry);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        try {
            while ((s = reader.readLine())!=null){
                System.out.println(s);
            }
        } finally {
            reader.close();
        }


    }

    public static void main(String[] args) throws IOException {
        ConfigInfo.setConfigPath("E:\\workspace\\gm_platform\\gm_gccp\\code\\trunk\\scheduler\\scheduler.server\\conf\\scheduler.properties");
        DynamicClassLoader cl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
        cl.addFolder("E:\\workspace\\gm_platform\\gm_gccp\\code\\trunk\\gm-parent\\gm-job\\gmjob-impl\\target");
//        URLClassLoader cl = new URLClassLoader(new URL[]{new File("E:\\\\workspace\\\\gm_platform\\\\gm_gccp\\\\code\\\\trunk\\\\gm-parent\\\\gm-job\\\\gmjob-impl\\\\target\\gmjob-impl-1.0-SNAPSHOT.jar").toURI().toURL()});
//        URL resource = cl.getResource("gm_consumer.xml");
//        URL resource = new URL("jar:file:/E:/workspace/gm_platform/gm_gccp/code/trunk/gm-parent/gm-job/gmjob-impl/target/gmjob-impl-1.0-SNAPSHOT.jar!/gm_consumer.xml");
//        System.out.println(new File(resource.getFile()).exists());
//        System.out.println(resource.toURI().isOpaque());
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()));
//        String s;
//        try {
//            while ((s = reader.readLine())!=null){
//                System.out.println(s);
//            }
//        } finally {
//            reader.close();
//        }

        try {
            Class<?> aClass = cl.loadClass("com.mokylin.gm.job.GmJob");
            System.out.println(aClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main_(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
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
