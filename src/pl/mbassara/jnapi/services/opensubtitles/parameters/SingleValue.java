package pl.mbassara.jnapi.services.opensubtitles.parameters;

public class SingleValue extends Value {
	private String value;

	public SingleValue(String type, String value) {
		super(type);
		this.value = value;
	}

	public SingleValue(String value) {
		super("string");
		this.value = value;
	}

	@Override
	public String toString() {
		return xmlBeg + value + xmlEnd;
	}
}
