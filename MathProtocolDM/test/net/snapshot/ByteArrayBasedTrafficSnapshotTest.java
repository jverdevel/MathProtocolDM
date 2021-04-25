package net.snapshot;

import org.junit.Assert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ByteArrayBasedTrafficSnapshotTest extends DefaultTrafficSnapshotTest {

	private static final byte[] INITIAL_ARRAY = TEST_SNAPSHOT.getBytes();

	@Override
	protected DefaultTrafficSnapshot initSnapshot() {
		return new ByteArrayBasedTrafficSnapshot(INITIAL_ARRAY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullArraySnapshotTest() {
		new ByteArrayBasedTrafficSnapshot(null);
	}

	@Test
	public void checkEquals() {
		EqualsVerifier.simple().forClass(ByteArrayBasedTrafficSnapshot.class).verify();
	}

}
