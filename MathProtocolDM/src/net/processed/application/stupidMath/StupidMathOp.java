package net.processed.application.stupidMath;

import net.processed.application.IProcessedApplicationPacket;
import net.protocols.application.stupidmath.pack.StupidMathPacket;

/**
 * OP for stupid math. 
 * @Javier Verde
 *
 */
public class StupidMathOp implements IProcessedApplicationPacket {

	//Better keep the processed OP and the packet data separate. Packet data here is useful, and we can use it as-is, but classes themselves should be kept separate, as we might want to add extra processed data to the OP in the future that wouldn't make sense in a packet-level
	private StupidMathPacket packet;

	/**
	 * Creates new operation data 
	 * @param packet backing packet
	 */
	public StupidMathOp(StupidMathPacket packet) {
		super();
		this.validateInput(packet);
		
		this.packet = packet;
	}
	
	/**
	 * Validates that input is correct
	 * @param packet packet data
	 */
	private void validateInput(StupidMathPacket packet) {
		if(packet == null) {
			throw new IllegalArgumentException("Packet cannot be null");
		}
	}

	/**
	 * Gets the backing packet data
	 * @return packet
	 */
	public StupidMathPacket getPacket() {
		return packet;
	}

	/**
	 * Get the operation value
	 * @return operation value
	 */
	public String getOperation() {
		return packet.getOperation();
	}

	/**
	 * Gets the value of the first operand
	 * @return value
	 */
	public int getOperand1() {
		return packet.getOperand1();
	}

	/**
	 * Gets the value of the second operand
	 * @return value
	 */
	public int getOperand2() {
		return packet.getOperand2();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((packet == null) ? 0 : packet.hashCode());
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
		if (packet == null) {
			if (other.packet != null)
				return false;
		} else if (!packet.equals(other.packet))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "StupidMathOp [packet=" + packet + "]";
	}


}
