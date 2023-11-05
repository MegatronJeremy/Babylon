package util;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;

public class Log4JUtils {

    private static final Log4JUtils logs = new Log4JUtils();

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

        String fileNameStripped = fAppender.getFile();
        fileNameStripped = fileNameStripped.substring(0, fileNameStripped.lastIndexOf('.'));

        String logFileName = fileNameStripped + "-test.log";

        File logFile = new File(logFileName);
        File renamedFile = new File(fileNameStripped + "-test." + System.currentTimeMillis() + ".log");

        if (logFile.exists()) {
            if (!logFile.renameTo(renamedFile))
                System.err.println("Could not rename log file!");
        }

        fAppender.setFile(logFile.getAbsolutePath());
        fAppender.activateOptions();
    }
}
