package pl.mbassara.jnapi.gui.subtitles;

import pl.mbassara.jnapi.core.services.SubtitlesResult;
import pl.mbassara.jnapi.gui.Global;
import pl.mbassara.jnapi.gui.mediainfo.MediaInfo;
import pl.mbassara.jnapi.gui.subtitles.parsers.*;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.TimeoutException;

public class SubtitlesConverter {

    public static boolean saveSubtitles(SubtitlesResult result, File destination, Subtitles.Format format, String charset)
            throws UnsupportedSubtitlesFormatException, TimeoutException {
        if (!result.isFound())
            return false;

        Subtitles subtitles = createSubtitles(result);
        if (subtitles == null)
            return false;

        return subtitles.save(format, destination, charset);
    }

    private static Subtitles createSubtitles(SubtitlesResult result) throws UnsupportedSubtitlesFormatException,
            TimeoutException {
        if (!result.isFound())
            return null;

        MediaInfo mediaInfo = null;
        try {
            mediaInfo = new MediaInfo();
        } catch (UnsatisfiedLinkError e) {
            JOptionPane.showMessageDialog(null,
                    "MediaInfo library is not found!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        mediaInfo.open(result.getMovieFile());

        double fps = Double.parseDouble(mediaInfo.get(
                MediaInfo.StreamKind.Video, 0, "FrameRate"));

        mediaInfo.close();

        String subtitlesString = result.getSubtitlesAsString();
        Subtitles subtitles = null;

        try {
            subtitles = new MicroDVDParser().parse(subtitlesString, fps);
        } catch (UnsupportedSubtitlesFormatException e) {
            Global.getInstance().getLogger().warning(e.toString());
            System.out.println("Error in line: " + e.getWrongLine()
                    + "\nParsing with MicroDVDParser failed. Trying SubRip.\n");
        }
        if (subtitles == null) {
            try {
                subtitles = new SubRipParser().parse(subtitlesString, fps);
            } catch (UnsupportedSubtitlesFormatException e) {
                Global.getInstance().getLogger().warning(e.toString());
                System.out.println("Error in line: " + e.getWrongLine()
                        + "\nParsing with SubRipParser failed. Trying MPL2.\n");
            }
        }
        if (subtitles == null) {
            try {
                subtitles = new MPL2Parser().parse(subtitlesString, fps);
            } catch (UnsupportedSubtitlesFormatException e) {
                Global.getInstance().getLogger().warning(e.toString());
                System.out
                        .println("Error in line: "
                                + e.getWrongLine()
                                + "\nParsing with MPL2Parser failed. Trying TMPlayer.\n");
            }
        }
        if (subtitles == null) {
            try {
                subtitles = new TMPlayerParser().parse(subtitlesString, fps);
            } catch (UnsupportedSubtitlesFormatException e) {
                Global.getInstance().getLogger().warning(e.toString());
                System.out
                        .println("Error in line: "
                                + e.getWrongLine()
                                + "\nParsing with TMPlayerParser failed. Can't save this file.\n");
            }
        }
        if (subtitles == null)
            throw new UnsupportedSubtitlesFormatException("");

        return subtitles;
    }
}
