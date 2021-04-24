package net.processed;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CommTest {

	private static final String EXAMPLE_PORT = "p";
	
	@Test
	public void checkValidCommTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA");
		Msg msgResponse = this.mockMsg("CBA", "ABC");
		Comm comm = new Comm<>(msgRequest, msgResponse);
		Assert.assertEquals(msgRequest, comm.getRequest());
		Assert.assertEquals(msgResponse, comm.getResponse());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidRequestTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA");
		new Comm<>(msgRequest, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidResponseTest() {
		Msg msgResponse = this.mockMsg("ABC", "CBA");
		new Comm<>(null, msgResponse);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidMismatchedRequestTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA");
		Msg msgResponse = this.mockMsg("CAA", "ABC");
		new Comm<>(msgRequest, msgResponse);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidMismatchedResponseTest() {
		Msg msgRequest = this.mockMsg("AAC", "CBA");
		Msg msgResponse = this.mockMsg("CBA", "ABC");
		new Comm<>(msgRequest, msgResponse);
	}
	
	
	@Test
	public void checkEquals() {
	    EqualsVerifier.simple().forClass(Comm.class).verify();
	}
	
	private Msg mockMsg(String origin, String destination) {
		Msg msg = Mockito.mock(Msg.class);
		AddressPort mockedOrigin = this.mockAddressPort(origin);
		AddressPort mockedDestination = this.mockAddressPort(destination);
		Mockito.when(msg.getOriginAddress()).thenReturn(mockedOrigin);
		Mockito.when(msg.getDestinationAddress()).thenReturn(mockedDestination);
		return msg;
	}
	
	private AddressPort mockAddressPort(String origin) {
		AddressPort ap = Mockito.mock(AddressPort.class);
		Mockito.when(ap.getIp()).thenReturn(origin);
		return ap;
	}
}
