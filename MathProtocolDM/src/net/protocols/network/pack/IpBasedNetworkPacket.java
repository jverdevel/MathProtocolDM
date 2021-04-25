package net.protocols.network.pack;

import net.snapshot.ITrafficSnapshot;

/**
 * Packet data for a IP -compatible network protocol
 * 
 * @author Javier Verde
 *
 **/
public class IpBasedNetworkPacket {
	private String originIp;
	private String destinationIp;
	private ITrafficSnapshot transportLayerData;
	private int totalLength;

	/**
	 * Created packet data
	 * 
	 * @param originIp           origin IP
	 * @param destinationIp      destination IP
	 * @param transportLayerData remaining unprocessed transport layer data
	 * @param totalLength        total length of the package
	 */
	public IpBasedNetworkPacket(String originIp, String destinationIp, ITrafficSnapshot transportLayerData,
			int totalLength) {
		super();
		this.validateEntry(originIp, destinationIp, transportLayerData, totalLength);

		this.originIp = originIp;
		this.destinationIp = destinationIp;
		this.transportLayerData = transportLayerData;
		this.totalLength = totalLength;
	}

	/**
	 * Validates entry data
	 * 
	 * @param originIp           origin IP
	 * @param destinationIp      destination IP
	 * @param transportLayerData remaining unprocessed transport layer data
	 */
	private void validateEntry(String originIp, String destinationIp, ITrafficSnapshot transportLayerData,
			int totalLength) {
		if (originIp == null) {
			throw new IllegalArgumentException("Origin IP cannot be null");
		}
		if (destinationIp == null) {
			throw new IllegalArgumentException("Destination IP cannot be null");
		}
		if (transportLayerData == null) {
			throw new IllegalArgumentException("Transport layer data cannot be null");
		}
		if (totalLength < 0) {
			throw new IllegalArgumentException("Length cannot be negative");
		}

	}

	/**
	 * Gets the origin IP
	 * 
	 * @return IP
	 */
	public String getOriginIp() {
		return originIp;
	}

	/**
	 * Gets the destination IP
	 * 
	 * @return destination IP
	 */
	public String getDestinationIp() {
		return destinationIp;
	}

	/**
	 * Gets the remaining data for the transport layer
	 * 
	 * @return data
	 */
	public ITrafficSnapshot getTransportLayerData() {
		return transportLayerData;
	}

	/**
	 * Gets the total length of the the package
	 * 
	 * @return total length
	 */
	public int getTotalLength() {
		return this.totalLength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinationIp == null) ? 0 : destinationIp.hashCode());
		result = prime * result + ((originIp == null) ? 0 : originIp.hashCode());
		result = prime * result + totalLength;
		result = prime * result + ((transportLayerData == null) ? 0 : transportLayerData.hashCode());
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
		IpBasedNetworkPacket other = (IpBasedNetworkPacket) obj;
		if (destinationIp == null) {
			if (other.destinationIp != null)
				return false;
		} else if (!destinationIp.equals(other.destinationIp))
			return false;
		if (originIp == null) {
			if (other.originIp != null)
				return false;
		} else if (!originIp.equals(other.originIp))
			return false;
		if (totalLength != other.totalLength)
			return false;
		if (transportLayerData == null) {
			if (other.transportLayerData != null)
				return false;
		} else if (!transportLayerData.equals(other.transportLayerData))
			return false;
		return true;
	}

}
