package pl.mbassara.jnapi.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

public interface ISubtitlesProvider {

	public Collection<SubtitlesResult> downloadSubtitles(File movieFile,
			Lang lang) throws FileNotFoundException, TimeoutException;

}
