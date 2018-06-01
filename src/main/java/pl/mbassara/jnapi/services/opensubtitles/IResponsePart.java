package pl.mbassara.jnapi.services.opensubtitles;

import java.util.ArrayList;

public interface IResponsePart {
	public IResponsePart getParent();

	public void setParent(IResponsePart parent);

	public int getDepth();

	public boolean hasName(String name);

	public void setName(String name);

	public String getName();

	public boolean setValueForName(String name, String value);

	public ArrayList<ResponseField> getFields();

	public ArrayList<ResponseField> getFieldsForName(String name);

}
