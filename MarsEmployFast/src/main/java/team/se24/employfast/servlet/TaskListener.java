package team.se24.employfast.servlet;

import team.se24.employfast.service.FileIOTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;

public class TaskListener implements ServletContextListener {
    private java.util.Timer timer = null;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        timer = new java.util.Timer(true);

        timer.schedule(new FileIOTask(sce.getServletContext()), new Date(), 20*1000);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
