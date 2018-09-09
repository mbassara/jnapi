package pl.mbassara.jnapi.web.napiproject;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mbassara.jnapi.core.services.SubtitlesResult;
import pl.mbassara.jnapi.web.exception.JNapiException;
import pl.mbassara.jnapi.web.file.FileFinder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/napiprojekt")
public class NapiprojektController {

    @Autowired
    private NapiprojektService napiprojekt;

    @Autowired
    private FileFinder fileFinder;

    @RequestMapping(value = "/download/{fileName:.+}", produces = "text/plain; charset=utf-8")
    public ResponseEntity<String> getSubtitles(@PathVariable String fileName) {
        return fileFinder.find(fileName)
                .flatMap(napiprojekt::download)
                .map(SubtitlesResult::getSubtitlesAsString)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/save/{fileName:.+}")
    public ResponseEntity<String> saveSubtitles(@PathVariable String fileName) {
        return fileFinder.find(fileName)
                .flatMap(napiprojekt::download)
                .map(this::saveSubtitles)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private String saveSubtitles(SubtitlesResult result) {
        Path moviePath = result.getMovieFile().toPath();
        String baseName = FilenameUtils.getBaseName(moviePath.toString());
        Path subtitlesPath = moviePath.resolveSibling(baseName + ".txt");

        try {
            byte[] subtitles = result.getSubtitlesAsString().getBytes(StandardCharsets.UTF_8);
            Files.write(subtitlesPath, subtitles);
            return "Subtitles saved to " + subtitlesPath;
        } catch (IOException e) {
            throw new JNapiException(e, "Cannot save subtitles to " + subtitlesPath);
        }
    }
}
