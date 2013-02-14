package pl.mbassara.jnapi.model.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.mbassara.jnapi.model.Subtitle;
import pl.mbassara.jnapi.model.Subtitles;

public class MPL2Parser extends Parser {

	@Override
	public Subtitles parse(InputStream inputStream, String charset, double fps)
			throws WrongSubtitlesFormatException {
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
					throw new WrongSubtitlesFormatException(line);

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
