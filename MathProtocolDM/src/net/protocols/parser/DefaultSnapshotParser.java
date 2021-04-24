package net.protocols.parser;

/**
 * Parser that extracts package information from a network traffic snapshot
 * @author Javier Verde
 *
 */
public abstract class DefaultSnapshotParser {
	
	/**
	 * Counts the times a character appears in a string
	 * @param string string 
	 * @param character character to count
	 * @return count of the character
	 */
	public final long countStringCharacters(String string, char character) {
		return string.chars().filter(c -> c == character).count();
	}

}
