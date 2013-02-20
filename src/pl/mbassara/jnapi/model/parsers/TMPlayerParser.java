package pl.mbassara.jnapi.model.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.mbassara.jnapi.model.Subtitle;
import pl.mbassara.jnapi.model.Subtitles;

public class TMPlayerParser extends Parser {

	@Override
	protected Subtitles parse(InputStream inputStream, String charset,
			double fps) throws WrongSubtitlesFormatException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream,
					charset));
			Subtitles subtitles = new Subtitles(fps);
			String line = reader.readLine(), nextLine = null;
			while (line.equals("") || line.matches("\\s*"))
				line = reader.readLine();

			if (!line.matches("\\d\\d:\\d\\d:\\d\\d:.+"))
				throw new WrongSubtitlesFormatException(line);

			while ((nextLine = reader.readLine()) != null || line != null) {
				if (nextLine != null
						&& (nextLine.equals("") || line.matches("\\s*")))
					continue;

				if (nextLine != null
						&& !nextLine.matches("\\d\\d:\\d\\d:\\d\\d:.+"))
					throw new WrongSubtitlesFormatException(nextLine);

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
