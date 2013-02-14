package pl.mbassara.napi.model.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pl.mbassara.napi.model.Subtitle;
import pl.mbassara.napi.model.Subtitles;

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
			double fps) throws WrongSubtitlesFormatException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(inputStream,
					charset));
			Subtitles subtitles = new Subtitles(fps);
			Subtitle tmpSubtitle = null;
			String line = "", prevLine = "";

			while ((line = reader.readLine()) != null) {
				if (state == STATE.LINE_NO) {
					if (line.equals("") || line.matches("\\s*"))
						continue;

					if (!line.matches("\\d+"))
						throw new WrongSubtitlesFormatException(line);

					tmpSubtitle = new Subtitle();
					nextState();
				} else if (state == STATE.TIMES) {
					if (!line
							.matches("\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d"))
						throw new WrongSubtitlesFormatException(line);

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
			if (!prevLine.equals("") && !prevLine.matches("\\s*"))
				subtitles.addSubtitle(tmpSubtitle);

			return subtitles;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
