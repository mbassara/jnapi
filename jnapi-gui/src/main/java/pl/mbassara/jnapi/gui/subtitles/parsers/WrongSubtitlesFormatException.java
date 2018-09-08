package pl.mbassara.jnapi.gui.subtitles.parsers;

public class WrongSubtitlesFormatException extends Exception {

    private static final long serialVersionUID = 2952114074165402073L;
    private String wrongLine;

    public WrongSubtitlesFormatException(String wrongLine) {
        this.wrongLine = wrongLine;
    }

    public String getWrongLine() {
        return wrongLine;
    }
}
