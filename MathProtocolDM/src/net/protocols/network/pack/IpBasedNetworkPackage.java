package net.protocols.network.pack;

import net.snapshot.ITrafficSnapshot;

/**
 * Package data for a IP -compatible network protocol
 * 
 * @author Javier Verde
 *
 **/
public class IpBasedNetworkPackage {
	private String originIp;
	private String destinationIp;
	private ITrafficSnapshot transportLayerData;
	
	/**
	 * Created package data
	 * @param originIp origin IP
	 * @param destinationIp destination IP
	 * @param transportLayerData remaining unprocessed transport layer data
	 */
	public IpBasedNetworkPackage(String originIp, String destinationIp, ITrafficSnapshot transportLayerData) {
		super();
		this.validateEntry(originIp, destinationIp, transportLayerData);

		this.originIp = originIp;
		this.destinationIp = destinationIp;
		this.transportLayerData = transportLayerData;
	}

	/**
	 * Validates entry data
	 * @param originIp origin IP
	 * @param destinationIp destination IP
	 * @param transportLayerData remaining unprocessed transport layer data
		 */
	private void validateEntry(String originIp, String destinationIp, ITrafficSnapshot transportLayerData) {
		if (originIp == null) {
			throw new IllegalArgumentException("Origin IP cannot be null");
		}
		if (destinationIp == null) {
			throw new IllegalArgumentException("Destination IP cannot be null");
		}
		if(transportLayerData==null) {
			throw new IllegalArgumentException("Transport layer data cannot be null");
		}
	}

	public String getOriginIp() {
		return originIp;
	}

	public String getDestinationIp() {
		return destinationIp;
	}

	public ITrafficSnapshot getTransportLayerData() {
		return transportLayerData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinationIp == null) ? 0 : destinationIp.hashCode());
		result = prime * result + ((originIp == null) ? 0 : originIp.hashCode());
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
		IpBasedNetworkPackage other = (IpBasedNetworkPackage) obj;
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
		if (transportLayerData == null) {
			if (other.transportLayerData != null)
				return false;
		} else if (!transportLayerData.equals(other.transportLayerData))
			return false;
		return true;
	}
	
	
}
