package html_words_counter;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.io.File;
import java.io.Closeable;

/**
 * Mimics scanner interface providing filtered HTML content.
 * Extracts words from all text sections of tags in HTML body of document
 * located in the given path.
 * All tags except enclosing body tag are treated as delimiters.
 * Encapsulates newly extracted word and maintains invariant: the word
 * is empty iff no more words are available.
 * Note: to ensure proper enclosing body tag determination the tag must
 * be separated from other page content by any of the predefined delimiters.
 */
public class HTMLWordsScanner implements Closeable, Iterator<String> {
	private String HTMLDocumentPath;
	private Scanner scanner;
	private boolean isOpen;
	private String nextWord;
	private final String stopTag = "</body>";

	/**
	 * Sets up file scanner and updates internal state with first
	 * extracted word from HTML body.
	 * Note: Skips all HTML content before and after HTML body. Also
	 * skips all body content between opening and closing script tags.
	 * @param path to local HTML document.
	 * @throws NullPointerException if path is null.
	 * @throws java.io.IOException if attempt to create Scanner has failed.
	 * @throws java.util.NoSuchElementException if no more words are available.
	 */
	public HTMLWordsScanner(String path) throws java.io.IOException	{
		HTMLDocumentPath = path;
		scanner = new Scanner(new File(HTMLDocumentPath), "UTF-8");
		isOpen = true;
		nextWord = "";

		scanner.skip(Pattern.compile(".*<body.*?>",
			Pattern.DOTALL | Pattern.CASE_INSENSITIVE));

		String[] delimiters = {
			" ", ",", ".", "!", "?", "\"", ";", ":",
			"\\[", "\\]", "\\(", "\\)", "\n", "\r", "\t"
		};
		String regexDelimiters = "[" + String.join("", delimiters) + "]";
		String regexSkipTag = "<(?!\\s*\\/body).*?>";
		String regexScriptContent = "<\\s*script.*?>.*<\\s*\\/script.*?>";
		Pattern HTMLDelimiterPattern = Pattern.compile(
			String.join("|", regexScriptContent, regexSkipTag, regexDelimiters),
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		scanner.useDelimiter(HTMLDelimiterPattern);

		readNextWord();
	}

	/**
	 * Closes file scanner, marks this scanner as closed.
	 */
	public void close() {
		isOpen = false;
		scanner.close();
	}

	/**
	 * Checks if any word is available.
	 * @return boolean, true if any word is available.
	 * @throws IllegalStateException if action is performed on closed scanner. 
	 */
	public boolean hasNext() {
		if (!isOpen) {
			throw new IllegalStateException("Scanner associated with "
				   	+ HTMLDocumentPath + " is closed");
		}
		return !nextWord.isEmpty();
	}

	/**
	 * Returns previously extracted word and updates internal state with
	 * new word.
	 * @return String, next available nonempty word.
	 * @throws java.util.NoSuchElementException if no more words are available.
	 */
	public String next() {
		if (!hasNext()) {
			throw new java.util.NoSuchElementException("EOF or " + stopTag
					+ " is reached");
		}
		String word = nextWord;
		readNextWord();
		return word;
	}

	private void readNextWord() {
		while (scanner.hasNext()) {
			nextWord = scanner.next();
			if (!nextWord.isEmpty()) {
				if (stopTag.compareToIgnoreCase(nextWord) != 0) {
					return;				
				}

				break;
			}
		}
		nextWord = "";
	}
}
