package net.processed.application.stupidMath;

import net.processed.application.IProcessedApplicationPacket;
import net.protocols.application.stupidmath.pack.StupidMathPacket;

public class StupidMathOp implements IProcessedApplicationPacket {

	private StupidMathPacket packet;

	public StupidMathOp(StupidMathPacket packet) {
		super();
		this.validateInput(packet);
		
		this.packet = packet;
	}
	
	private void validateInput(StupidMathPacket packet) {
		if(packet == null) {
			throw new IllegalArgumentException("Packet cannot be null");
		}
	}

	public StupidMathPacket getPacket() {
		return packet;
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

}
