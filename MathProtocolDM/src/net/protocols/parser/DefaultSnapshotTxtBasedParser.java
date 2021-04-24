package net.protocols.parser;

import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser that extracts package information from a text-based network traffic snapshot
 * @author Javier Verde
 *
 */
public abstract class DefaultSnapshotTxtBasedParser {
	protected static final String SEPARATOR = "#";

	/**
	 * Counts the times a character appears in a string
	 * @param string string 
	 * @param character character to count
	 * @return count of the character
	 */
	public final long countStringCharacters(String string, char character) {
		return string.chars().filter(c -> c == character).count();
	}
	
	/**
	 * Processes the length of a txt-like snapshot package using hash as a separator
	 * @param snapshot snapshot 
	 * @param initPointer point where length is supposed to start
	 * @param nameLayer name of the layer, for error handling
	 * @return length results
	 * @throws ParseSnapshotException
	 */
	protected final ProcessedLength processLength(ITrafficSnapshot snapshot, int initPointer, String nameLayer) throws ParseSnapshotException{
		int pointer = initPointer;
		boolean done = false;
		StringBuilder sb = new StringBuilder();
		while(!done) {
			if(pointer >=snapshot.getLength()) {
				throw new ParseSnapshotException(snapshot, nameLayer, pointer, "Couldn't find valid separator '#'");			
			}
			String currentChar = snapshot.getString(pointer, pointer+1);
			if(currentChar.equals(SEPARATOR)) {
				break;
			}
			if(!Character.isDigit(currentChar.codePointAt(0))) {
				throw new ParseSnapshotException(snapshot, nameLayer, pointer, "Invalid length");			
			}
			sb.append(currentChar);			
			pointer++;
		}
		int dataLength = Integer.valueOf(sb.toString());
		return new ProcessedLength(pointer+1, dataLength);
	}
	
	/**
	 * Processed length results
	 * @author Javier Verde
	 *
	 */
	protected final static class ProcessedLength{
		private int start;
		private int length;
		public ProcessedLength(int start, int length) {
			super();
			this.start = start;
			this.length = length;
		}
		
		/**
		 * Point where the next layer data starts
		 * @return start
		 */
		public int getStart() {
			return start;
		}
		
		/**
		 * Length of next layer data
		 * @return length
		 */
		public int getLength() {
			return length;
		}
		
		
	}

}
