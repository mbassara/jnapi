package pl.mbassara.jnapi.services.opensubtitles.parameters;

import java.util.ArrayList;

public class ArrayValue extends Value {
	private ArrayList<Value> values;

	public ArrayValue(Value[] values) {
		super("array");
		this.values = new ArrayList<Value>();
		for (Value value : values)
			this.values.add(value);
	}

	@Override
	public String toString() {
		String result = xmlBeg + "<data>";
		for (Value value : values)
			result += value.toString();
		return result + "</data>" + xmlEnd;
	}

}
