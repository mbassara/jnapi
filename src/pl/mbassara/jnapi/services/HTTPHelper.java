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
	public static String sendRequest(String url, String contentType,
			String data, long timeoutMillis) throws TimeoutException {

		long begTime = System.currentTimeMillis();

		HTTPRequestThread thread = new HTTPRequestThread(url, contentType, data);
		thread.start();

		while (!thread.isResultReady()) {
			if (System.currentTimeMillis() - begTime > timeoutMillis) {
				thread.cancel();
				throw new TimeoutException("Http POST request timeout ("
						+ timeoutMillis + " ms)");
			}

			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return thread.getResponse();
	}

	private static class HTTPRequestThread extends Thread {

		private String url, contentType, data;
		private boolean resultReady = false;
		private StringBuilder result = new StringBuilder();
		private HttpURLConnection urlConnection;
		private OutputStreamWriter out;
		private BufferedReader in;

		public HTTPRequestThread(String url, String contentType, String data) {
			setName("HTTPRequestThread");
			this.url = url;
			this.contentType = contentType;
			this.data = data;
		}

		@Override
		public void run() {
			try {

				urlConnection = (HttpURLConnection) (new URL(url)
						.openConnection());
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("content-type", contentType);
				urlConnection.setRequestProperty("accept", "text/plain");
				urlConnection.connect();

				out = new OutputStreamWriter(urlConnection.getOutputStream());

				out.write(data);
				out.flush();
				out.close();

				in = new BufferedReader(new InputStreamReader(
						urlConnection.getInputStream(), "UTF-8"));
				String tmp;
				while ((tmp = in.readLine()) != null)
					result.append(tmp);

				in.close();
				urlConnection.disconnect();

				resultReady = true;
			} catch (IOException e) {
				Global.getInstance().getLogger().warning(e.toString());
				e.printStackTrace();
			}
		}

		public void cancel() {
			try {
				if (urlConnection != null)
					urlConnection.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public boolean isResultReady() {
			return resultReady;
		}

		public String getResponse() {
			return result.toString();
		}
	}
}
