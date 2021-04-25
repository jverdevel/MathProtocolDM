package net.protocols.network.pack;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.snapshot.ITrafficSnapshot;
import nl.jqno.equalsverifier.EqualsVerifier;

public class IpBasedNetworkPacketTest {

	@Test
	public void checkCorrectPackage() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		IpBasedNetworkPacket pack = new IpBasedNetworkPacket("anc", "bnc", snapshot, 10);
		Assert.assertEquals("anc", pack.getOriginIp());
		Assert.assertEquals("bnc", pack.getDestinationIp());
		Assert.assertEquals(snapshot, pack.getTransportLayerData());
		Assert.assertEquals(10, pack.getTotalLength());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullOrigin() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new IpBasedNetworkPacket(null, "a", snapshot, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullDestination() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new IpBasedNetworkPacket("a", null, snapshot, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullData() {
		new IpBasedNetworkPacket("a", "b", null, 10);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkNegativeLengthData() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new IpBasedNetworkPacket("a", "b", snapshot, -10);
	}
	
	@Test
	public void checkEquals() {
	    EqualsVerifier.simple().forClass(IpBasedNetworkPacket.class).verify();
	}
	
}
