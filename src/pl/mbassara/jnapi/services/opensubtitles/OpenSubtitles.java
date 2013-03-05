package pl.mbassara.jnapi.services.opensubtitles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pl.mbassara.jnapi.Global;
import pl.mbassara.jnapi.gui.MediaFileFilter;
import pl.mbassara.jnapi.gui.SubtitlesCharset;
import pl.mbassara.jnapi.services.FileHelper;
import pl.mbassara.jnapi.services.HTTPHelper;
import pl.mbassara.jnapi.services.ISubtitlesProvider;
import pl.mbassara.jnapi.services.Lang;
import pl.mbassara.jnapi.services.SubtitlesResult;
import pl.mbassara.jnapi.services.napiprojekt.Napiprojekt;
import pl.mbassara.jnapi.services.opensubtitles.parameters.ArrayValue;
import pl.mbassara.jnapi.services.opensubtitles.parameters.Member;
import pl.mbassara.jnapi.services.opensubtitles.parameters.SingleValue;
import pl.mbassara.jnapi.services.opensubtitles.parameters.StructValue;
import pl.mbassara.jnapi.services.opensubtitles.parameters.Value;

public class OpenSubtitles implements ISubtitlesProvider {

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
			Value[] parameters) throws TimeoutException {
		String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodCall><methodName>"
				+ methodName + "</methodName>";

		if (parameters.length > 0)
			request += "<params>";

		for (Value param : parameters)
			request += "<param>" + param + "</param>";

		if (parameters.length > 0)
			request += "</params>";

		request += "</methodCall>";

		String response = HTTPHelper.sendOpenSubtitlesRequest(URL, request);
		OpenSubtitlesXMLHandler handler = new OpenSubtitlesXMLHandler();
		try {
			parser.parse(new ByteArrayInputStream(response.getBytes("UTF-8")),
					handler);
		} catch (UnsupportedEncodingException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		} catch (SAXException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		}

		return handler.getResult();
	}

	public static String logIn() throws TimeoutException {
		ResponseStruct response = logIn("", "", "en", "JNapi v0.1");
		if (isResponseOK(response))
			return response.getFieldsForName("token").get(0).getValue();
		else
			return null;
	}

	public static ResponseStruct logIn(String username, String password,
			String language, String useragent) throws TimeoutException {

		return callMethod("LogIn", new Value[] { new SingleValue(username),
				new SingleValue(password), new SingleValue(language),
				new SingleValue(useragent) });
	}

	public static boolean logOut(String token) throws TimeoutException {
		return isResponseOK(callMethod("LogOut", new Value[] { new SingleValue(
				token) }));
	}

	public static ResponseStruct searchSubtitles(String token, Lang lang,
			String movieHash, long fileSize) throws TimeoutException {
		String l = lang == Lang.PL ? "pol" : "eng";
		return callMethod("SearchSubtitles", new Value[] {
				new SingleValue(token),
				new ArrayValue(new Value[] { new StructValue(new Member[] {
						new Member("sublanguageid", new SingleValue(l)),
						new Member("moviehash", new SingleValue(movieHash)),
						new Member("moviebytesize", new SingleValue("double",
								fileSize + "")) }) }) });
	}

	public static ResponseStruct searchSubtitles(String token, File movieFile,
			Lang lang) throws TimeoutException {
		try {
			String movieHash = OpenSubtitlesHasher.computeHash(movieFile);
			long fileSize = movieFile.length();

			return searchSubtitles(token, lang, movieHash, fileSize);
		} catch (IOException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		}
		return null;
	}

	public static ResponseStruct downloadSubtitles(String token,
			String idSubtitleFile) throws TimeoutException {
		return callMethod("DownloadSubtitles",
				new Value[] {
						new SingleValue(token),
						new ArrayValue(new Value[] { new SingleValue(
								idSubtitleFile) }) });
	}

	public static ResponseStruct downloadSubtitles(String token,
			File movieFile, Lang lang) throws TimeoutException {
		ResponseStruct response = searchSubtitles(token, movieFile, lang);
		if (!isResponseOK(response)
				|| response.getFieldsForName("IDSubtitleFile").size() == 0)
			return null;

		String idSubtitleFile = response.getFieldsForName("IDSubtitleFile")
				.get(0).getValue();
		response = downloadSubtitles(token, idSubtitleFile);
		if (!isResponseOK(response))
			return null;

		return response;
	}

	private static boolean isResponseOK(ResponseStruct response) {
		if (response == null)
			return false;
		ArrayList<ResponseField> fields = response.getFieldsForName("status");
		if (fields.size() == 0)
			return false;

		return fields.get(0).getValue().equals("200 OK");
	}

	@Override
	public ArrayList<SubtitlesResult> downloadSubtitles(final File movieFile,
			Lang lang) throws FileNotFoundException, TimeoutException {
		String token = logIn();
		ResponseStruct response = OpenSubtitles.searchSubtitles(token,
				movieFile, lang);
		logOut(token);

		if (!isResponseOK(response))
			return null;

		ArrayList<SubtitlesResult> list = new ArrayList<SubtitlesResult>();

		for (final ResponseStruct struct : response.getSubResponseStructs()) {
			list.add(new SubtitlesResult() {

				private String idSubtitleFile = struct
						.getFieldsForName("IDSubtitleFile").get(0).getValue();
				private String subtitlesString = null;

				@Override
				public boolean isFound() {
					return true;
				}

				@Override
				public String getSubtitlesAsString() throws TimeoutException {
					if (subtitlesString != null)
						return subtitlesString;

					String token = logIn();
					ResponseStruct responseStruct = downloadSubtitles(token,
							idSubtitleFile);
					logOut(token);

					if (!isResponseOK(responseStruct))
						return null;

					subtitlesString = FileHelper.ungzipData(
							FileHelper
									.base64ToByteArray(responseStruct
											.getFieldsForName("data").get(0)
											.getValue()), SubtitlesCharset.UTF8
									.toString());

					return subtitlesString;
				}

				@Override
				public String getProviderName() {
					return "Opensubtitles.org";
				}

				@Override
				public String getMovieReleaseName() {
					return struct.getFieldsForName("MovieReleaseName").get(0)
							.getValue();
				}

				@Override
				public String getMovieName() {
					return struct.getFieldsForName("MovieName").get(0)
							.getValue();
				}

				@Override
				protected File getMovieFile() {
					return movieFile;
				}

				@Override
				public Object getRawResult() {
					return struct;
				}

			});
		}

		return list;
	}

	public static void main(String[] args) throws FileNotFoundException,
			TimeoutException {

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

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileFilter(new MediaFileFilter());
		int result = chooser.showOpenDialog(null);

		if (result != JFileChooser.APPROVE_OPTION)
			return;

		System.out.println(searchSubtitles(logIn(), chooser.getSelectedFile(),
				Lang.PL));
		System.out.println("results from OS:\t"
				+ new OpenSubtitles().downloadSubtitles(
						chooser.getSelectedFile(), Lang.PL).size());
		System.out.println("results from napi:\t"
				+ new Napiprojekt().downloadSubtitles(
						chooser.getSelectedFile(), Lang.PL).size());

		// System.out.println("\"" + "7za.exe\" x -y -so -piBlm8NTigvru0Jr0 \""
		// + "File" + ".7z\" > \"" + "File" + "\"");
	}
}
