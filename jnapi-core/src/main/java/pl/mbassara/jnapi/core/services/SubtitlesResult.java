package pl.mbassara.jnapi.core.services;

import java.io.File;
import java.util.concurrent.TimeoutException;

public interface SubtitlesResult {

    boolean isFound();

    String getMovieName();

    String getMovieReleaseName();

    String getProviderName();

    String getSubtitlesAsString() throws TimeoutException;

    Object getRawResult();

    File getMovieFile();

}
