package net.processed;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CommTest {

	private static final String EXAMPLE_PORT = "p";

	@Test
	public void checkValidCommTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA", "1", "2");
		Msg msgResponse = this.mockMsg("CBA", "ABC", "2", "1");
		Comm comm = new Comm<>(msgRequest, msgResponse);
		Assert.assertEquals(msgRequest, comm.getRequest());
		Assert.assertEquals(msgResponse, comm.getResponse());
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidRequestTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA", "1", "2");
		new Comm<>(msgRequest, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidResponseTest() {
		Msg msgResponse = this.mockMsg("ABC", "CBA", "1", "2");
		new Comm<>(null, msgResponse);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidMismatchedRequestIPTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA", "1", "2");
		Msg msgResponse = this.mockMsg("CAA", "ABC", "2", "1");
		new Comm<>(msgRequest, msgResponse);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidMismatchedResponseIPTest() {
		Msg msgRequest = this.mockMsg("AAC", "CBA", "1", "2");
		Msg msgResponse = this.mockMsg("CBA", "ABC", "2", "1");
		new Comm<>(msgRequest, msgResponse);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidMismatchedRequestPortTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA", "1", "2");
		Msg msgResponse = this.mockMsg("CBA", "ABC", "3", "1");
		new Comm<>(msgRequest, msgResponse);
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidMismatchedResponsePortTest() {
		Msg msgRequest = this.mockMsg("ABC", "CBA", "4", "2");
		Msg msgResponse = this.mockMsg("CBA", "ABC", "2", "1");
		new Comm<>(msgRequest, msgResponse);
	}

	@Test
	public void checkEquals() {
		EqualsVerifier.simple().forClass(Comm.class).verify();
	}

	private Msg mockMsg(String origin, String destination, String originPort, String destinationPort) {
		Msg msg = Mockito.mock(Msg.class);
		AddressPort originAddress = new AddressPort(origin, originPort);
		AddressPort originDestination = new AddressPort(destination, destinationPort);
		Mockito.when(msg.getOriginAddress()).thenReturn(originAddress);
		Mockito.when(msg.getDestinationAddress()).thenReturn(originDestination);
		return msg;
	}

}
