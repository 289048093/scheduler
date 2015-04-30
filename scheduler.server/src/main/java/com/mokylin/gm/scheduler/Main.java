package com.mokylin.gm.scheduler;

import com.mokylin.gm.scheduler.jetty.JettyHandler;
import com.mokylin.gm.scheduler.jobloader.ClassHelper;
import com.mokylin.gm.scheduler.util.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;

import java.io.File;
import java.io.IOException;


/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/4.
 */

public class Main {

    private static String cfgDir;

    static {
        cfgDir = new File(Main.class.getClassLoader().getResource("").getPath()).getParent()+"/conf/";
    }


    private static void init() {
        DOMConfigurator.configure(cfgDir + "log4j.xml");

        SystemUtils.addClassPath(cfgDir);
        for(File file:new File(cfgDir).listFiles()){
            SystemUtils.addClassPath(file.getAbsolutePath());
        }
        ConfigInfo.setConfigPath(cfgDir + "scheduler.properties");

        Global.startDubbo(cfgDir);

        ClassHelper.init();

//        try {
//            FileUtils.copyFile(new File(cfgDir + "provider.xml"), new File("file:"+cfgDir+"/provider.xml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        JobManagerImpl.getInstance().initLoad();
    }

    private static void startJetty(){
        //        DOMConfigurator.configure(cfgDir + "log4j.xml");
        Server server = new Server(ConfigInfo.getInstance().getInt(ConfigInfo.JETTY_PORT));
        server.setHandler(new DefaultHandler());
//        XmlConfiguration configuration = null;
//        try {
//            configuration = new XmlConfiguration(
//                    new FileInputStream(cfgDir+"jetty.xml"));
//        } catch (FileNotFoundException e1) {
//            e1.printStackTrace();
//        } catch (SAXException e1) {
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }


        try {
//            configuration.configure(server);
            JettyHandler handler = new JettyHandler();
            server.setHandler(handler);
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException {
        init();

        startJetty();
    }


}
