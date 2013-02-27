package pl.mbassara.jnapi;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import pl.mbassara.jnapi.gui.SubtitlesCharset;
import pl.mbassara.jnapi.logs.FileLogHandler;
import pl.mbassara.jnapi.model.Subtitles.Format;
import pl.mbassara.jnapi.services.Lang;

public class Global {

	private static Global instance = null;

	private static final Logger logger = Logger.getLogger(Global.class
			.getName());

	private Global() {
		logger.addHandler(new FileLogHandler("logs/Global.txt", true));

		try {
			icon = ImageIO.read(getClass().getClassLoader()
					.getResourceAsStream("icon.png"));
		} catch (IOException e) {
			logger.warning(e.toString());
			e.printStackTrace();
		}
	}

	public static Global getInstance() {
		if (instance == null)
			instance = new Global();

		return instance;
	}

	private Lang lang = Lang.PL;
	private Format format = Format.MicroDVD;
	private SubtitlesCharset subtitlesCharset = SubtitlesCharset.Windows;
	private File lastUsedDirectory = null;
	private String selectedMovieFilePath = "";
	private Image icon;

	public Lang getLang() {
		return lang;
	}

	public void setLang(Lang lang) {
		this.lang = lang;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public SubtitlesCharset getSubtitlesCharset() {
		return subtitlesCharset;
	}

	public void setSubtitlesCharset(SubtitlesCharset subtitlesCharset) {
		this.subtitlesCharset = subtitlesCharset;
	}

	public File getLastUsedDirectory() {
		return lastUsedDirectory;
	}

	public void setLastUsedDirectory(File lastUsedDirectory) {
		this.lastUsedDirectory = lastUsedDirectory;
	}

	public String getSelectedMovieFilePath() {
		return selectedMovieFilePath;
	}

	public void setSelectedMovieFilePath(String selectedMovieFilePath) {
		this.selectedMovieFilePath = selectedMovieFilePath;
	}

	public Image getIcon() {
		return icon;
	}

}
