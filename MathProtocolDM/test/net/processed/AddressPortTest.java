package net.processed;

import org.junit.Assert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AddressPortTest {

	@Test
	public void validAddressTest() {
		AddressPort ap = new AddressPort("ABD", "e");
		Assert.assertEquals("ABD", ap.getIp());
		Assert.assertEquals("e", ap.getPort());
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidIPTest() {
		new AddressPort(null, "e");
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidPortTest() {
		new AddressPort("ABD", null);
	}

	@Test
	public void checkEquals() {
		EqualsVerifier.simple().forClass(AddressPort.class).verify();
	}

}
