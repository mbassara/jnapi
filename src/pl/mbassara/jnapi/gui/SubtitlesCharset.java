package pl.mbassara.jnapi.gui;

public enum SubtitlesCharset {
	Windows("windows-1250"), Latin2("ISO-8859-2"), UTF8("UTF-8");

	private String value;

	SubtitlesCharset(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.getValue();
	}

	public static SubtitlesCharset getValueOf(String value) {
		for (SubtitlesCharset v : values())
			if (value.equalsIgnoreCase(v.getValue()))
				return v;

		return null;
	}
}
