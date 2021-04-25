package net.snapshot;

import org.junit.Assert;
import org.junit.Test;

public abstract class DefaultTrafficSnapshotTest {
	protected static final String TEST_SNAPSHOT = "ThisIsATestArray";

	@Test
	public void validStringTest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		String resultString = snapshot.getString(0, TEST_SNAPSHOT.length());
		Assert.assertEquals(TEST_SNAPSHOT, resultString);
	}

	protected abstract DefaultTrafficSnapshot initSnapshot();

	@Test
	public void getLengthTest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		Assert.assertEquals(TEST_SNAPSHOT.length(), snapshot.getLength());
	}

	@Test
	public void getStringShorterTest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		String result = snapshot.getString(1, 4);
		Assert.assertEquals("his", result);
	}

	@Test
	public void getStringLongerTest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		String result = snapshot.getString(11, 16);
		Assert.assertEquals("Array", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidStringTestToLesserThanFrom() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		snapshot.getString(2, 1);
	}

	@Test
	public void validStringTestToEqualThanFrom() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		String string = snapshot.getString(1, 1);
		Assert.assertEquals("", string);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidStringTestNegativeFrom() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		snapshot.getString(-1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidStringTestToTooBig() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		snapshot.getString(0, 90);
	}

	@Test
	public void validSnapshotFragmentTest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		ITrafficSnapshot fragment = snapshot.getSnapshotFragment(1, 4);
		String result = fragment.getString(0, 3);
		Assert.assertEquals("his", result);
	}

	@Test
	public void fullSnapshortTest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		ITrafficSnapshot fragment = snapshot.getSnapshotFragment(1, 4);
		ITrafficSnapshot fullSnapshot = fragment.getFullSnapshot();
		Assert.assertEquals(TEST_SNAPSHOT, fullSnapshot.getString(0, fullSnapshot.getLength()));
	}

	@Test
	public void twoSnapshotFragmentCallstest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		ITrafficSnapshot fragment = snapshot.getSnapshotFragment(4, 10);
		ITrafficSnapshot twoFragment = fragment.getSnapshotFragment(2, 3);
		String result = twoFragment.getString(0, twoFragment.getLength());
		Assert.assertEquals("A", result);
	}

	@Test
	public void relativePositionCheckTest() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		int unmodifiedResult = snapshot.translateLocalPositionToCompletePosition(4);
		ITrafficSnapshot fragment = snapshot.getSnapshotFragment(2, 7);
		int modifiedResult = fragment.translateLocalPositionToCompletePosition(4);
		Assert.assertEquals(4, unmodifiedResult);
		Assert.assertEquals(6, modifiedResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidFragmentTestToLesserThanFrom() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		snapshot.getSnapshotFragment(2, 1);
	}

	@Test
	public void validFragmentTestToEqualThanFrom() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		snapshot.getSnapshotFragment(1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidFragmentTestNegativeFrom() {
		DefaultTrafficSnapshot snapshot = this.initSnapshot();
		snapshot.getSnapshotFragment(-1, 1);
	}
}
