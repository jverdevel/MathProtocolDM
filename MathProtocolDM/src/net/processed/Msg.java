package net.processed;

import java.util.List;

import net.processed.application.IProcessedApplicationPacket;

/**
 * Processed message between two network elements, including address and port
 * 
 * @author Javier Verde
 *
 * @param <T> type of packet
 */
public class Msg<T extends IProcessedApplicationPacket> {
	private AddressPort originAddress;
	private AddressPort destinationAddress;
	private List<T> applicationProcessedPackets;

	/**
	 * Creates message
	 * 
	 * @param originAddress               address message was sent from
	 * @param destinationAddress          address message was sent to
	 * @param applicationProcessedPackets list of contained packets
	 */
	public Msg(AddressPort originAddress, AddressPort destinationAddress, List<T> applicationProcessedPackets) {
		super();
		this.validateInput(originAddress, destinationAddress, applicationProcessedPackets);

		this.originAddress = originAddress;
		this.destinationAddress = destinationAddress;
		this.applicationProcessedPackets = applicationProcessedPackets;
	}

	/**
	 * Validates the input, throws exception if not correct
	 * 
	 * @param originAddress               address message was sent from
	 * @param destinationAddress          address message was sent to
	 * @param applicationProcessedPackets list of containted packets
	 */
	private void validateInput(AddressPort originAddress, AddressPort destinationAddress,
			List<T> applicationProcessedPackets) {

		if (originAddress == null || destinationAddress == null) {
			throw new IllegalArgumentException("Addresses cannot be null");
		}
		if (applicationProcessedPackets == null) {
			throw new IllegalArgumentException("Transport packete list cannot be null");
		}
	}

	/**
	 * Gets address the message was sent from
	 * 
	 * @return address
	 */
	public AddressPort getOriginAddress() {
		return originAddress;
	}

	/**
	 * Gets address the message was sent to
	 * 
	 * @return address
	 */
	public AddressPort getDestinationAddress() {
		return destinationAddress;
	}

	/**
	 * Gets the list of processed transport packets
	 * 
	 * @return list of packets
	 */
	public List<T> getApplicationProcessedPackets() {
		return applicationProcessedPackets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinationAddress == null) ? 0 : destinationAddress.hashCode());
		result = prime * result + ((originAddress == null) ? 0 : originAddress.hashCode());
		result = prime * result + ((applicationProcessedPackets == null) ? 0 : applicationProcessedPackets.hashCode());
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
		Msg other = (Msg) obj;
		if (destinationAddress == null) {
			if (other.destinationAddress != null)
				return false;
		} else if (!destinationAddress.equals(other.destinationAddress))
			return false;
		if (originAddress == null) {
			if (other.originAddress != null)
				return false;
		} else if (!originAddress.equals(other.originAddress))
			return false;
		if (applicationProcessedPackets == null) {
			if (other.applicationProcessedPackets != null)
				return false;
		} else if (!applicationProcessedPackets.equals(other.applicationProcessedPackets))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Msg [originAddress=" + originAddress + ", destinationAddress=" + destinationAddress
				+ ", transportProcessedPackets=" + applicationProcessedPackets + "]";
	}

}
