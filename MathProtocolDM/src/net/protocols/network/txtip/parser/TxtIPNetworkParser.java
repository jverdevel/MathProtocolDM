package net.protocols.network.txtip.parser;

import java.util.ArrayList;
import java.util.List;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.network.pack.IpBasedNetworkPacket;
import net.protocols.network.parser.IIpBasedNetworkProtocolParser;
import net.protocols.parser.DefaultSnapshotTxtBasedParser;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser for the txt-ip network layer
 * 
 * @author Javier Verde
 *
 */
public class TxtIPNetworkParser extends DefaultSnapshotTxtBasedParser implements IIpBasedNetworkProtocolParser {

	public static final String NAME_NETWORK_LAYER = "network";

	public static final int CHARACTERS_PER_IP = 3;
	public static final int MIN_LENGTH = CHARACTERS_PER_IP * 2 + 1;

	@Override
	public IpBasedNetworkPacket processPackage(ITrafficSnapshot snapshot) throws ParseSnapshotException {
		if (snapshot.getLength() < MIN_LENGTH) {
			throw new ParseSnapshotException(snapshot, NAME_NETWORK_LAYER, 0, "network package is too short");
		}

		String ipFrom = snapshot.getString(0, CHARACTERS_PER_IP);
		String ipTo = snapshot.getString(CHARACTERS_PER_IP, CHARACTERS_PER_IP * 2);

		// Since we don't know how big the snapshot is, rather than transforming it to a
		// String, we'll use stringbuilder and looking for Hash, we are gonna read one
		// character at a time. If any character isn't a digit or hash, we can fail
		// inmediately.
		int pointer = CHARACTERS_PER_IP * 2;
		ProcessedLength dataLength = this.processLength(snapshot, pointer, NAME_NETWORK_LAYER);

		int endRemains = dataLength.getStart() + dataLength.getLength();
		ITrafficSnapshot remains = snapshot.getSnapshotFragment(dataLength.getStart(), endRemains);
		return new IpBasedNetworkPacket(ipFrom, ipTo, remains, endRemains);
	}
	
	@Override
	public List<IpBasedNetworkPacket> processPackages(ITrafficSnapshot snapshot) throws ParseSnapshotException {
		ITrafficSnapshot currentSnapshot = snapshot;
		List<IpBasedNetworkPacket> networkPackages = new ArrayList<>();
		while (currentSnapshot.getLength() != 0) {
			IpBasedNetworkPacket newPackage = this.processPackage(currentSnapshot);
			networkPackages.add(newPackage);
			currentSnapshot = currentSnapshot.getSnapshotFragment(newPackage.getTotalLength(),
					currentSnapshot.getLength());
		}
		return networkPackages;
	}

}
