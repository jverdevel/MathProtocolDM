package net.snapshot;

/**
 * Default implementation of a traffic snapshot
 * 
 * @author Javi
 *
 */
public abstract class DefaultTrafficSnapshot implements ITrafficSnapshot {

	protected int start;
	protected int end;

	/**
	 * Creates a traffic snapshot
	 * 
	 * @param start First CHARACTER considered part of the snapshot. Inclusive
	 * @param end   Last CHARACTER considered part of the snapshot. Exclusive
	 */
	protected DefaultTrafficSnapshot(int start, int end) {
		this.validateInput(start, end);

		this.start = start;
		this.end = end;
	}

	/**
	 * Validates the input is correct, throws an exception if it's not
	 * 
	 * @param start first character considered part of the snapshot. Inclusive
	 * @param end   Last character considered part of the snapshot
	 */
	private void validateInput(int start, int end) {
		if (start > end) {
			throw new IllegalArgumentException("end must be greater than start");
		}
		if (start < 0) {
			throw new IllegalArgumentException("Start must be a positive value");
		}
	}

	/**
	 * Checks that the index position is valid
	 * 
	 * @param from first character considered part of the array
	 * @param to   last character considered part of the array
	 */
	protected final void checkValidIndexes(int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException("to must be greater than from");
		}
		if (from < 0) {
			throw new IllegalArgumentException("from must be a positive value");
		}
		int currentLength = this.end - this.start;
		if (to > currentLength) {
			throw new IllegalArgumentException("to is greater than current length");
		}

	}

	@Override
	public final int getLength() {
		return this.end - this.start;
	}

	@Override
	public int translateLocalPositionToCompletePosition(int relativePosition) {
		return this.start + relativePosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultTrafficSnapshot other = (DefaultTrafficSnapshot) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}

}
