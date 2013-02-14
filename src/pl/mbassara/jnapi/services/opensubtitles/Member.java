package pl.mbassara.jnapi.services.opensubtitles;

public class Member {

	private String name;
	private Value value;

	public String getName() {
		return name;
	}

	public Value getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "name: " + name + "value: " + value;
	}
}
