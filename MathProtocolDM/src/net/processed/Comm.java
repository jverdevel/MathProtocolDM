package net.processed;

import net.processed.application.IProcessedApplicationPacket;

/**
 * 
 * Communication and reply between two network systems
 * 
 * @author Javier Verde
 *
 * @param <T> Type of packet
 */
public class Comm<T extends IProcessedApplicationPacket> {

	private Msg<T> request;
	private Msg<T> response;

	/**
	 * Creates a communication
	 * 
	 * @param request  request message
	 * @param response response message
	 */
	public Comm(Msg<T> request, Msg<T> response) {
		super();
		this.validateInput(request, response);

		this.request = request;
		this.response = response;
	}

	private void validateInput(Msg<T> request, Msg<T> response) {
		if (request == null || response == null) {
			throw new IllegalArgumentException("Request and response can't be null");
		}
		if (!request.getOriginAddress().equals(response.getDestinationAddress())) {
			throw new IllegalArgumentException("Mismatched request origin address and response destination address");
		}
		if (!request.getDestinationAddress().equals(response.getOriginAddress())) {
			throw new IllegalArgumentException("Mismatched response origin address and request destination address");
		}
	}

	/**
	 * VV: Gets the request message
	 * 
	 * @return request
	 */
	public Msg<T> getRequest() {
		return request;
	}

	/**
	 * VV: Gets the response
	 * 
	 * @return response
	 */
	public Msg<T> getResponse() {
		return response;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
		result = prime * result + ((response == null) ? 0 : response.hashCode());
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
		Comm other = (Comm) obj;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		if (response == null) {
			if (other.response != null)
				return false;
		} else if (!response.equals(other.response))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Comm [request=" + request + ", response=" + response + "]";
	}

}
