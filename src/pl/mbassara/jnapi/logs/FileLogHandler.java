package pl.mbassara.jnapi.logs;

import java.io.IOException;
import java.util.logging.LogRecord;

public class FileLogHandler extends LogHandler {

	public FileLogHandler(String fileName, boolean append) {
		super(fileName, append);
	}

	@Override
	public void publish(LogRecord record) {
		try {
			time.setTime(record.getMillis());
			out.write("LOG no.\t\t" + record.getSequenceNumber()
					+ "\r\nTIME:\t\t" + time.toString() + "\r\nLEVEL:\t\t"
					+ record.getLevel().getName() + "\r\nFROM:\t\t"
					+ record.getSourceClassName() + "."
					+ record.getSourceMethodName() + "()" + "\r\nMESSAGE:\t"
					+ record.getMessage() + "\r\n\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
