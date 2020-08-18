package html_words_counter;

import java.util.HashMap;

/**
 * Main application class.
 */
public class App {
	/**
	 * Main function counts words in HTML text content.
	 * Either processes given local HTML document or downloads, stores and
	 * processes HTML content found at given URL.
	 * For simplicity catches all exceptions thrown on each step of 
	 * content processing.
	 */
	public static void main(String[] args) {
		String HTMLFilePath = "";
		switch (args.length) {
			case 2:
				try {
					ContentDownloader.download(args[0], args[1]);
					HTMLFilePath = args[1];
				}
				catch (Exception e) {
					printDownloadingErrorMessage(e, args[0], args[1]);
				}
				break;
			case 1:
				HTMLFilePath = args[0];
				break;
			case 0:
				printUsage();
				return;
			default:
				printUnexpectedArgumentsMessage();
				printUsage();
				return;
		}
		
		try (HTMLWordsScanner scanner = new HTMLWordsScanner(HTMLFilePath)) {
			HashMap<String, Integer> wordsDist = new HashMap<>();
			while (scanner.hasNext()) {
				String word = scanner.next();
				wordsDist.put(word, wordsDist.getOrDefault(word, 0) + 1);
			}
			printWordsDistribution(wordsDist);
		}
		catch (Exception e) {
			printScanningErrorMessage(e, HTMLFilePath);		
		}
	}

	private static void printWordsDistribution(
			HashMap<String, Integer> wordsDist) {
		wordsDist.forEach((word, count) ->
					      System.out.format("%s - %d\n", word, count));
	}

	private static void printUsage() {
		System.out.println(
		  "\nApplication usage scenarios:\n"

		+ "Download page content from <URL> to <HTML-document-path>"
		+ " and print distribution of unique text words:\n"
		+ "\tjava -jar <app-name>.jar <URL> <HTML-document-path>\n"		
		
		+ "\nPrint distribution of unique text words from HTML document"
		+ " <HTML-document-path>:\n"
		+ "\tjava -jar <app-name>.jar <HTML-document-path>\n"

		+ "Show this message:\n"
		+ "\tjava -jar <app-name>.jar\n"
		+ "\n"
		);
	}

	private static void printDownloadingErrorMessage(Exception e,
			String url, String path) {
		System.out.printf(
			"Unexpected - Failed to download content from %s to %s",
			url, path);
		System.out.println("Exception: " + e);
	}

	private static void printUnexpectedArgumentsMessage() {
		System.out.println("Unexpected arguments count\n");
	}

	private static void printScanningErrorMessage(Exception e,
			String path) {
		System.out.println("Unexpected - Failed to parse " + path);
		System.out.println("Exception: " + e);
	}
}

