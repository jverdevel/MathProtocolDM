package net.protocols.application.stupidmath.op;

import org.junit.Assert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class StupidMathOpTest {
	
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
		new StupidMathOp("#", 2, 3);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkNullOperationConstruct() {
		new StupidMathOp(null, 2, 3);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOperand1() {
		new StupidMathOp("+", -4, 5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidOperand2() {
		new StupidMathOp("+", 4, -5);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void checkInValidEqualsOperand2() {
		new StupidMathOp("=", 4, 5);
	}
	
	@Test
	public void checkEquals() {
	    EqualsVerifier.simple().forClass(StupidMathOp.class).verify();
	}
	
	public void checkValidUsePreviousOperand() {
		this.checkValidConstructor("+", StupidMathOp.USE_PREVIOUS_OPERAND, StupidMathOp.USE_PREVIOUS_OPERAND);
	}
	
	private void checkValidConstructor(String operation, int operand1, int operand2) {
		StupidMathOp op = new StupidMathOp(operation, operand1, operand2);
		Assert.assertEquals(operation, op.getOperation());
		Assert.assertEquals(operand1, op.getOperand1());
		Assert.assertEquals(operand2, op.getOperand2());
	}
	

}
