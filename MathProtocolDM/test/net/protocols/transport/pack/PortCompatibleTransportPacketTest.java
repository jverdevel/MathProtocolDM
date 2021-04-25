package net.protocols.transport.pack;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.snapshot.ITrafficSnapshot;
import nl.jqno.equalsverifier.EqualsVerifier;

public class PortCompatibleTransportPacketTest {

	@Test
	public void checkCorrectPackage() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		PortCompatibleTransportPacket pack = new PortCompatibleTransportPacket("a", "b", snapshot);
		Assert.assertEquals("a", pack.getOriginPort());
		Assert.assertEquals("b", pack.getDestinationPort());
		Assert.assertEquals(snapshot, pack.getApplicationLayerData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullOrigin() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new PortCompatibleTransportPacket(null, "a", snapshot);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullDestination() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new PortCompatibleTransportPacket("a", null, snapshot);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullData() {
		new PortCompatibleTransportPacket("a", "b", null);
	}

	@Test
	public void checkEquals() {
		EqualsVerifier.simple().forClass(PortCompatibleTransportPacket.class).verify();
	}

}
