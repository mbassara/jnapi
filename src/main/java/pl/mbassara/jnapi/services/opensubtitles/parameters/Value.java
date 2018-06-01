package pl.mbassara.jnapi.services.opensubtitles.parameters;

public abstract class Value {
	private String type;
	protected String xmlBeg;
	protected String xmlEnd;

	public Value(String type) {
		this.type = type;
		xmlBeg = "<value><" + type + ">";
		xmlEnd = "</" + type + "></value>";
	}

	public String getType() {
		return type;
	}
}
