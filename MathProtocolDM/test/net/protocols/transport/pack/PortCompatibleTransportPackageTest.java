package net.protocols.transport.pack;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.snapshot.ITrafficSnapshot;
import nl.jqno.equalsverifier.EqualsVerifier;


public class PortCompatibleTransportPackageTest {
	
	@Test
	public void checkCorrectPackage() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		PortCompatibleTransportPackage pack = new PortCompatibleTransportPackage("a", "b", snapshot);
		Assert.assertEquals("a", pack.getOriginPort());
		Assert.assertEquals("b", pack.getDestinationPort());
		Assert.assertEquals(snapshot, pack.getApplicationLayerData());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullOrigin() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new PortCompatibleTransportPackage(null, "a", snapshot);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullDestination() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		new PortCompatibleTransportPackage("a", null, snapshot);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkIncorrectPackageNullData() {
		new PortCompatibleTransportPackage("a", "b", null);
	}
	
	@Test
	public void checkEquals() {
	    EqualsVerifier.simple().forClass(PortCompatibleTransportPackage.class).verify();
	}
	
	

}
