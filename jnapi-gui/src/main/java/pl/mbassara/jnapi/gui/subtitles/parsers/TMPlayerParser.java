package pl.mbassara.jnapi.gui.subtitles.parsers;

import pl.mbassara.jnapi.gui.Global;
import pl.mbassara.jnapi.gui.subtitles.Subtitle;
import pl.mbassara.jnapi.gui.subtitles.Subtitles;

import java.io.*;
import java.util.ArrayList;

public class TMPlayerParser extends Parser {

    @Override
    protected Subtitles parse(InputStream inputStream, String charset,
                              double fps) throws UnsupportedSubtitlesFormatException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    charset));
            Subtitles subtitles = new Subtitles(fps);
            String line = reader.readLine(), nextLine = null;
            while (line.equals("") || line.matches("\\s*"))
                line = reader.readLine();

            if (!line.matches("\\d\\d:\\d\\d:\\d\\d:.+"))
                throw new UnsupportedSubtitlesFormatException(line);

            while ((nextLine = reader.readLine()) != null || line != null) {
                if (nextLine != null
                        && (nextLine.equals("") || line.matches("\\s*")))
                    continue;

                if (nextLine != null
                        && !nextLine.matches("\\d\\d:\\d\\d:\\d\\d:.+"))
                    throw new UnsupportedSubtitlesFormatException(nextLine);

                ArrayList<String> lines = new ArrayList<String>();

                int timeFrom = Integer.parseInt(line.substring(6, 8)) * 1000;
                timeFrom += Integer.parseInt(line.substring(3, 5)) * 60 * 1000;
                timeFrom += Integer.parseInt(line.substring(0, 2)) * 60 * 60 * 1000;

                int timeNext;
                if (nextLine != null) {
                    timeNext = Integer.parseInt(nextLine.substring(6, 8)) * 1000;
                    timeNext += Integer.parseInt(nextLine.substring(3, 5)) * 60 * 1000;
                    timeNext += Integer.parseInt(nextLine.substring(0, 2)) * 60 * 60 * 1000;
                } else
                    timeNext = Integer.MAX_VALUE;

                line = line.substring(9);

                for (String subline : line.split("\\|"))
                    lines.add(subline);

                int timeTo = Math.min(timeFrom + 2500 * lines.size(), timeNext);

                subtitles.addSubtitle(new Subtitle(timeFrom, timeTo, lines));

                line = nextLine;
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
