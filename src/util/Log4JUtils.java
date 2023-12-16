package util;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;

public class Log4JUtils {
    private static final Log4JUtils logs = new Log4JUtils();

    static {
        System.setProperty("logFileName", "mj-" + System.currentTimeMillis() + "-test");
    }

    public static Log4JUtils instance() {
        return logs;
    }

    public URL findLoggerConfigFile() {
        return Thread.currentThread().getContextClassLoader().getResource("log4j.xml");
    }

    public void prepareLogFile(Logger root) {
        Appender appender = root.getAppender("file");

        if (!(appender instanceof FileAppender))
            return;
        FileAppender fAppender = (FileAppender) appender;

        String fileName = fAppender.getFile();
        String fileNameStripped = fileName.substring(0, fileName.lastIndexOf('.'));
        String logFileName = fileNameStripped + System.currentTimeMillis() + "-test.log"; // Dynamic log file name with timestamp

        File logFile = new File(logFileName);

        fAppender.setFile(logFile.getAbsolutePath());
        fAppender.activateOptions();
    }
}
