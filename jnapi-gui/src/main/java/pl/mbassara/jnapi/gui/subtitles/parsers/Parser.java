package pl.mbassara.jnapi.gui.subtitles.parsers;

import pl.mbassara.jnapi.gui.Global;
import pl.mbassara.jnapi.gui.subtitles.Subtitles;

import java.io.*;

public abstract class Parser {

    public Subtitles parse(byte[] data, String charset, double fps)
            throws UnsupportedSubtitlesFormatException {
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(data);
            return parse(inputStream, charset, fps);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Global.getInstance().getLogger().warning(e.toString());
                e.printStackTrace();
            }
        }

    }

    public Subtitles parse(String subtitles, double fps)
            throws UnsupportedSubtitlesFormatException {
        try {
            return parse(subtitles.getBytes("UTF-8"), "UTF-8", fps);
        } catch (UnsupportedEncodingException e) {
            Global.getInstance().getLogger().warning(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public Subtitles parse(File file, String charset, double fps)
            throws UnsupportedSubtitlesFormatException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return parse(inputStream, charset, fps);
        } catch (FileNotFoundException e) {
            Global.getInstance().getLogger().warning(e.toString());
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Global.getInstance().getLogger().warning(e.toString());
                e.printStackTrace();
            }
        }

        return null;
    }

    protected abstract Subtitles parse(InputStream inputStream, String charset,
                                       double fps) throws UnsupportedSubtitlesFormatException;
}
