package pl.mbassara.jnapi.services.opensubtitles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pl.mbassara.jnapi.services.FileHelper;
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

	public static String logIn() {
		ResponseStruct response = logIn("", "", "en", "Subget");
		if (isResponseOK(response))
			return response.getFieldsForName("token").get(0).getValue();
		else
			return null;
	}

	public static ResponseStruct logIn(String username, String password,
			String language, String useragent) {

		return callMethod("LogIn", new Value[] { new SingleValue(username),
				new SingleValue(password), new SingleValue(language),
				new SingleValue(useragent) });
	}

	public static boolean logOut(String token) {
		return isResponseOK(callMethod("LogOut", new Value[] { new SingleValue(
				token) }));
	}

	public static ResponseStruct searchSubtitles(String token, Lang lang,
			String movieHash, long fileSize) {
		String l = lang == Lang.PL ? "pol" : "eng";
		return callMethod("SearchSubtitles", new Value[] {
				new SingleValue(token),
				new ArrayValue(new Value[] { new StructValue(new Member[] {
						new Member("sublanguageid", new SingleValue(l)),
						new Member("moviehash", new SingleValue(movieHash)),
						new Member("moviebytesize", new SingleValue("double",
								fileSize + "")) }) }) });
	}

	public static ResponseStruct searchSubtitles(String token, File movieFile) {
		try {
			String movieHash = OpenSubtitlesHasher.computeHash(movieFile);
			long fileSize = movieFile.length();

			return searchSubtitles(token, Lang.PL, movieHash, fileSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ResponseStruct downloadSubtitles(String token,
			String idSubtitleFile) {
		return callMethod("DownloadSubtitles",
				new Value[] {
						new SingleValue(token),
						new ArrayValue(new Value[] { new SingleValue(
								idSubtitleFile) }) });
	}

	public static String downloadSubtitles(String token, File movieFile) {
		ResponseStruct response = searchSubtitles(token, movieFile);
		if (!isResponseOK(response))
			return null;

		String idSubtitleFile = response.getFieldsForName("IDSubtitleFile")
				.get(0).getValue();
		response = downloadSubtitles(token, idSubtitleFile);
		if (!isResponseOK(response))
			return null;

		return FileHelper.ungzipData(FileHelper.base64ToByteArray(response
				.getFieldsForName("data").get(0).getValue()));
	}

	private static boolean isResponseOK(ResponseStruct response) {
		if (response == null)
			return false;
		ArrayList<ResponseField> fields = response.getFieldsForName("status");
		if (fields.size() == 0)
			return false;

		return fields.get(0).getValue().equals("200 OK");
	}

	public static void main(String[] args) {
		// System.out.println(logIn());

		// System.out.println(logOut("5ev5qrbi743aug37oskpphsbi3"));

		// ResponseStruct result = searchSubtitles("5jm0j0gic050v6t15mgqacbkl6",
		// Lang.PL, "7d9cd5def91c9432", 735934464);
		// System.out.println(result);
		// for (ResponseField field : result.getFieldsForName("IDSubtitleFile"))
		// System.out.println(field);

		// ResponseStruct response = downloadSubtitles(
		// "gt4uf2kh0s2j9qeljvh1nt4jo6", "1951948416");
		// System.out.println(FileHelper.ungzipData(FileHelper
		// .base64ToByteArray(response.getFieldsForName("data").get(0)
		// .getValue())));
		// FileHelper.saveBase64File(new File("out.gzip"), response
		// .getFieldsForName("data").get(0).getValue());
		// FileHelper.saveBase64UngzippedFile(new File("out.txt"), response
		// .getFieldsForName("data").get(0).getValue());

		System.out
				.println(downloadSubtitles(
						"gt4uf2kh0s2j9qeljvh1nt4jo6",
						new File(
								"F:\\Maciek\\Videos\\Conspiracy (2001)\\Conspiracy.avi")));

		// System.out.println("\"" + "7za.exe\" x -y -so -piBlm8NTigvru0Jr0 \""
		// + "File" + ".7z\" > \"" + "File" + "\"");
	}
}
