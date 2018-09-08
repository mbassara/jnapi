package pl.mbassara.jnapi.core.files;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MediaFileFilter extends FileFilter {

    private ArrayList<String> supportedFormats = new ArrayList<String>(
            Arrays.asList(new String[]{"mkv", "mka", "mks", "ogg", "ogm",
                    "avi", "wav", "mpeg", "mpg", "vob", "mp4", "mpgv", "mpv",
                    "m1v", "m2v", "mp2", "mp3", "asf", "wma", "wmv", "qt",
                    "mov", "rm", "rmvb", "ra", "ifo", "ac3", "dts", "aac",
                    "ape", "mac", "flac", "dat", "aiff", "aifc", "au", "iff",
                    "paf", "sd2", "irca", "w64", "mat", "pvf", "xi", "sds",
                    "avr"}));

    @Override
    public boolean accept(File file) {
        if (file.isDirectory())
            return true;

        String extension = file.getName();
        extension = extension.substring(extension.lastIndexOf(".") + 1);
        extension = extension.toLowerCase();

        return supportedFormats.contains(extension);
    }

    @Override
    public String getDescription() {
        return "Media files (avi, mp4, mkv, ...)";
    }

}
