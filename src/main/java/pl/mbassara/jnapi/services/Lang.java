package pl.mbassara.jnapi.services;

public enum Lang {
	PL("Polish"), ENG("English");

	private String value;

	Lang(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.getValue();
	}

	public static Lang getValueOf(String value) {
		for (Lang v : values())
			if (value.equalsIgnoreCase(v.getValue()))
				return v;

		return null;
	}
}
