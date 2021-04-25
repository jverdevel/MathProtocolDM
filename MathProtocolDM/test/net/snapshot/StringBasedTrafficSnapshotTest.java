package net.snapshot;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class StringBasedTrafficSnapshotTest extends DefaultTrafficSnapshotTest {

	@Test(expected = IllegalArgumentException.class)
	public void nullStringSnapshotTest() {
		new StringBasedTrafficSnapshot(null);
	}

	@Test
	public void checkEquals() {
		EqualsVerifier.simple().forClass(StringBasedTrafficSnapshot.class).verify();
	}

	@Override
	protected DefaultTrafficSnapshot initSnapshot() {
		return new StringBasedTrafficSnapshot(TEST_SNAPSHOT);
	}
}
