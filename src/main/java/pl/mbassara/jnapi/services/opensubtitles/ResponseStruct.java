package pl.mbassara.jnapi.services.opensubtitles;

import java.util.ArrayList;

public class ResponseStruct implements IResponsePart {

	private String name = "struct";
	private ArrayList<IResponsePart> parts;
	private IResponsePart parent = null;

	public ResponseStruct() {
		parts = new ArrayList<IResponsePart>();
	}

	@Override
	public IResponsePart getParent() {
		return parent;
	}

	@Override
	public void setParent(IResponsePart parent) {
		this.parent = parent;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void addPart(IResponsePart part) {
		part.setParent(this);
		parts.add(part);
	}

	public ArrayList<ResponseStruct> getSubResponseStructs() {
		ArrayList<ResponseStruct> result = new ArrayList<ResponseStruct>();

		for (IResponsePart part : parts)
			if (part instanceof ResponseStruct)
				result.add((ResponseStruct) part);

		return result;
	}

	@Override
	public boolean hasName(String name) {
		return this.name.equals(name) || getFieldsForName(name).size() > 0;
	}

	@Override
	public ArrayList<ResponseField> getFields() {
		ArrayList<ResponseField> result = new ArrayList<ResponseField>();

		for (IResponsePart part : parts)
			result.addAll(part.getFields());

		return result;
	}

	@Override
	public ArrayList<ResponseField> getFieldsForName(String name) {
		if (this.name.equals(name))
			return getFields();

		ArrayList<ResponseField> result = new ArrayList<ResponseField>();

		for (IResponsePart part : parts)
			result.addAll(part.getFieldsForName(name));

		return result;
	}

	@Override
	public boolean setValueForName(String name, String value) {
		boolean result = false;

		for (IResponsePart part : parts)
			if (part.setValueForName(name, value))
				result = true;

		return result;
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

		String result = indent + "name: " + name + "\tfields:\n";
		for (IResponsePart part : parts)
			result += "\t" + part + "\n";

		return result;
	}
}
