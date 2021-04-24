package net.protocols.network.pack;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.snapshot.ITrafficSnapshot;
import nl.jqno.equalsverifier.EqualsVerifier;

public class IpBasedNetworkPackageTest {

	@Test
	public void checkCorrectPackage() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		IpBasedNetworkPackage pack = new IpBasedNetworkPackage("anc", "bnc", snapshot);
		Assert.assertEquals("anc", pack.getOriginIp());
		Assert.assertEquals("bnc", pack.getDestinationIp());
		Assert.assertEquals(snapshot, pack.getTransportLayerData());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullOrigin() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new IpBasedNetworkPackage(null, "a", snapshot);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullDestination() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new IpBasedNetworkPackage("a", null, snapshot);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullData() {
		new IpBasedNetworkPackage("a", "b", null);
	}
	
	@Test
	public void checkEquals() {
	    EqualsVerifier.simple().forClass(IpBasedNetworkPackage.class).verify();
	}
	
}
