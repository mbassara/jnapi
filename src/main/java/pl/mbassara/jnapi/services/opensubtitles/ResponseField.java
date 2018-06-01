package pl.mbassara.jnapi.services.opensubtitles;

import java.util.ArrayList;

public class ResponseField implements IResponsePart {

	private String name = "";
	private String value = "";
	private IResponsePart parent = null;

	@Override
	public IResponsePart getParent() {
		return parent;
	}

	@Override
	public void setParent(IResponsePart parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void concatValue(String value) {
		this.value += value;
	}

	@Override
	public boolean hasName(String name) {
		return this.name.equals(name);
	}

	@Override
	public ArrayList<ResponseField> getFields() {
		ArrayList<ResponseField> result = new ArrayList<ResponseField>();
		result.add(this);

		return result;
	}

	@Override
	public ArrayList<ResponseField> getFieldsForName(String name) {
		ArrayList<ResponseField> result = new ArrayList<ResponseField>();
		if (this.name.equals(name))
			result.add(this);

		return result;
	}

	@Override
	public boolean setValueForName(String name, String value) {
		if (this.name.equals(name)) {
			this.value = value;
			return true;
		}

		return false;
	}

	@Override
	public int getDepth() {
		if (parent == null)
			return 0;
		else
			return parent.getDepth() + 1;
	}

	@Override
	public String toString() {
		String indent = "";
		for (int i = 0; i < getDepth(); i++)
			indent += "\t";

		return indent + "name: " + name + "\tvalue: " + value;
	}
}
