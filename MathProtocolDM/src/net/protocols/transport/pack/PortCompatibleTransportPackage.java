package net.protocols.transport.pack;

import net.snapshot.ITrafficSnapshot;

/**
 * Package data for a port-compatible transport protocol
 * 
 * @author Javier Verde
 *
 */
public class PortCompatibleTransportPackage {

	private String originPort;
	private String destinationPort;
	private ITrafficSnapshot applicationLayerData;

	/**
	 * Created package data
	 * @param originPort origin port
	 * @param destinationPort destination port
	 * @param applicationLayerData remaining unprocessed application layer data
	 */
	public PortCompatibleTransportPackage(String originPort, String destinationPort, ITrafficSnapshot applicationLayerData) {
		super();
		this.validateEntry(originPort, destinationPort, applicationLayerData);

		this.originPort = originPort;
		this.destinationPort = destinationPort;
		this.applicationLayerData = applicationLayerData;
	}

	/**
	 * Validates entry data
	 * @param originPort origin port
	 * @param destinationPort destination port
	 * @param applicationLayerData remaining unprocessed application layer data
	 */
	private void validateEntry(String originPort, String destinationPort, ITrafficSnapshot applicationLayerData) {
		if (originPort == null) {
			throw new IllegalArgumentException("Origin port cannot be null");
		}
		if (destinationPort == null) {
			throw new IllegalArgumentException("Destination port cannot be null");
		}
		if(applicationLayerData==null) {
			throw new IllegalArgumentException("Application layer data cannot be null");
		}
	}

	/**
	 * Gets the origin port
	 * @return origin port
	 */
	public String getOriginPort() {
		return originPort;
	}

	/**
	 * Gets the destination port
	 * @return destination port
	 */
	public String getDestinationPort() {
		return destinationPort;
	}

	/**
	 * Gets the remaining application layer data
	 * @return application layer data
	 */
	public ITrafficSnapshot getApplicationLayerData() {
		return applicationLayerData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((applicationLayerData == null) ? 0 : applicationLayerData.hashCode());
		result = prime * result + ((destinationPort == null) ? 0 : destinationPort.hashCode());
		result = prime * result + ((originPort == null) ? 0 : originPort.hashCode());
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
		PortCompatibleTransportPackage other = (PortCompatibleTransportPackage) obj;
		if (applicationLayerData == null) {
			if (other.applicationLayerData != null)
				return false;
		} else if (!applicationLayerData.equals(other.applicationLayerData))
			return false;
		if (destinationPort == null) {
			if (other.destinationPort != null)
				return false;
		} else if (!destinationPort.equals(other.destinationPort))
			return false;
		if (originPort == null) {
			if (other.originPort != null)
				return false;
		} else if (!originPort.equals(other.originPort))
			return false;
		return true;
	}

	
}
