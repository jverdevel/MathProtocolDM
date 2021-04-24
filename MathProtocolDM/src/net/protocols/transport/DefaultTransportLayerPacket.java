package net.protocols.transport;

import java.util.Collections;
import java.util.List;

import net.protocols.application.IApplicationLayerPacket;

/**
 * Default implementation of a transport layer packet
 * 
 * @author Javier Verde
 *
 * @param <T> class of application layer packet contained
 */
public abstract class DefaultTransportLayerPacket<T extends IApplicationLayerPacket> implements ITransportLayerPacket {

	private List<T> listApplicationPackets;

	/**
	 * Creates new packet 
	 * @param listApplicationPackets list of contained application packets
	 */
	public DefaultTransportLayerPacket(List<T> listApplicationPackets) {
		super();
		this.validateInput(listApplicationPackets);

		this.listApplicationPackets = Collections.unmodifiableList(listApplicationPackets);
	}

	/**
	 * Validates the input, throws exception if not valid
	 * 
	 * @param listApplicationPackets list of aplication packets
	 */
	private void validateInput(List<T> listApplicationPackets) {
		if (listApplicationPackets == null) {
			throw new IllegalArgumentException("Application packets cannot be null");
		}
	}

	@Override
	public List<T> getApplicationPackets() {
		return this.listApplicationPackets;
	}

}
