package pl.mbassara.jnapi.services;

import java.io.File;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import pl.mbassara.jnapi.logs.FileLogHandler;
import pl.mbassara.jnapi.mediainfo.MediaInfo;
import pl.mbassara.jnapi.model.Subtitles;
import pl.mbassara.jnapi.model.Subtitles.Format;
import pl.mbassara.jnapi.model.parsers.MPL2Parser;
import pl.mbassara.jnapi.model.parsers.MicroDVDParser;
import pl.mbassara.jnapi.model.parsers.SubRipParser;
import pl.mbassara.jnapi.model.parsers.TMPlayerParser;
import pl.mbassara.jnapi.model.parsers.UnsupportedSubtitlesFormatException;

public abstract class SubtitlesResult {

	private final Logger logger = Logger.getLogger(SubtitlesResult.class
			.getName());

	public SubtitlesResult() {
		logger.addHandler(new FileLogHandler("logs/SubtitlesResult.txt", true));
	}

	public abstract boolean isFound();

	public abstract String getMovieName();

	public abstract String getMovieReleaseName();

	public abstract String getProviderName();

	public abstract String getSubtitlesAsString();

	public abstract Object getRawResult();

	protected abstract File getMovieFile();

	public Subtitles getSubtitles() throws UnsupportedSubtitlesFormatException {
		if (!isFound())
			return null;

		MediaInfo mediaInfo = null;
		try {
			mediaInfo = new MediaInfo();
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null,
					"mediainfo.dll library is not found!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		mediaInfo.open(getMovieFile());

		double fps = Double.parseDouble(mediaInfo.get(
				MediaInfo.StreamKind.Video, 0, "FrameRate"));

		String subtitlesString = getSubtitlesAsString();
		Subtitles subtitles = null;

		try {
			subtitles = new MicroDVDParser().parse(subtitlesString, fps);
		} catch (UnsupportedSubtitlesFormatException e) {
			logger.warning(e.toString());
			System.out.println("Error in line: " + e.getWrongLine()
					+ "\nParsing with MicroDVDParser failed. Trying SubRip.\n");
		}
		if (subtitles == null) {
			try {
				subtitles = new SubRipParser().parse(subtitlesString, fps);
			} catch (UnsupportedSubtitlesFormatException e) {
				logger.warning(e.toString());
				System.out.println("Error in line: " + e.getWrongLine()
						+ "\nParsing with SubRipParser failed. Trying MPL2.\n");
			}
		}
		if (subtitles == null) {
			try {
				subtitles = new MPL2Parser().parse(subtitlesString, fps);
			} catch (UnsupportedSubtitlesFormatException e) {
				logger.warning(e.toString());
				System.out
						.println("Error in line: "
								+ e.getWrongLine()
								+ "\nParsing with MPL2Parser failed. Trying TMPlayer.\n");
			}
		}
		if (subtitles == null) {
			try {
				subtitles = new TMPlayerParser().parse(subtitlesString, fps);
			} catch (UnsupportedSubtitlesFormatException e) {
				logger.warning(e.toString());
				System.out
						.println("Error in line: "
								+ e.getWrongLine()
								+ "\nParsing with TMPlayerParser failed. Can't save this file.\n");
			}
		}
		if (subtitles == null)
			throw new UnsupportedSubtitlesFormatException("");

		return subtitles;
	}

	public boolean saveSubtitles(File destination, Format format, String charset)
			throws UnsupportedSubtitlesFormatException {
		if (!isFound())
			return false;

		Subtitles subtitles = getSubtitles();
		if (subtitles == null)
			return false;

		return subtitles.save(format, destination, charset);
	}
}