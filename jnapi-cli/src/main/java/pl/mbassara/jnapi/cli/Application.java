package pl.mbassara.jnapi.cli;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.apache.commons.io.FilenameUtils;
import pl.mbassara.jnapi.core.services.ISubtitlesProvider;
import pl.mbassara.jnapi.core.services.Lang;
import pl.mbassara.jnapi.core.services.SubtitlesResult;
import pl.mbassara.jnapi.core.services.napiprojekt.Napiprojekt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
            throw new RuntimeException("Cannot download subtitles for: " + path + ". " + e.getMessage(), e);
        }
    }

    private static void main(Path path) throws Exception {
        Map<Double, Integer> focal = getStatistics(path, Application::getFocalLength);
        Map<Double, Integer> focalEq = getFocalEquivalent(focal);
        Map<Double, Integer> aperture = getStatistics(path, Application::getAperture);
        Map<Double, Integer> iso = getStatistics(path, Application::getIso);
        String csv = focal.entrySet().stream().map(e -> e.getKey() + ", " + e.getValue()).collect(Collectors.joining("\n"));

        System.out.println(focal);
    }

    private static Map<Double, Integer> getStatistics(Path path, Function<Metadata, Double> extractor) throws IOException {
        return Files.find(path, 10, (p, opts) -> Files.isRegularFile(p))
                .map(Path::toFile)
                .map(Application::readMetadata)
                .map(extractor)
                .collect(Collectors.toMap(Function.identity(), key -> 1, (a, b) -> a + b, TreeMap::new));
    }

    private static Metadata readMetadata(File file) {
        try {
            return ImageMetadataReader.readMetadata(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static double getFocalLength(Metadata metadata) {
        return getValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
    }

    private static double getAperture(Metadata metadata) {
        return getValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_FNUMBER);
    }

    private static double getIso(Metadata metadata) {
        return getValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
    }

    private static double getValue(Metadata metadata, Class<? extends Directory> directoryType, int tag) {
        Directory directory = metadata.getDirectoriesOfType(directoryType).iterator().next();
        try {
            return Math.round(directory.getFloat(tag) * 10) / 10.0;
        } catch (MetadataException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<Double, Integer> getFocalEquivalent(Map<Double, Integer> focal) {
        Map<Double, Integer> focalEq = new TreeMap<>();
        focal.forEach((f, c) -> focalEq.put(f * 1.5, c));
        return focalEq;
    }

    private static void main2(Path path) throws Exception {
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
