package team.se24.employfast.service;

import javax.servlet.ServletContext;
import java.util.TimerTask;

/*This is a class for the servlet schedule task to refresh the data files.
* Since buffers are designed for the csv files, the data has to be written back the the files after a short period of time*/
public class FileIOTask extends TimerTask {
    private ServletContext sc = null;
    private static boolean running=false;
    private static MarsEmployFast employSystem = null;

    @Override
    public void run() {
        if (!running) {
            running = true;
            fileIoTask();
            running = false;
        }
    }

    private void fileIoTask() {
        if (employSystem == null) {
            employSystem = MarsEmployFast.getInstance();
        }
        employSystem.flush();
    }

    public FileIOTask(ServletContext sc) {
        this.sc = sc;
    }
}
