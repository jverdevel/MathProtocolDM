package net.processed.application.parser;

import java.util.List;

import net.processed.application.IProcessedApplicationPacket;
import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;

/**
 * VV: Parser for processed packages in the application layer level
 * 
 * @author Javier Verde
 * 
 * @param <T> type of packet
 *
 */
public interface IProcessedApplicationParser<T extends IProcessedApplicationPacket> {

	/**
	 * VV: Parses the processed packets from a snapshot
	 * 
	 * @param snapshot snapshot.
	 * @return list of packets
	 * @throws ParseSnapshotException
	 */
	public List<T> parsePackets(ITrafficSnapshot snapshot) throws ParseSnapshotException;

}
