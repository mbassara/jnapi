package pl.mbassara.napi.connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Helper class which provides simple interface for HTTP requests
 * 
 * @author maciek
 */
public abstract class NapiHTTPHelper {

	/**
	 * 
	 * @param url
	 *            URL of http server
	 * @param data
	 *            data which will be send to server as request body
	 * @return XML containing server's response
	 */
	public static String sendRequest(String url, String data) {
		String result = "";

		try {

			HttpURLConnection urlConnection = (HttpURLConnection) (new URL(url)
					.openConnection());
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
