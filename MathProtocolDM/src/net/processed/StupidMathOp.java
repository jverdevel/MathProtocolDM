package net.processed;

import java.util.Set;

import net.protocols.application.DefaultApplicationLayerPacket;

/**
 * Processed operation used by the StupidMath application layer
 * @author VV
 *
 */
public class StupidMathOp implements IProcessedTransportPacket{

	public static final int USE_PREVIOUS_OPERAND = -1;
	public static final int EXPECTED_OPERAND_2_RESULT = 0;
	
	public static final String OPERATION_SUM ="+";
	public static final String OPERATION_MINUS = "-";
	public static final String OPERATION_PRODUCT = "*";
	public static final String OPERATION_DIVIDE = "/";
	public static final String OPERATION_RESULT = "=";
	public static final Set<String> VALID_OPERATIONS = Set.of(OPERATION_SUM, OPERATION_MINUS, OPERATION_PRODUCT, OPERATION_DIVIDE, OPERATION_RESULT);
	
	private String operation;
	private int operand1;
	private int operand2; //on result operations this is always 0
	
	/**
	 * Creates a packet for StupidMath application layer
	 * @param operation. Expected values are one of [+,-,*,/,]
	 * @param operand1 first operand. Use USE_PREVIOUS_OPERAND if packet tells to call previous operand
	 * @param operand2 second operand. Use USE_PREVIOUS_OPERAND if packet tells to call previous operand, If the operation is result, this is always 0
	 */
	public StupidMathOp(String operation, int operand1, int operand2) {
		super();
	
		this.validateInput(operation, operand1, operand2);
		
		this.operation = operation;
		this.operand1 = operand1;
		this.operand2 = operand2;	
	}
	
	/**
	 * Validates that the input values are compatible with the defined protocol
	 * @param operation operation 
	 * @param operand1 first operand 
	 * @param operand2 second operand
	 */
	private void validateInput(String operation, int operand1, int operand2) {
		this.validateOperation(operation);
		this.validateOperand(operand1);
		this.validateOperand(operand2);
		this.validateSecondOperandResult(operation, operand2);		
	}
	
	private void validateOperation(String operation) {
		if(operation == null || !VALID_OPERATIONS.contains(operation)) {
			throw new IllegalArgumentException("Operation isn't valid");
		}
	}
	
	/**
	 * Validates that the operand has a expected value
	 * @param operand operand
	 */
	private void validateOperand(int operand) {
		if(operand <0 && operand !=USE_PREVIOUS_OPERAND) {
			throw new IllegalArgumentException("Illegal negative value for operand");
		}
	}
	
	private void validateSecondOperandResult(String operation, int operand2) {
		if(operation.equals(OPERATION_RESULT) && operand2!=EXPECTED_OPERAND_2_RESULT){
			throw new IllegalArgumentException("Second operand should be " + EXPECTED_OPERAND_2_RESULT + " for a result operation");
		}
	}
	
	/**
	 * Get the operation value
	 * @return operation value
	 */
	public String getOperation() {
		return operation;
	}
	
	/**
	 * Gets the value of the first operand
	 * @return value
	 */
	public int getOperand1() {
		return operand1;
	}
	
	/**
	 * Gets the value of the second operand
	 * @return value
	 */
	public int getOperand2() {
		return operand2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + operand1;
		result = prime * result + operand2;
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StupidMathOp other = (StupidMathOp) obj;
		if (operand1 != other.operand1)
			return false;
		if (operand2 != other.operand2)
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		return true;
	}
	
	
	
	
}
