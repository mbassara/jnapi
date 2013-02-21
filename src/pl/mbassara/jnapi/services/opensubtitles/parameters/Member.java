package pl.mbassara.jnapi.services.opensubtitles.parameters;

public class Member {
	private String name;
	private Value value;

	public Member(String name, Value value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Value getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "<member><name>" + name + "</name>" + value + "</member>";
	}
}
