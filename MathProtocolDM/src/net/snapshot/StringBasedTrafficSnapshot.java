package net.snapshot;

/**
 * VV: Traffic snapshot based on a string.
 * 
 * @author Javier Verde
 *
 */
public class StringBasedTrafficSnapshot extends DefaultTrafficSnapshot {
	private String string;

	/**
	 * Creates a traffic snapshot backed by a string
	 * 
	 * @param array byte array
	 */
	public StringBasedTrafficSnapshot(String string) {
		this(string, 0, string == null ? 0 : string.length());
	}

	/**
	 * Creates a traffic snapshot backed by a string
	 * 
	 * @param string string
	 * @param start  First CHARACTER (not byte!) considered part of the snapshot.
	 *               Inclusive
	 * @param end    Last CHARACTER (not byte!) considered part of the snapshot.
	 *               Exclusive
	 */
	private StringBasedTrafficSnapshot(String string, int start, int end) {
		super(start, end);
		this.validateInput(string);
		this.string = string;
	}

	/**
	 * Validates the input is correct, and throws an exception in other case
	 * 
	 * @param array backing array
	 */
	private void validateInput(String string) {
		if (string == null) {
			throw new IllegalArgumentException("Backing string cannot be null");
		}
	}

	@Override
	public String getString(int from, int to) {
		this.checkValidIndexes(from, to);
		return string.substring(start + from, start + to);
	}

	@Override
	public ITrafficSnapshot getSnapshotFragment(int from, int to) {
		this.checkValidIndexes(from, to);
		return new StringBasedTrafficSnapshot(string, from + this.start, to + this.start);
	}

	@Override
	public ITrafficSnapshot getFullSnapshot() {
		return new StringBasedTrafficSnapshot(string, 0, string.length());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringBasedTrafficSnapshot other = (StringBasedTrafficSnapshot) obj;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return true;
	}

}
