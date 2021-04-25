package net.protocols.application.stupidmath.pack;

import org.junit.Assert;
import org.junit.Test;

import net.protocols.application.stupidmath.pack.StupidMathPacket;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StupidMathPacketTest {

	@Test
	public void checkValidConstructorSum() {
		this.checkValidConstructor("+", 3, 4);
	}

	@Test
	public void checkValidConstructorMinus() {
		this.checkValidConstructor("-", 5, 4);
	}

	@Test
	public void checkValidConstructorProduct() {
		this.checkValidConstructor("*", 0, 9);
	}

	@Test
	public void checkValidConstructorDivide() {
		this.checkValidConstructor("/", 8, 4);
	}

	@Test
	public void checkValidConstructorEquals() {
		this.checkValidConstructor("=", 3, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOperationConstruct() {
		new StupidMathPacket("#", 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkNullOperationConstruct() {
		new StupidMathPacket(null, 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOperand1() {
		new StupidMathPacket("+", -4, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOperand2() {
		new StupidMathPacket("+", 4, -5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInValidEqualsOperand2() {
		new StupidMathPacket("=", 4, 5);
	}

	@Test
	public void checkEquals() {
		EqualsVerifier.simple().forClass(StupidMathPacket.class).verify();
	}

	public void checkValidUsePreviousOperand() {
		this.checkValidConstructor("+", StupidMathPacket.USE_PREVIOUS_OPERAND, StupidMathPacket.USE_PREVIOUS_OPERAND);
	}

	private void checkValidConstructor(String operation, int operand1, int operand2) {
		StupidMathPacket op = new StupidMathPacket(operation, operand1, operand2);
		Assert.assertEquals(operation, op.getOperation());
		Assert.assertEquals(operand1, op.getOperand1());
		Assert.assertEquals(operand2, op.getOperand2());
	}

}
