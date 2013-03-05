package pl.mbassara.jnapi.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public interface ISubtitlesProvider {

	public ArrayList<SubtitlesResult> downloadSubtitles(File movieFile,
			Lang lang) throws FileNotFoundException, TimeoutException;

}
