package pl.mbassara.jnapi.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import pl.mbassara.jnapi.Global;

/**
 * Helper class which provides simple interface for HTTP requests
 * 
 * @author maciek
 */
public abstract class HTTPHelper {

	public static String sendNapiprojektRequest(String url, String data)
			throws TimeoutException {
		return sendRequest(url, "application/x-www-form-urlencoded", data, 5000);
	}

	public static String sendOpenSubtitlesRequest(String url, String data)
			throws TimeoutException {
		return sendRequest(url, "text/xml", data, 5000);
	}

	/**
	 * 
	 * @param url
	 *            URL of http server
	 * @param data
	 *            data which will be send to server as request body
	 * @return XML containing server's response
	 */
	public static String sendRequest(final String url,
			final String contentType, final String data, long timeoutMillis)
			throws TimeoutException {

		final StringBuilder result = new StringBuilder();
		final BooleanWrapper isResultReady = new BooleanWrapper(false);
		long begTime = System.currentTimeMillis();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					HttpURLConnection urlConnection = (HttpURLConnection) (new URL(
							url).openConnection());
					urlConnection.setDoOutput(true);
					urlConnection.setRequestMethod("POST");
					urlConnection.setRequestProperty("content-type",
							contentType);
					urlConnection.setRequestProperty("accept", "text/plain");
					urlConnection.connect();

					OutputStreamWriter out = new OutputStreamWriter(
							urlConnection.getOutputStream());

					out.write(data);
					out.flush();
					out.close();

					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									urlConnection.getInputStream(), "UTF-8"));
					String tmp;
					while ((tmp = in.readLine()) != null)
						result.append(tmp);

					in.close();
					urlConnection.disconnect();

					isResultReady.set(true);
				} catch (IOException e) {
					Global.getInstance().getLogger().warning(e.toString());
					e.printStackTrace();
				}

			}
		}, "HttpRequestThread").start();

		while (!isResultReady.get()) {
			if (System.currentTimeMillis() - begTime > timeoutMillis)
				throw new TimeoutException("Http POST request timeout ("
						+ timeoutMillis + " ms)");

			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return result.toString();
	}

	private static class BooleanWrapper {
		private boolean value;

		public BooleanWrapper(boolean value) {
			this.value = value;
		}

		public void set(boolean value) {
			this.value = value;
		}

		public boolean get() {
			return value;
		}
	}
}
