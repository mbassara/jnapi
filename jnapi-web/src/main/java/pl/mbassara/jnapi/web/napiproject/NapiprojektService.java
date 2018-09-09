package pl.mbassara.jnapi.web.napiproject;

import org.springframework.stereotype.Service;
import pl.mbassara.jnapi.core.services.Lang;
import pl.mbassara.jnapi.core.services.SubtitlesResult;
import pl.mbassara.jnapi.core.services.napiprojekt.Napiprojekt;
import pl.mbassara.jnapi.web.exception.JNapiException;

import java.nio.file.Path;
import java.util.Optional;

@Service
public class NapiprojektService {

    private final Napiprojekt napiprojekt = new Napiprojekt();

    public Optional<SubtitlesResult> download(Path path) {
        try {
            return napiprojekt.downloadSubtitles(path.toFile(), Lang.PL).stream()
                    .filter(SubtitlesResult::isFound)
                    .findFirst();
        } catch (Exception e) {
            throw new JNapiException(e, "Cannot download subtitles for " + path);
        }
    }
}
