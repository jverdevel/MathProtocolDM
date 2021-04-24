package net.processed;

/**
 * Combination of address plus port
 * 
 * @author Javier Verde
 *
 */
public class AddressPort {

	private String ip;
	private String port;

	/**
	 * VV: Creates an address and port combination
	 * 
	 * @param ip   IP address
	 * @param port port
	 */
	public AddressPort(String ip, String port) {
		super();
		this.validateInput(ip, port);

		this.ip = ip;
		this.port = port;
	}

	/**
	 * VV: Validates the input, throws a exception if not valid
	 * 
	 * @param ip   IP address
	 * @param port port
	 */
	private void validateInput(String ip, String port) {
		if (ip == null) {
			throw new IllegalArgumentException("IP must not be null");
		}
		if (port == null) {
			throw new IllegalArgumentException("Port must not be null");
		}
	}

	/**
	 * VV: Gets the IP address
	 * 
	 * @return IP address
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * VV: Gets the port
	 * 
	 * @return port
	 */
	public String getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
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
		AddressPort other = (AddressPort) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		return true;
	}

}
