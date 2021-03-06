package pl.mbassara.jnapi.gui.subtitles;

import pl.mbassara.jnapi.gui.Global;

import java.io.*;
import java.util.ArrayList;

/**
 * The Subtitles class represents movie subtitles with certain FPS, which can be
 * saved as one of available format (MicroDVD, MPL2, TMPlayer, SubRip).
 *
 * @author Maciek
 */
public class Subtitles {
    public enum Format {
        MicroDVD, MPL2, SubRip, TMPlayer
    }

    private double fps;
    private ArrayList<Subtitle> subtitles;

    /**
     * Construct subtitles object for given fps.
     *
     * @param fps floating point value representing frames per seconds.
     */
    public Subtitles(double fps) {
        this.fps = fps;
        subtitles = new ArrayList<Subtitle>();
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public ArrayList<Subtitle> getSubtitles() {
        return subtitles;
    }

    /**
     * Appends new Subtitle to this Subtitles object.
     *
     * @param line line to add
     */
    public void addSubtitle(Subtitle subtitle) {
        subtitles.add(subtitle);
    }

    public boolean save(String format, File file, String charset) {
        return save(Format.valueOf(format), file, charset);
    }

    public boolean save(Format format, File file, String charset) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), charset));

            int i = 1;
            for (Subtitle subtitle : subtitles) {
                writer.write(subtitle.toString(format, i++, fps));
                writer.newLine();
            }

            return true;

        } catch (UnsupportedEncodingException e) {
            Global.getInstance().getLogger().warning(e.toString());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Global.getInstance().getLogger().warning(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Global.getInstance().getLogger().warning(e.toString());
            e.printStackTrace();
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Global.getInstance().getLogger().warning(e.toString());
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public String toString() {
        String result = "FPS: " + fps + "\n\n";

        for (Subtitle subtitle : subtitles)
            result += subtitle + "\n";

        return result;
    }
}
