package net.protocols.exception;

import net.snapshot.ITrafficSnapshot;

/**
 * Exception caused by a parse error while processing a snapshot
 * @author Javier Verde
 *
 */
public class ParseSnapshotException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private ITrafficSnapshot snapshot;
	private int position;

	/**
	 * VV: Creates a parse exception
	 * @param snapshot snapshot used
	 * @param layer layer where the error happened
	 * @param position position from total where error happened
	 */
	public ParseSnapshotException(ITrafficSnapshot snapshot, String layer, int position) {
		super(composeMsg(layer, position));
		this.snapshot = snapshot == null? null: snapshot.getFullSnapshot();	
		this.position = position;
	}
	
	/**
	 * Composes message for error handling
	 * @param layer layer where it happened
	 * @param position position where it happened
	 * @return message
	 */
	private static String composeMsg(String layer, int position) {
		StringBuilder sb = new StringBuilder("Parse error at layer ");
		sb.append(layer);		 
		sb.append(" Position: ");
		sb.append(position);
		return sb.toString();				
	}

	/**
	 * Gets the snapshot where the error happened. Can be null
	 * @return snapshot
	 */
	public ITrafficSnapshot getSnapshot() {
		return snapshot;

	}

	/**
	 * VV: Gets the position where the error happened
	 * @return position
	 */
	public int getPosition() {
		return position;
	}
	
	
}
