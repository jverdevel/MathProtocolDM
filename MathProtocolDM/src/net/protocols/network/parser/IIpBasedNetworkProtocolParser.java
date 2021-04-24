package net.protocols.network.parser;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.network.pack.IpBasedNetworkPackage;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser for a port-compatible network protocol
 * @author Javier Verde
 *
 */
public interface IIpBasedNetworkProtocolParser {

	/**
	 * Process packet data for a traffic snapshot
	 * @param snapshot snapshot
	 * @return packet packet data
	 * @throws ParseSnapshotException
	 */
	public IpBasedNetworkPackage processPackage(ITrafficSnapshot snapshot) throws ParseSnapshotException;
}
