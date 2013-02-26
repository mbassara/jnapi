package pl.mbassara.jnapi.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface ISubtitlesProvider {

	public ArrayList<SubtitlesResult> downloadSubtitles(File movieFile,
			Lang lang) throws FileNotFoundException;

}
