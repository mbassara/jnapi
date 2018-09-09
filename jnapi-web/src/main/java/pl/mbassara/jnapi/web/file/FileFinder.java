package pl.mbassara.jnapi.web.file;

import org.springframework.stereotype.Service;
import pl.mbassara.jnapi.web.exception.JNapiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Service
public class FileFinder {

    private static final Path BASE_PATH = Paths.get("/home/maciek/Wideo");

    public Optional<Path> find(String fileName) {
        List<Path> paths;
        try {
            paths = Files.find(BASE_PATH, 10, fileMatcher(fileName)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new JNapiException(e, "Cannot find file " + fileName);
        }
        if (paths.isEmpty()) {
            return Optional.empty();
        }
        if (paths.size() > 1) {
            throw new JNapiException("Too many file found for " + fileName);
        }
        return Optional.ofNullable(paths.get(0));
    }

    private BiPredicate<Path, BasicFileAttributes> fileMatcher(String expectedFileName) {
        return (path, attributes) -> {
            String fileName = path.getFileName().toString();
            return attributes.isRegularFile() && expectedFileName.equalsIgnoreCase(fileName);
        };
    }
}
