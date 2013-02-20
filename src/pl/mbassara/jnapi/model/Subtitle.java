package pl.mbassara.jnapi.model;

import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * Structure representing single subtitle (could be multiline), including
 * beginning and end time in milliseconds.
 * 
 * @author Maciek
 * 
 */
public class Subtitle {
	private int timeFrom;
	private int timeTo;
	private ArrayList<String> lines;

	/**
	 * Constructs new Subtitle object which should be initialized later by
	 * proper setters
	 */
	public Subtitle() {
		this.lines = new ArrayList<String>();
	}

	/**
	 * Constructs new Subtitle with given content and times.
	 * 
	 * @param timeFrom
	 *            beginning time in miliseconds
	 * @param timeTo
	 *            end time in milliseconds
	 * @param line
	 *            single line of subtitles
	 */
	public Subtitle(int timeFrom, int timeTo, ArrayList<String> lines) {
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
		this.lines = lines;
	}

	/**
	 * @param timeFrom
	 *            time in milliseconds
	 */
	public void setTimeFrom(int timeFrom) {
		this.timeFrom = timeFrom;
	}

	/**
	 * @return time in milliseconds
	 */
	public int getTimeFrom() {
		return timeFrom;
	}

	/**
	 * @param timeTo
	 *            time in milliseconds
	 */
	public void setTimeTo(int timeTo) {
		this.timeTo = timeTo;
	}

	/**
	 * @return time in milliseconds
	 */
	public int getTimeTo() {
		return timeTo;
	}

	/**
	 * Appends given line to the list of lines stored by this Subtitle object
	 * 
	 * @param line
	 *            line to add
	 */
	public void addLine(String line) {
		lines.add(line);
	}

	/**
	 * @return List containing content of this subtitle
	 */
	public ArrayList<String> getLines() {
		return lines;
	}

	public String toString(Subtitles.TYPE type, int i, double fps) {
		switch (type) {
		case MicroDVD:
			return toMicroDVD(fps);
		case MPL2:
			return toMPL2();
		case SubRip:
			return toSubRip(i);
		case TMPlayer:
			return toTMPlayer();
		default:
			return "";
		}
	}

	public String toTMPlayer() {
		DateTime from = new DateTime(timeFrom);

		String result = String.format("%02d", from.getHourOfDay() - 1) + ":"
				+ String.format("%02d", from.getMinuteOfHour()) + ":"
				+ String.format("%02d", from.getSecondOfMinute()) + ":";
		int i = 0;
		for (String line : lines) {
			result += ((i > 0) ? "|" : "") + line;
			i++;
		}

		return result;
	}

	public String toMicroDVD(double fps) {
		String result = "{" + (int) (timeFrom * fps / 1000) + "}" + "{"
				+ (int) (timeTo * fps / 1000) + "}";
		int i = 0;
		for (String line : lines) {
			result += ((i > 0) ? "|" : "") + line;
			i++;
		}

		return result;
	}

	public String toMPL2() {
		String result = "[" + (int) (timeFrom / 100) + "]" + "["
				+ (int) (timeTo / 100) + "]";
		int i = 0;
		for (String line : lines) {
			result += ((i > 0) ? "|" : "") + line;
			i++;
		}

		return result;
	}

	public String toSubRip(int i) {
		DateTime from = new DateTime(timeFrom);
		DateTime to = new DateTime(timeTo);

		String result = i + "\n"
				+ String.format("%02d", from.getHourOfDay() - 1) + ":"
				+ String.format("%02d", from.getMinuteOfHour()) + ":"
				+ String.format("%02d", from.getSecondOfMinute()) + ","
				+ String.format("%03d", from.getMillisOfSecond()) + " --> "
				+ String.format("%02d", to.getHourOfDay() - 1) + ":"
				+ String.format("%02d", to.getMinuteOfHour()) + ":"
				+ String.format("%02d", to.getSecondOfMinute()) + ","
				+ String.format("%03d", to.getMillisOfSecond()) + "\n";

		for (String line : lines)
			result += line + "\n";

		return result;
	}

	@Override
	public String toString() {
		String result = timeFrom + " -> " + timeTo + ":";
		int i = 0;
		for (String line : lines) {
			result += ((i > 0) ? " | " : " ") + line;
			i++;
		}

		return result;
	}
}
