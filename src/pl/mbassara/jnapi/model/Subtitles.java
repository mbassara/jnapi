package pl.mbassara.jnapi.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

import pl.mbassara.jnapi.logs.FileLogHandler;

/**
 * The Subtitles class represents movie subtitles with certain FPS, which can be
 * saved as one of available format (MicroDVD, MPL2, TMPlayer, SubRip).
 * 
 * @author Maciek
 * 
 */
public class Subtitles {
	public enum Format {
		MicroDVD, MPL2, SubRip, TMPlayer
	}

	private double fps;
	private ArrayList<Subtitle> subtitles;

	private final Logger logger = Logger.getLogger(Subtitles.class.getName());

	/**
	 * Construct subtitles object for given fps.
	 * 
	 * @param fps
	 *            floating point value representing frames per seconds.
	 */
	public Subtitles(double fps) {
		logger.addHandler(new FileLogHandler("logs/Subtitles.txt", true));
		this.fps = fps;
		subtitles = new ArrayList<Subtitle>();
	}

	public double getFps() {
		return fps;
	}

	public void setFps(double fps) {
		this.fps = fps;
	}

	public ArrayList<Subtitle> getSubtitles() {
		return subtitles;
	}

	/**
	 * Appends new Subtitle to this Subtitles object.
	 * 
	 * @param line
	 *            line to add
	 */
	public void addSubtitle(Subtitle subtitle) {
		subtitles.add(subtitle);
	}

	public boolean save(String format, File file, String charset) {
		return save(Format.valueOf(format), file, charset);
	}

	public boolean save(Format format, File file, String charset) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), charset));

			int i = 1;
			for (Subtitle subtitle : subtitles) {
				writer.write(subtitle.toString(format, i++, fps));
				writer.newLine();
			}

			return true;

		} catch (UnsupportedEncodingException e) {
			logger.warning(e.toString());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.warning(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.warning(e.toString());
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				logger.warning(e.toString());
				e.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public String toString() {
		String result = "FPS: " + fps + "\n\n";

		for (Subtitle subtitle : subtitles)
			result += subtitle + "\n";

		return result;
	}
}
