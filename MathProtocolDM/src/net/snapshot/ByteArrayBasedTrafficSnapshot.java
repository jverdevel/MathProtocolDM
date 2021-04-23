package net.snapshot;

import java.nio.charset.Charset;
import java.util.Arrays;

//TODO: depending on codification answer, this might require a bit of a change. 
/**
 * VV: Traffic snapshot based on a byte array. Assumes default codification
 * 
 * @author Javier Verde
 *
 */
public class ByteArrayBasedTrafficSnapshot implements ITrafficSnapshot {

	private byte[] array;

	private int start;
	private int end;

	private static final int BYTES_PER_CHAR = 1;
	
	private static final String CODIFICATION = "ISO-8859-1";

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
		this.validateInput(array, start, end);
		this.array = array;
		this.start = start;
		this.end = end;
	}

	/**
	 * VAlidates the input is correct, and throws an exception in other case
	 * 
	 * @param array backing array
	 * @param start first character considered part of the snapshot. Inclusive
	 * @param end   Last character considered part of the snapshot
	 */
	private void validateInput(byte[] array, int start, int end) {
		if (array == null) {
			throw new IllegalArgumentException("Backing array cannot be null");
		}
		if (array.length % BYTES_PER_CHAR != 0) {
			throw new IllegalArgumentException("Array doesn't contain pairs of bytes");
		}
		if (start >= end) {
			throw new IllegalArgumentException("end must be greater than start");
		}
		if (end * BYTES_PER_CHAR > array.length) {
			throw new IllegalArgumentException("End cannot be greater than snapshot length");
		}
		if (start < 0) {
			throw new IllegalArgumentException("Start must be a positive value");
		}
	}

	@Override
	public int getLength() {
		return this.end - this.start;
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
		//return new String(tmpByteArray, CODIFICATION);
	}

	/**
	 * Checks that the index position is valid
	 * 
	 * @param from first character considered part of the array
	 * @param to   last character considered part of the array
	 */
	private void checkValidIndexes(int from, int to) {
		if (from >= to) {
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
	public ITrafficSnapshot getSnapshotFragment(int from, int to) {
		this.checkValidIndexes(from, to);
		return new ByteArrayBasedTrafficSnapshot(array, from + this.start, to + this.start);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
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
		ByteArrayBasedTrafficSnapshot other = (ByteArrayBasedTrafficSnapshot) obj;
		if (!Arrays.equals(array, other.array))
			return false;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}

}
