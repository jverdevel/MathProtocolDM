package net.protocols.network.parser;

import java.util.List;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.network.pack.IpBasedNetworkPacket;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser for a port-compatible network protocol
 * 
 * @author Javier Verde
 *
 */
public interface IIpBasedNetworkProtocolParser {

	/**
	 * Process first packet data for a traffic snapshot
	 * 
	 * @param snapshot snapshot
	 * @return packet packet data
	 * @throws ParseSnapshotException
	 */
	public IpBasedNetworkPacket processPackage(ITrafficSnapshot snapshot) throws ParseSnapshotException;
	
	/**
	 * Processes all packet data for a traffic snapshot
	 * @param snapshot snapshot
	 * @return packet data
	 * @throws ParseSnapshotException
	 */
	public List<IpBasedNetworkPacket> processPackages(ITrafficSnapshot snapshot) throws ParseSnapshotException;
}
