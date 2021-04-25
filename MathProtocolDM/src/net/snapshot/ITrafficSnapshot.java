package net.snapshot;

/**
 * Representation of a network traffic snapshot with a string-based protocol
 * 
 * @author Javier Verde
 *
 */
public interface ITrafficSnapshot {

	/**
	 * Length of the snapshot.
	 * 
	 * @return length
	 */
	public int getLength();

	/**
	 * VV: Gets the string at the indicated position. Please notice this WILL copy
	 * the values in a different String, so only use it for final processing
	 * 
	 * @param from start position (included)
	 * @param to   end position (excluded)
	 * @return string
	 */
	public String getString(int from, int to);

	/**
	 * VV: Gets another fragment that goes from one position to other.
	 * Implementation should NOT duplicate the data source under any circustances.
	 * 
	 * @param from start position (included)
	 * @param to
	 * @return end position (excluded)
	 */
	public ITrafficSnapshot getSnapshotFragment(int from, int to);

	/**
	 * Gets the full snapshot. Implementation should NOT duplicate the data source
	 * under any circustances
	 * 
	 * @return snapshot
	 */
	public ITrafficSnapshot getFullSnapshot();

	/**
	 * Obtains the position related to the full snapshot from the relative position
	 * 
	 * @param relativePosition relative position
	 * @return absolute position
	 */
	public int translateLocalPositionToCompletePosition(int relativePosition);
}
