package pl.mbassara.jnapi.core.services.opensubtitles;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

public class OpenSubtitlesXMLHandler extends DefaultHandler {

    private ResponseStruct currentStruct = null;
    private ResponseField currentField = null;
    private Stack<ResponseField> fieldStack;
    private Stack<ResponseStruct> structStack;

    private boolean isMember = false;
    private boolean isName = false;
    private boolean isValue = false;

    public ResponseStruct getResult() {
        return currentStruct;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();

        fieldStack = new Stack<ResponseField>();
        structStack = new Stack<ResponseStruct>();

    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (qName.equals("struct")) {
            if (currentStruct != null)
                structStack.push(currentStruct);
            if (currentField != null)
                fieldStack.push(currentField);

            currentStruct = new ResponseStruct();

            isMember = false;
            isValue = false;
        } else if (qName.equals("member")) {
            isMember = true;
            currentField = new ResponseField();
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
        if (isBlank(chars))
            return;

        if (isMember) {
            if (isName)
                currentField.setName(chars);
            if (isValue)
                currentField.concatValue(chars);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);

        if (qName.equals("struct")) {
            ResponseStruct oldStruct = currentStruct;

            if (structStack.size() > 0) {
                currentStruct = structStack.pop();
                currentStruct.addPart(oldStruct);
            }

            isMember = true;
            isValue = true;
        } else if (qName.equals("member")) {
            isMember = false;
            currentStruct.addPart(currentField);
        } else if (qName.equals("name"))
            isName = false;
        else if (qName.equals("value"))
            isValue = false;

    }

    private boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

}
