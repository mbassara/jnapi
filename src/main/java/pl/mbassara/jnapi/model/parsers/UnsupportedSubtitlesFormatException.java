package pl.mbassara.jnapi.model.parsers;

public class UnsupportedSubtitlesFormatException extends Exception {

	private static final long serialVersionUID = 2952114074165402073L;
	private String wrongLine;

	public UnsupportedSubtitlesFormatException(String wrongLine) {
		this.wrongLine = wrongLine;
	}

	public String getWrongLine() {
		return wrongLine;
	}
}
