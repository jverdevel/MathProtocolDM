package net.protocols.transport.stupidtxt.parser;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.parser.DefaultSnapshotTxtBasedParser;
import net.protocols.transport.pack.PortCompatibleTransportPacket;
import net.protocols.transport.parser.IPortCompatibleTransportProtocolParser;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser for the stupid-txt transport layer 
 * @author Javier Verde
 *
 */
public class StupidTxtTransParser extends DefaultSnapshotTxtBasedParser implements IPortCompatibleTransportProtocolParser {

	private static final String NAME_TRANSPORT_LAYER = "transport";
	
	private static final int MIN_PACKAGE_SIZE = 4;
	
	@Override
	public PortCompatibleTransportPacket processPackage(ITrafficSnapshot snapshot)
			throws ParseSnapshotException {
		
		if(snapshot.getLength()<MIN_PACKAGE_SIZE) {
			throw new ParseSnapshotException(snapshot, NAME_TRANSPORT_LAYER, 0, "Transport package is too short");
		}
				
		String portFrom = snapshot.getString(0, 1);
		String portTo = snapshot.getString(1, 2);
		
		//Since we don't know how big the snapshot is, rather than transforming it to a String,  we'll use stringbuilder and looking for Hash, we are gonna read one character at a time. If any character isn't a digit or hash, we can fail inmediately. 
		int pointer = 2;
		ProcessedLength dataLength = this.processLength(snapshot, pointer, NAME_TRANSPORT_LAYER);
		
		ITrafficSnapshot remains = snapshot.getSnapshotFragment(dataLength.getStart(), dataLength.getStart()+dataLength.getLength());		
		return new PortCompatibleTransportPacket(portFrom, portTo, remains);		
	}
		
	
}
