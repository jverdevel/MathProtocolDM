package net.snapshot;

import java.util.Arrays;

/**
 * VV: Traffic snapshot based on a byte array. Assumes ISO-8859-1 codification
 * 
 * @author Javier Verde
 *
 */
public class ByteArrayBasedTrafficSnapshot extends DefaultTrafficSnapshot {

	private byte[] array;

	private static final int BYTES_PER_CHAR = 1;

	public static final String CODIFICATION = "ISO-8859-1";

	/**
	 * Creates a traffic snapshot backed by a byte array
	 * 
	 * @param array byte array
	 */
	public ByteArrayBasedTrafficSnapshot(byte[] array) {
		this(array, 0, array == null ? 0 : array.length / BYTES_PER_CHAR);
	}

	/**
	 * Creates a traffic snapshot backed by a byte array
	 * 
	 * @param array backing array
	 * @param start First CHARACTER (not byte!) considered part of the snapshot.
	 *              Inclusive
	 * @param end   Last CHARACTER (not byte!) considered part of the snapshot.
	 *              Exclusive
	 */
	private ByteArrayBasedTrafficSnapshot(byte[] array, int start, int end) {
		super(start, end);
		this.validateInput(array);
		this.array = array;
	}

	/**
	 * Validates the input is correct, and throws an exception in other case
	 * 
	 * @param array backing array
	 */
	private void validateInput(byte[] array) {
		if (array == null) {
			throw new IllegalArgumentException("Backing array cannot be null");
		}
		if (array.length % BYTES_PER_CHAR != 0) {
			throw new IllegalArgumentException("Array doesn't contain pairs of bytes");
		}

		if (end * BYTES_PER_CHAR > array.length) {
			throw new IllegalArgumentException("End cannot be greater than snapshot length");
		}
	}

	@Override
	public String getString(int from, int to) {
		this.checkValidIndexes(from, to);

		byte[] tmpByteArray = new byte[(to - from) * BYTES_PER_CHAR];
		int bytePositionFrom = from * BYTES_PER_CHAR + this.start * BYTES_PER_CHAR;
		int bytePositionTo = to * BYTES_PER_CHAR + this.start * BYTES_PER_CHAR;
		for (int i = bytePositionFrom; i < bytePositionTo; i++) {
			tmpByteArray[i - bytePositionFrom] = this.array[i];
		}
		return new String(tmpByteArray);
	}

	@Override
	public ITrafficSnapshot getSnapshotFragment(int from, int to) {
		this.checkValidIndexes(from, to);
		return new ByteArrayBasedTrafficSnapshot(array, from + this.start, to + this.start);
	}

	@Override
	public ITrafficSnapshot getFullSnapshot() {
		return new ByteArrayBasedTrafficSnapshot(array, 0, array.length / BYTES_PER_CHAR);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(array);
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
		ByteArrayBasedTrafficSnapshot other = (ByteArrayBasedTrafficSnapshot) obj;
		if (!Arrays.equals(array, other.array))
			return false;
		return true;
	}

}
