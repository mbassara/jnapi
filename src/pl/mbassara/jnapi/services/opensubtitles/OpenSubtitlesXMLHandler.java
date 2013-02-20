package pl.mbassara.jnapi.services.opensubtitles;

import java.util.ArrayList;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OpenSubtitlesXMLHandler extends DefaultHandler {

	private ArrayList<Member> currentStruct;
	private Member currentMember;
	private Stack<ArrayList<Member>> structsStack;
	private Stack<Member> membersStack;

	private boolean isMember = false;
	private boolean isName = false;
	private boolean isValue = false;

	@SuppressWarnings("unchecked")
	public ArrayList<Member> getResult() {
		if (currentMember.getValue() == null)
			return null;
		System.out.println(currentMember.getValue().getValue());
		return (ArrayList<Member>) currentMember.getValue().getValue();
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();

		structsStack = new Stack<ArrayList<Member>>();
		membersStack = new Stack<Member>();

		currentMember = new Member();
		currentMember.setValue(new Value("initialType", "initialValue"));
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (qName.equals("struct")) {
			if (currentStruct != null)
				structsStack.push(currentStruct);
			if (currentMember != null)
				membersStack.push(currentMember);

			currentStruct = new ArrayList<Member>();
			System.out.println("new struct");

			isMember = false;
			isValue = false;
		} else if (qName.equals("member")) {
			isMember = true;
			currentMember = new Member();
			System.out.println("new member");
		} else if (qName.equals("name"))
			isName = true;
		else if (qName.equals("value"))
			isValue = true;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);

		String chars = new String(ch, start, length);

		if (isMember) {
			if (isName)
				currentMember.setName(chars);
			if (isValue)
				currentMember.setValue(new Value("string", chars));
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		if (qName.equals("struct")) {
			if (membersStack.size() > 0)
				currentMember = membersStack.pop();

			currentMember.setValue(new Value("struct", currentStruct));
			System.out.println("close struct");
			System.out
					.println(currentMember.getValue().getValue() instanceof ArrayList<?>);

			if (structsStack.size() > 0)
				currentStruct = structsStack.pop();

			isMember = true;
			isValue = true;
		} else if (qName.equals("member")) {
			isMember = false;
			currentStruct.add(currentMember);
			System.out.println("close member");
		} else if (qName.equals("name"))
			isName = false;
		else if (qName.equals("value"))
			isValue = false;

	}

}
