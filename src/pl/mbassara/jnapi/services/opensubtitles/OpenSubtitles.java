package pl.mbassara.jnapi.services.opensubtitles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pl.mbassara.jnapi.services.HTTPHelper;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt.Lang;
import pl.mbassara.jnapi.services.opensubtitles.parameters.ArrayValue;
import pl.mbassara.jnapi.services.opensubtitles.parameters.Member;
import pl.mbassara.jnapi.services.opensubtitles.parameters.SingleValue;
import pl.mbassara.jnapi.services.opensubtitles.parameters.StructValue;
import pl.mbassara.jnapi.services.opensubtitles.parameters.Value;

public class OpenSubtitles {

	static {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	private static final String URL = "http://api.opensubtitles.org/xml-rpc";
	private static SAXParser parser;

	public static ResponseStruct callMethod(String methodName,
			Value[] parameters) {
		String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodCall><methodName>"
				+ methodName + "</methodName>";

		if (parameters.length > 0)
			request += "<params>";

		for (Value param : parameters)
			request += "<param>" + param + "</param>";

		if (parameters.length > 0)
			request += "</params>";

		request += "</methodCall>";

		System.out.println(request);

		String response = HTTPHelper.sendOpenSubtitlesRequest(URL, request);

		OpenSubtitlesXMLHandler handler = new OpenSubtitlesXMLHandler();
		try {
			parser.parse(new ByteArrayInputStream(response.getBytes("UTF-8")),
					handler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return handler.getResult();
	}

	public static ResponseStruct logIn(String username, String password,
			String language, String useragent) {

		return callMethod("LogIn", new Value[] { new SingleValue(username),
				new SingleValue(password), new SingleValue(language),
				new SingleValue(useragent) });
	}

	public static ResponseStruct logOut(String token) {
		return callMethod("LogOut", new Value[] { new SingleValue(token) });
	}

	public static ResponseStruct searchSubtitles(String token, Lang lang,
			String movieHash, int fileSize) {
		String l = lang == Lang.PL ? "pol" : "eng";
		return callMethod("SearchSubtitles", new Value[] {
				new SingleValue(token),
				new ArrayValue(new Value[] { new StructValue(new Member[] {
						new Member("sublanguageid", new SingleValue(l)),
						new Member("moviehash", new SingleValue(movieHash)),
						new Member("moviebytesize", new SingleValue("double",
								fileSize + "")) }) }) });
	}

	public static ResponseStruct downloadSubtitles(String token,
			String idSubtitleFile) {
		return callMethod("DownloadSubtitles",
				new Value[] {
						new SingleValue(token),
						new ArrayValue(new Value[] { new SingleValue(
								idSubtitleFile) }) });
	}

	public static void main(String[] args) {

		// System.out.println(logIn("mbassara", "thorongil", "en", "Subget"));

		// System.out.println(logOut("amd30taehsj8ggc6sqnqvpp4f3"));

		// ResponseStruct result = searchSubtitles("5jm0j0gic050v6t15mgqacbkl6",
		// Lang.PL, "7d9cd5def91c9432", 735934464);
		// System.out.println(result);
		// for (ResponseField field : result.getFieldsForName("IDSubtitleFile"))
		// System.out.println(field);

		// ResponseStruct response = downloadSubtitles(
		// "amd30taehsj8ggc6sqnqvpp4f3", "1951948416");
		// System.out.println(FileHelper.decodeBase64TextData(response
		// .getFieldsForName("data").get(0).getValue()));
		// FileHelper.saveBase64File(new File("out.gzip"), response
		// .getFieldsForName("data").get(0).getValue());
		// FileHelper.saveBase64UngzippedFile(new File("out.txt"), response
		// .getFieldsForName("data").get(0).getValue());

		System.out.println("\"" + "7za.exe\" x -y -so -piBlm8NTigvru0Jr0 \""
				+ "File" + ".7z\" > \"" + "File" + "\"");
	}
}
