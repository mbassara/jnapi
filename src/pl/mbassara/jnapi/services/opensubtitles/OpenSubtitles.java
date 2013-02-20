package pl.mbassara.jnapi.services.opensubtitles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pl.mbassara.jnapi.services.HTTPHelper;

public class OpenSubtitles {

	private static final String URL = "http://api.opensubtitles.org/xml-rpc";

	public static String callMethod(String methodName, Value[] parameters) {
		String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodCall><methodName>"
				+ methodName + "</methodName>";

		if (parameters.length > 0)
			request += "<params>";

		for (Value param : parameters)
			request += "<param><value><" + param.getType() + ">"
					+ param.getValue() + "</" + param.getType()
					+ "></value></param>";

		if (parameters.length > 0)
			request += "</params>";

		request += "</methodCall>";

		System.out.println(request);

		return HTTPHelper.sendOpenSubtitlesRequest(URL, request);
	}

	public static String logIn(String username, String password,
			String language, String useragent) {

		return callMethod("LogIn", new Value[] { new Value("string", username),
				new Value("string", password), new Value("string", language),
				new Value("string", useragent) });
	}

	public static String logOut(String token) {
		return callMethod("LogOut", new Value[] { new Value("string", token) });
	}

	public static void main(String[] args) {
		String response = logIn("mbassara", "thorongil", "en", "JNapi v0.1");

		System.out.println(response);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		OpenSubtitlesXMLHandler handler = new OpenSubtitlesXMLHandler();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(response.getBytes("UTF-8")),
					handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Member member : handler.getResult()) {
			System.out.println(member);
		}

		// System.out.println(logOut("t2koinjb7csmu79in86e2eb0f7"));
	}
}
