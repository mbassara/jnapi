package pl.mbassara.jnapi.cli;

import org.apache.commons.io.FilenameUtils;
import pl.mbassara.jnapi.core.services.ISubtitlesProvider;
import pl.mbassara.jnapi.core.services.Lang;
import pl.mbassara.jnapi.core.services.SubtitlesResult;
import pl.mbassara.jnapi.core.services.napiprojekt.Napiprojekt;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.logging.Logger;

public class Application {

    private static final Logger logger = Logger.getLogger(Application.class.getSimpleName());

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide file path parameter");
            System.exit(-1);
            return;
        }

        Path path = Paths.get(args[0]);
        try {
            main(path);
        } catch (Exception e) {
            throw new RuntimeException("Cannot download subtitles for: " + path + ". " + e.getMessage());
        }
    }

    private static void main(Path path) throws Exception {
        ISubtitlesProvider provider = new Napiprojekt();
        Collection<SubtitlesResult> subtitlesResults = provider.downloadSubtitles(path.toFile(), Lang.PL);

        SubtitlesResult result = subtitlesResults.stream()
                .filter(SubtitlesResult::isFound)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find any subtitles"));

        String subtitles = result.getSubtitlesAsString();
        String name = FilenameUtils.getBaseName(path.toAbsolutePath().toString());

        Path subtitlesPath = path.resolveSibling(name + ".txt");
        Files.write(subtitlesPath, subtitles.getBytes(StandardCharsets.UTF_8));

        logger.info("Saved subtitles to " + subtitlesPath);
    }
}
