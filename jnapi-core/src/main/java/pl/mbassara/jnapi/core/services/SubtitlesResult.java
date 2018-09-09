package pl.mbassara.jnapi.core.services;

import java.io.File;

public interface SubtitlesResult {

    boolean isFound();

    String getMovieName();

    String getMovieReleaseName();

    String getProviderName();

    String getSubtitlesAsString();

    Object getRawResult();

    File getMovieFile();

}
