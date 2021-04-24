package net.processed.stupidMath;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.protocols.application.stupidmath.pack.StupidMathPacket;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StupidMathOpTest {
	
	@Test
	public void checkValidTest() {
		StupidMathPacket packet = Mockito.mock(StupidMathPacket.class);
		StupidMathOp op = new StupidMathOp(packet);
		Assert.assertEquals(packet, op.getPacket());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkNullPacketInvalidTest() {
		new StupidMathOp(null);
	}	

	@Test
	public void checkEquals() {
	    EqualsVerifier.simple().forClass(StupidMathOp.class).verify();
	}

}
