package pl.mbassara.jnapi.model.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.mbassara.jnapi.Global;
import pl.mbassara.jnapi.model.Subtitle;
import pl.mbassara.jnapi.model.Subtitles;

public class MicroDVDParser extends Parser {

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

				if (!line.matches("\\{\\d*\\}\\{\\d*\\}.*"))
					throw new UnsupportedSubtitlesFormatException(line);

				ArrayList<String> lines = new ArrayList<String>();

				String frameFromString = line.substring(1, line.indexOf("}"));
				if (frameFromString.equals(""))
					frameFromString = "0";
				int frameFrom = Integer.parseInt(frameFromString);
				line = line.substring(line.indexOf("}") + 1);

				String frameToString = line.substring(1, line.indexOf("}"));
				if (frameToString.equals(""))
					frameToString = "0";
				int frameTo = Integer.parseInt(frameToString);
				line = line.substring(line.indexOf("}") + 1);

				for (String subline : line.split("\\|"))
					lines.add(subline);

				subtitles.addSubtitle(new Subtitle(
						(int) (frameFrom / fps * 1000.0),
						(int) (frameTo / fps * 1000.0), lines));
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
