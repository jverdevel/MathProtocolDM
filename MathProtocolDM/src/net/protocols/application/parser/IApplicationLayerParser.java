package net.protocols.application.parser;

import java.util.List;

import net.protocols.application.IApplicationLayerPacket;
import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;

/**
 * VV: Parser for packages in the application layer level
 * 
 * @author Javier Verde
 * 
 * @param <T> type of packet
 *
 */
public interface IApplicationLayerParser<T extends IApplicationLayerPacket> {

	/**
	 * VV: Parses the packets from a snapshot
	 * 
	 * @param snapshot snapshot.
	 * @return list of packets
	 * @throws ParseSnapshotException
	 */
	public List<T> parsePackets(ITrafficSnapshot snapshot) throws ParseSnapshotException;
}
