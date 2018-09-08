package pl.mbassara.jnapi.gui.logs;

import java.io.*;
import java.sql.Time;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogHandler extends Handler {

    protected BufferedWriter out;
    protected Time time;
    protected File file;

    public static LogHandler getHandler(String fileName) {
        return new LogHandler(fileName, true);
    }

    public static LogHandler getHandler(File file) {
        LogHandler result = null;
        try {
            result = new LogHandler(file.getCanonicalPath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public LogHandler(String fileName, boolean append) {
        super();
        time = new Time(System.currentTimeMillis());
        file = new File(fileName);
        try {
            out = new BufferedWriter(new FileWriter(fileName, append));

            out.write("\r\n\t##############################################"
                    + "\r\n\t# LOG SESSION: "
                    + (new Date(System.currentTimeMillis())).toString()
                    + " #"
                    + "\r\n\t##############################################\r\n\r\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    @Override
    public void close() throws SecurityException {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(LogRecord record) {
        try {
            out.write("#" + record.getSequenceNumber() + "\t"
                    + record.getMessage() + "\r\n\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
