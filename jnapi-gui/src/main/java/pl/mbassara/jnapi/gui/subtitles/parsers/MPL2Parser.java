package pl.mbassara.jnapi.gui.subtitles.parsers;

import pl.mbassara.jnapi.gui.Global;
import pl.mbassara.jnapi.gui.subtitles.Subtitle;
import pl.mbassara.jnapi.gui.subtitles.Subtitles;

import java.io.*;
import java.util.ArrayList;

public class MPL2Parser extends Parser {

    @Override
    public Subtitles parse(InputStream inputStream, String charset, double fps)
            throws UnsupportedSubtitlesFormatException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    charset));
            Subtitles subtitles = new Subtitles(fps);
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.equals("") || line.matches("\\s*"))
                    continue;

                if (!line.matches("\\[\\d*\\]\\[\\d*\\].*"))
                    throw new UnsupportedSubtitlesFormatException(line);

                ArrayList<String> lines = new ArrayList<String>();

                String frameFromString = line.substring(1, line.indexOf("]"));
                if (frameFromString.equals(""))
                    frameFromString = "0";
                int frameFrom = Integer.parseInt(frameFromString);
                line = line.substring(line.indexOf("]") + 1);

                String frameToString = line.substring(1, line.indexOf("]"));
                if (frameToString.equals(""))
                    frameToString = "0";
                int frameTo = Integer.parseInt(frameToString);
                line = line.substring(line.indexOf("]") + 1);

                for (String subline : line.split("\\|"))
                    lines.add(subline);

                subtitles.addSubtitle(new Subtitle(frameFrom * 100,
                        frameTo * 100, lines));
            }

            return subtitles;

        } catch (FileNotFoundException e) {
            Global.getInstance().getLogger().warning(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Global.getInstance().getLogger().warning(e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
