package pl.mbassara.jnapi.services.napiprojekt;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAXParser handler for napiprojekt XMLs
 * 
 * @author maciek
 * 
 */
public class NapiXMLHandler extends DefaultHandler {

	private NapiResult result;

	private String currentElement = "";

	private boolean isSubtitles = false;
	private boolean isMovie = false;
	private boolean isCountry = false;

	/**
	 * Get NapiResult object created from parsed XML file
	 * 
	 * @return NapiResult for given XML
	 */
	public NapiResult getResult() {
		return result;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();

		result = new NapiResult();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		currentElement = qName;

		if (qName.equals("subtitles"))
			isSubtitles = true;
		else if (qName.equals("movie"))
			isMovie = true;
		else if (qName.equals("country"))
			isCountry = true;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);

		String chars = new String(ch, start, length);

		if (currentElement.equals("status")) {
			if (!isMovie)
				result.setStatus(chars.equals("success"));
			else
				result.setCoverStatus(chars.equals("success"));
		} else if (currentElement.equals("id")) {
			if (isSubtitles)
				result.setHash(chars);
			else
				result.setMovieHash(chars);
		} else if (currentElement.equals("subs_hash"))
			result.setSubsHash(chars);
		else if (currentElement.equals("filesize"))
			result.setFilesize(Integer.parseInt(chars));
		else if (currentElement.equals("author"))
			result.setAuthor(chars);
		else if (currentElement.equals("uploader"))
			result.setUploader(chars);
		else if (currentElement.equals("upload_date")) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			result.setUploadDate(format.parse(chars, new ParsePosition(0)));
		} else if (currentElement.equals("content"))
			result.setSubsAsciiBin(chars);
		else if (currentElement.equals("title"))
			result.setTitle(chars);
		else if (currentElement.equals("year"))
			result.setYear(chars);
		else if (currentElement.equals("pl")) {
			if (isCountry)
				result.setPlCountry(chars);
			else
				result.setPlGenre(chars);
		} else if (currentElement.equals("en")) {
			if (isCountry)
				result.setEnCountry(chars);
			else
				result.setEnGenre(chars);
		} else if (currentElement.equals("direction"))
			result.setDirector(chars);
		else if (currentElement.equals("screenplay"))
			result.setScreenPlay(chars);
		else if (currentElement.equals("cinematography"))
			result.setCinematography(chars);
		else if (currentElement.equals("music"))
			result.setMusic(chars);
		else if (currentElement.equals("tv_series"))
			result.setTvSeries(!chars.equals("0"));
		else if (currentElement.equals("filmweb_pl"))
			result.setFilmweb(chars);
		else if (currentElement.equals("rating"))
			result.setRating(Integer.parseInt(chars));
		else if (currentElement.equals("votes"))
			result.setVotes(Integer.parseInt(chars));
		else if (currentElement.equals("cover"))
			result.setCoverAsciiBin(chars);

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);

		currentElement = "";

		if (qName.equals("subtitles"))
			isSubtitles = false;
		else if (qName.equals("movie"))
			isMovie = false;
		else if (qName.equals("country"))
			isCountry = false;
	}

}
