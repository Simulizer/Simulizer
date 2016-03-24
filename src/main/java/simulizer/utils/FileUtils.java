package simulizer.utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Wrappers for accessing files including bundled resources
 * @author mbway
 */
public class FileUtils {

	/**
	 * read the contents of the given file
	 * @param path the path on the filesystem to the file
	 * @return the file contents (read as UTF-8)
	 */
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

	/**
	 * read the contents of a File object
	 * @return the file's contents
	 */
	public static String getFileContent(File f) {
		return getFileContent(f.getPath());
	}

	/**
	 * write to a file (as Java makes this overly bureaucratic)
	 */
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

	/**
	 * transform the path of a resource from inside a Jar to a form that some methods accept.
	 */
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

	/**
	 * Open a file in the default program for the filetype
	 * @param path the file to open
	 */
	public static void openFile(String path) {
		try {
			Thread openFileThread = new Thread(() -> {
				try {
					Desktop.getDesktop().open(new File(path));
				} catch (IOException ignored) {
				}
			}, "OpenFile-Thread");
			openFileThread.setDaemon(true);
			openFileThread.start();

			openFileThread.join(1000);
		} catch (InterruptedException e) {
			UIUtils.showErrorDialog("Failed to Open", "Failed to open the file: \"" + path + "\"");
		}
	}
}
