package html_words_counter;

import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;

/**
 *Encapsulates HTML content downloading method.
 */
public class ContentDownloader {
	private static final String USER_AGENT = "Mozilla/5.0"
	    + " (Windows NT 10.0; Win64; x64; rv:80.0)"
		+ " Gecko/20100101 Firefox/80.0";

	/**
	 * Downloads page content.
	 * @param url of content to be downloaded. 
	 * @param path - output file path.
	 * @return long, number of bytes downloaded.
	 * @throws java.io.IOException thrown in the following cases:
	 * URL is malformed, creation attempt of URL object, URLConnection, 
	 * InputStream from URLConnection fails, timeout is occurred on a 
	 * socket read or accept, file copying fails.
	 * @throws java.nio.file.InvalidPathException - unchecked, thrown
	 * when String to Path conversion fails.
	 */
	public static long download(String url, String path)
			throws java.io.IOException {
		URLConnection connection = new URL(url).openConnection(); 
		connection.setRequestProperty("User-Agent", USER_AGENT);
		Path outputPath = Paths.get(path);
		return Files.copy(connection.getInputStream(), outputPath,
				StandardCopyOption.REPLACE_EXISTING);

	}
}
