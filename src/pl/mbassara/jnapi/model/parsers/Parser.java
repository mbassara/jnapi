package pl.mbassara.jnapi.model.parsers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import pl.mbassara.jnapi.model.Subtitles;

public abstract class Parser {

	public Subtitles parse(byte[] data, String charset, double fps)
			throws WrongSubtitlesFormatException {
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(data);
			return parse(inputStream, charset, fps);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public Subtitles parse(File file, String charset, double fps)
			throws WrongSubtitlesFormatException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			return parse(inputStream, charset, fps);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	protected abstract Subtitles parse(InputStream inputStream, String charset,
			double fps) throws WrongSubtitlesFormatException;
}
