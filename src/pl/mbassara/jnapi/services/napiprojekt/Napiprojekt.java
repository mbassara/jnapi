package pl.mbassara.jnapi.services.napiprojekt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pl.mbassara.jnapi.Global;
import pl.mbassara.jnapi.services.FileHelper;
import pl.mbassara.jnapi.services.HTTPHelper;
import pl.mbassara.jnapi.services.ISubtitlesProvider;
import pl.mbassara.jnapi.services.Lang;
import pl.mbassara.jnapi.services.SubtitlesResult;

/**
 * This class allows requesting napiprojekt data base for subtitles to given
 * movie file.
 * 
 * @author maciek
 * 
 */
public class Napiprojekt implements ISubtitlesProvider {

	public enum Mode {
		SUBS, COVER, SUBS_COVER
	}

	private static final String napiUrl = "http://www.napiprojekt.pl/api/api-napiprojekt3.php";

	private static String getBody(String hash, Mode mode, Lang lang) {
		String result = "downloaded_subtitles_lang=";
		result += lang
				+ "&downloaded_subtitles_txt=1&client_ver=1.0&downloaded_subtitles_id="
				+ hash;
		result += "&client=AutoMove&mode=" + (mode.ordinal() + 1);
		result += "&downloaded_cover_id=" + hash;

		return result;
	}

	/**
	 * 
	 * @param file
	 *            File for which subtitles should be downloaded
	 * @param mode
	 *            Available modes: SUBS, COVER, SUBS_COVER
	 * @return NapiResult object representing server's response for this request
	 * @throws FileNotFoundException
	 */
	public static NapiResult request(File file, Mode mode, Lang lang)
			throws FileNotFoundException {
		String body = getBody(FileHelper.getHash(file), mode, lang);
		String xmlResponse = HTTPHelper.sendNapiprojektRequest(napiUrl, body);

		NapiXMLHandler handler = new NapiXMLHandler();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(
					new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")),
					handler);
		} catch (ParserConfigurationException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
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

	@Override
	public ArrayList<SubtitlesResult> downloadSubtitles(final File movieFile,
			Lang lang) throws FileNotFoundException {
		final NapiResult result = Napiprojekt.request(movieFile,
				Mode.SUBS_COVER, lang);

		ArrayList<SubtitlesResult> list = new ArrayList<SubtitlesResult>();
		if (result.isStatus())
			list.add(new SubtitlesResult() {

				@Override
				public String getProviderName() {
					return "Napiprojekt";
				}

				@Override
				public String getMovieReleaseName() {
					return result.getTitle();
				}

				@Override
				public String getMovieName() {
					return result.getTitle();
				}

				@Override
				public boolean isFound() {
					return result.isStatus();
				}

				@Override
				public String getSubtitlesAsString() {
					return FileHelper.decodeBase64TextData(result
							.getSubsAsciiBin());
				}

				@Override
				protected File getMovieFile() {
					return movieFile;
				}

				@Override
				public Object getRawResult() {
					return result;
				}
			});

		return list;
	}
}
