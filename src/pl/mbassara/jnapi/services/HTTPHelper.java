package pl.mbassara.jnapi.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pl.mbassara.jnapi.Global;

/**
 * Helper class which provides simple interface for HTTP requests
 * 
 * @author maciek
 */
public abstract class HTTPHelper {

	public static String sendNapiprojektRequest(String url, String data) {
		return sendRequest(url, "application/x-www-form-urlencoded", data);
	}

	public static String sendOpenSubtitlesRequest(String url, String data) {
		return sendRequest(url, "text/xml", data);
	}

	/**
	 * 
	 * @param url
	 *            URL of http server
	 * @param data
	 *            data which will be send to server as request body
	 * @return XML containing server's response
	 */
	public static String sendRequest(String url, String contentType, String data) {
		String result = "";

		try {

			HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url)
					.openConnection());
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("content-type", contentType);
			urlConnection.setRequestProperty("accept", "text/plain");
			urlConnection.connect();

			OutputStreamWriter out = new OutputStreamWriter(
					urlConnection.getOutputStream());

			out.write(data);
			out.flush();
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream(), "UTF-8"));
			String tmp;
			while ((tmp = in.readLine()) != null)
				result += tmp;

			in.close();
			urlConnection.disconnect();
		} catch (MalformedURLException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		}

		return result;
	}

}
