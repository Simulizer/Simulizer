package simulizer.utils;

import org.antlr.v4.runtime.ParserRuleContext;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Wrappers for accessing files including bundled resources
 * @author mbway
 */
public class FileUtils {

	public static String getFileContent(String path) {
		FileInputStream fis;
		try {
			File file = new File(path);
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			//noinspection ResultOfMethodCallIgnored
			fis.read(data);
			fis.close();

			return new String(data, "UTF-8");

		} catch (IOException e) {
			UIUtils.showExceptionDialog(e);
		}

		return null;
	}
	public static String getFileContent(File f) {
		return getFileContent(f.getPath());
	}

	public static void writeToFile(File file, String content) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			UIUtils.showExceptionDialog(e);
		}
	}

	/**
	 * Get the real path of a bundled resource
	 * @param path A path relative to src/main/resources. Note: the path MUST start with '/'
	 */
	public static String getResourcePath(String path) {
		assert path.charAt(0) == '/'; // must start with a slash
		try {
			return FileUtils.class.getResource(path).toURI().toString();
		} catch (Exception e) {
			UIUtils.showExceptionDialog(e);
			return "";
		}
	}
	public static String getResourceToExternalForm(String path) {
		assert path.charAt(0) == '/'; // must start with a slash
		try {
			return FileUtils.class.getResource(path).toExternalForm();
		} catch (Exception e) {
			UIUtils.showExceptionDialog(e);
			return "";
		}
	}


	/**
	 * get the content of a bundled resource. This is not built in...
	 * Wow I hate java so much...
	 * @param path A path relative to src/main/resources. Note: the path MUST start with '/'
	 * @return hopefully the content, decoded from UTF-8
	 */
	public static String getResourceContent(String path) {
		StringBuilder sb = new StringBuilder();

		assert path.charAt(0) == '/'; // must start with a slash

		BufferedReader br = null;
		try {
			InputStream in = FileUtils.class.getResourceAsStream(path);
			if(in != null) {
				br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			} else {
				throw new FileNotFoundException(path);
			}
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			UIUtils.showExceptionDialog(e);
		}

		try {
			assert br != null;
			for (int c = br.read(); c != -1; c = br.read())
				sb.append((char) c);
		} catch (IOException e) {
			UIUtils.showExceptionDialog(e);
		}

		return sb.toString();
	}

}
