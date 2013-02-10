package pl.mbassara.napi.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * This class allows requesting napiprojekt data base for subtitles to given
 * movie file.
 * 
 * @author maciek
 * 
 */
public abstract class Napiprojekt {

	public enum Mode {
		SUBS, COVER, SUBS_COVER
	}

	private static final String napiUrl = "http://www.napiprojekt.pl/api/api-napiprojekt3.php";

	private static String getBody(String hash, Mode mode) {
		String result = "downloaded_subtitles_lang=PL&downloaded_subtitles_txt=1&client_ver=1.0&downloaded_subtitles_id="
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
	public static NapiResult request(File file, Mode mode)
			throws FileNotFoundException {
		String body = getBody(NapiFileHelper.getHash(file), mode);
		String xmlResponse = NapiHTTPHelper.sendRequest(napiUrl, body);

		NapiXMLHandler handler = new NapiXMLHandler();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(
					new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")),
					handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return handler.getResult();
	}

}
