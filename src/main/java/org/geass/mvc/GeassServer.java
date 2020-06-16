package org.geass.mvc;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.Servlet;
import java.io.File;

/**
 * @Description:
 * @Author: ArchGeass
 * @Date: 2020/6/14,上午9:43
 */
public class GeassServer {
    public static void startServer(Servlet servlet) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.setConnector(connector);

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        tomcat.addServlet("", "Embedded", servlet);
        ctx.addServletMappingDecoded("/myPath", "Embedded");

        tomcat.start();
        tomcat.getServer().await();
    }
}
