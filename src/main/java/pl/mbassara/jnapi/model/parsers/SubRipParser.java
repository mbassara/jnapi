package pl.mbassara.jnapi.model.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pl.mbassara.jnapi.Global;
import pl.mbassara.jnapi.model.Subtitle;
import pl.mbassara.jnapi.model.Subtitles;

public class SubRipParser extends Parser {

	private enum STATE {
		LINE_NO, TIMES, CONTENT
	}

	private STATE state;

	public SubRipParser() {
		state = STATE.LINE_NO;
	}

	private void nextState() {
		switch (state) {
		case LINE_NO:
			state = STATE.TIMES;
			break;
		case TIMES:
			state = STATE.CONTENT;
			break;
		case CONTENT:
			state = STATE.LINE_NO;
			break;
		}
	}

	protected Subtitles parse(InputStream inputStream, String charset,
			double fps) throws UnsupportedSubtitlesFormatException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream,
					charset));
			Subtitles subtitles = new Subtitles(fps);
			Subtitle tmpSubtitle = null;
			String line = "", prevLine = "";

			while ((line = reader.readLine()) != null) {
				if (state == STATE.LINE_NO) {
					if (!line.matches("\\d+"))
						continue;

					tmpSubtitle = new Subtitle();
					nextState();
				} else if (state == STATE.TIMES) {
					if (!line
							.matches("\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d"))
						throw new UnsupportedSubtitlesFormatException(line);

					int timeFrom = Integer.parseInt(line.substring(9, 12));
					timeFrom += Integer.parseInt(line.substring(6, 8)) * 1000;
					timeFrom += Integer.parseInt(line.substring(3, 5)) * 60 * 1000;
					timeFrom += Integer.parseInt(line.substring(0, 2)) * 60 * 60 * 1000;

					int timeTo = Integer.parseInt(line.substring(26, 29));
					timeTo += Integer.parseInt(line.substring(23, 25)) * 1000;
					timeTo += Integer.parseInt(line.substring(20, 22)) * 60 * 1000;
					timeTo += Integer.parseInt(line.substring(17, 19)) * 60 * 60 * 1000;

					tmpSubtitle.setTimeFrom(timeFrom);
					tmpSubtitle.setTimeTo(timeTo);
					nextState();
				} else if (state == STATE.CONTENT) {
					if (line.equals("") || line.matches("\\s*")) {
						subtitles.addSubtitle(tmpSubtitle);
						nextState();
						continue;
					}

					tmpSubtitle.addLine(line);
				}
				prevLine = line;
			}

			if (subtitles.getSubtitles().size() == 0)
				throw new UnsupportedSubtitlesFormatException(prevLine);

			if (!prevLine.equals("") && !prevLine.matches("\\s*")) // if there
																	// was no
																	// empty
																	// line at
																	// the end
																	// of file
				subtitles.addSubtitle(tmpSubtitle);

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
