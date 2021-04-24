package net.protocols.transport;

import java.util.List;

import net.protocols.application.IApplicationLayerPacket;

/**
 * A packet sent in the transport layer of a network protocol. 
 * @author Javier Verde
 *
 */
public interface ITransportLayerPacket {

	/**
	 * Returns the list of contained application packets
	 * @return list of contained application packets
	 */
	public List<? extends IApplicationLayerPacket> getApplicationPackets();
}
