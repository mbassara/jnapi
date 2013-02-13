package pl.mbassara.napi.model.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.mbassara.napi.model.Subtitle;
import pl.mbassara.napi.model.Subtitles;

public class MicroDVDParser extends Parser {

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

				if (!line.matches("\\{\\d+\\}\\{\\d+\\}.*"))
					throw new WrongSubtitlesFormatException(line);

				ArrayList<String> lines = new ArrayList<String>();

				int frameFrom = Integer.parseInt(line.substring(1,
						line.indexOf("}")));
				line = line.substring(line.indexOf("}") + 1);
				int frameTo = Integer.parseInt(line.substring(1,
						line.indexOf("}")));
				line = line.substring(line.indexOf("}") + 1);

				for (String subline : line.split("\\|"))
					lines.add(subline);

				subtitles.addSubtitle(new Subtitle(
						(int) (frameFrom / fps * 1000.0),
						(int) (frameTo / fps * 1000.0), lines));
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
