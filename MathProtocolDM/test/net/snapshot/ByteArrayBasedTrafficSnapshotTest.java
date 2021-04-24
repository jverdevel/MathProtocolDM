package net.snapshot;

import org.junit.Assert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ByteArrayBasedTrafficSnapshotTest {

	private static final String TEST_SNAPSHOT = "ThisIsATestArray";
	private static final byte[] INITIAL_ARRAY = new String(TEST_SNAPSHOT).getBytes();

	@Test
	public void validStringTest() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		String resultString = snapshot.getString(0, TEST_SNAPSHOT.length());
		Assert.assertEquals(TEST_SNAPSHOT, resultString);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullArraySnapshotTest() {
		new ByteArrayBasedTrafficSnapshot(null);
	}

	@Test
	public void checkEquals() {
		EqualsVerifier.simple().forClass(ByteArrayBasedTrafficSnapshot.class).verify();
	}

	@Test
	public void getLengthTest() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		Assert.assertEquals(TEST_SNAPSHOT.length(), snapshot.getLength());
	}

	@Test
	public void getStringShorterTest() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		String result = snapshot.getString(1, 4);
		Assert.assertEquals("his", result);
	}

	@Test
	public void getStringLongerTest() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		String result = snapshot.getString(11, 16);
		Assert.assertEquals("Array", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidStringTestToLesserThanFrom() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		snapshot.getString(2, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidStringTestToEqualThanFrom() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		snapshot.getString(1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidStringTestNegativeFrom() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		snapshot.getString(-1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidStringTestToTooBig() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		snapshot.getString(0, 90);
	}

	@Test
	public void validSnapshotFragmentTest() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		ITrafficSnapshot fragment = snapshot.getSnapshotFragment(1, 4);
		String result = fragment.getString(0, 3);
		Assert.assertEquals("his", result);
	}
	
	@Test
	public void fullSnapshortTest() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		ITrafficSnapshot fragment = snapshot.getSnapshotFragment(1, 4);
		ITrafficSnapshot fullSnapshot = fragment.getFullSnapshot();
		Assert.assertEquals(TEST_SNAPSHOT, fullSnapshot.getString(0, fullSnapshot.getLength()));
	}

	@Test
	public void twoSnapshotFragmentCallstest() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		ITrafficSnapshot fragment = snapshot.getSnapshotFragment(4, 10);
		ITrafficSnapshot twoFragment = fragment.getSnapshotFragment(2, 3);
		String result = twoFragment.getString(0, twoFragment.getLength());
		Assert.assertEquals("A", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidFragmentTestToLesserThanFrom() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		snapshot.getSnapshotFragment(2, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidFragmentTestToEqualThanFrom() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		snapshot.getSnapshotFragment(1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidFragmentTestNegativeFrom() {
		ByteArrayBasedTrafficSnapshot snapshot = new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
		snapshot.getSnapshotFragment(-1, 1);
	}

}
