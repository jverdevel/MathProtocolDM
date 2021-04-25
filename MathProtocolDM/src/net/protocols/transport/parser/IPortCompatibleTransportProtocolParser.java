package net.protocols.transport.parser;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.transport.pack.PortCompatibleTransportPacket;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser for a port-compatible transport protocol
 * 
 * @author Javier Verde
 *
 */
public interface IPortCompatibleTransportProtocolParser {

	/**
	 * Process packet data for a traffic snapshot
	 * 
	 * @param snapshot snapshot
	 * @return packet packet data
	 * @throws ParseSnapshotException
	 */
	public PortCompatibleTransportPacket processPackage(ITrafficSnapshot snapshot) throws ParseSnapshotException;
}
