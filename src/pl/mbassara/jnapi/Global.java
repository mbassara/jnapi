package pl.mbassara.jnapi;

import java.io.File;

import pl.mbassara.jnapi.gui.SubtitlesCharset;
import pl.mbassara.jnapi.model.Subtitles.Format;
import pl.mbassara.jnapi.services.Lang;

public class Global {

	private static Global instance = null;

	private Global() {
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

}
