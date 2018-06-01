package pl.mbassara.jnapi.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.DatatypeConverter;

import pl.mbassara.jnapi.Global;
import sun.misc.BASE64Decoder;

import com.ibm.icu.text.CharsetDetector;

/**
 * Functions for file operations, used in this app. Especially for computing
 * hash used by napiprojekt API.
 * 
 * @author maciek
 * 
 */
public abstract class FileHelper {

	/**
	 * Calculates hash of first 10MB of given file
	 * 
	 * @param file
	 *            file for which hash is calculated
	 * @return string representation of computed hash
	 * @throws FileNotFoundException
	 */
	public static String getHash(File file) throws FileNotFoundException {
		byte[] buff = new byte[1024];
		FileInputStream stream = null;

		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			stream = new FileInputStream(file);

			for (int i = 0; i < 10240 && stream.available() > 0; i++) {
				int size = stream.read(buff);
				digest.update(buff, 0, size);
			}

			byte[] md5 = digest.digest();
			return DatatypeConverter.printHexBinary(md5).toLowerCase();

		} catch (NoSuchAlgorithmException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					Global.getInstance().getLogger().warning(e.toString());
					e.printStackTrace();
				}
		}

		return null;
	}

	/**
	 * Decodes string with Base64 encoded data.
	 * 
	 * @param base64Data
	 *            Encoded data
	 * @return Decoded data or null if sth goes wrong
	 */
	public static byte[] base64ToByteArray(String base64Data) {
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			return decoder.decodeBuffer(base64Data);
		} catch (IOException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Decodes Base64 data and saves it into file.
	 * 
	 * @param file
	 *            Destination where decoded Base64 data will be saved.
	 * @param base64Data
	 *            Data to save.
	 * @return true if everything went fine, otherwise false
	 */
	public static boolean saveBase64File(File file, String base64Data) {
		try {
			OutputStream stream = new FileOutputStream(file);
			stream.write(base64ToByteArray(base64Data));
			stream.flush();
			stream.close();

			return true;
		} catch (IOException e) {
			Global.getInstance().getLogger().warning(e.toString());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Decodes Base64 data returns it as String.
	 * 
	 * @param base64Data
	 *            Data to decode.
	 * @return String representation of Base64 decoded data.
	 */
	public static String decodeBase64TextData(String base64Data) {
		try {
			byte[] data = base64ToByteArray(base64Data);
			CharsetDetector detector = new CharsetDetector();
			detector.setText(data);
			return detector.detect().getString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String ungzipData(byte[] data, String charset) {

		char[] buff = new char[1024];

		try {

			InputStreamReader input = new InputStreamReader(
					new GZIPInputStream(new ByteArrayInputStream(data)),
					charset);

			StringBuilder out = new StringBuilder();

			int len;
			while ((len = input.read(buff)) > 0) {
				out.append(buff, 0, len);
			}

			input.close();
			return out.toString();

		} catch (IOException ex) {
			Global.getInstance().getLogger().warning(ex.toString());
			ex.printStackTrace();
		}

		return null;
	}

	/**
	 * Decodes Base64 data, decompress it from gzip and saves it into file.
	 * 
	 * @param file
	 *            Destination where decoded Base64 data will be saved.
	 * @param base64Data
	 *            Data to save.
	 */
	public static void saveBase64UngzippedFile(File file, String base64Data,
			String charsetName) {

		try {

			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(file), charsetName);

			out.write(ungzipData(base64ToByteArray(base64Data), charsetName));
			out.flush();

			out.close();

		} catch (IOException ex) {
			Global.getInstance().getLogger().warning(ex.toString());
			ex.printStackTrace();
		}
	}
}