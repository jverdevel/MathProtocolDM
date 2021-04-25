package net.processed;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MsgTest {
	@Test
	public void validMsgTest() {
		AddressPort origin = Mockito.mock(AddressPort.class);
		AddressPort destination = Mockito.mock(AddressPort.class);
		List list = Mockito.mock(List.class);
		Msg msg = new Msg<>(origin, destination, list);
		Assert.assertEquals(origin, msg.getOriginAddress());
		Assert.assertEquals(destination, msg.getDestinationAddress());
		Assert.assertEquals(list, msg.getApplicationProcessedPackets());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidOriginTest() {
		AddressPort destination = Mockito.mock(AddressPort.class);
		List list = Mockito.mock(List.class);
		new Msg<>(null, destination, list);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidDestinationTest() {
		AddressPort origin = Mockito.mock(AddressPort.class);
		List list = Mockito.mock(List.class);
		new Msg<>(origin, null, list);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidListTest() {
		AddressPort origin = Mockito.mock(AddressPort.class);
		AddressPort destination = Mockito.mock(AddressPort.class);
		new Msg<>(origin, destination, null);
	}
	
	@Test
	public void checkEquals() {
	    EqualsVerifier.simple().forClass(Msg.class).verify();
	}

}
