package pl.mbassara.jnapi.model.parsers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import pl.mbassara.jnapi.logs.FileLogHandler;
import pl.mbassara.jnapi.model.Subtitles;

public abstract class Parser {

	private final Logger logger = Logger.getLogger(Parser.class.getName());

	public Parser() {
		logger.addHandler(new FileLogHandler("logs/Parser.txt", true));
	}

	public Subtitles parse(byte[] data, String charset, double fps)
			throws UnsupportedSubtitlesFormatException {
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(data);
			return parse(inputStream, charset, fps);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.warning(e.toString());
				e.printStackTrace();
			}
		}

	}

	public Subtitles parse(String subtitles, double fps)
			throws UnsupportedSubtitlesFormatException {
		try {
			return parse(subtitles.getBytes("UTF-8"), "UTF-8", fps);
		} catch (UnsupportedEncodingException e) {
			logger.warning(e.toString());
			e.printStackTrace();
		}
		return null;
	}

	public Subtitles parse(File file, String charset, double fps)
			throws UnsupportedSubtitlesFormatException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			return parse(inputStream, charset, fps);
		} catch (FileNotFoundException e) {
			logger.warning(e.toString());
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.warning(e.toString());
				e.printStackTrace();
			}
		}

		return null;
	}

	protected abstract Subtitles parse(InputStream inputStream, String charset,
			double fps) throws UnsupportedSubtitlesFormatException;
}
