package pl.mbassara.jnapi.services.opensubtitles.parameters;

import java.util.ArrayList;

public class StructValue extends Value {
	private ArrayList<Member> members;

	public StructValue(Member[] members) {
		super("struct");
		this.members = new ArrayList<Member>();
		for (Member member : members)
			this.members.add(member);
	}

	@Override
	public String toString() {
		String result = xmlBeg;
		for (Member member : members)
			result += member.toString();
		return result + xmlEnd;
	}
}
