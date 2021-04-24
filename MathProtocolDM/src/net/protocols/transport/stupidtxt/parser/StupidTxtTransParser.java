package net.protocols.transport.stupidtxt.parser;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.transport.pack.PortCompatibleTransportPackage;
import net.protocols.transport.parser.IPortCompatibleTransportProtocolParser;
import net.snapshot.ITrafficSnapshot;

public class StupidTxtTransParser implements IPortCompatibleTransportProtocolParser{

	private static final String NAME_TRANSPORT_LAYER = "transport";
	
	private static final int MIN_PACKAGE_SIZE = 4;
	private static final String SEPARATOR = "#";
	
	@Override
	public PortCompatibleTransportPackage processPackage(ITrafficSnapshot snapshot)
			throws ParseSnapshotException {
		
		if(snapshot.getLength()<MIN_PACKAGE_SIZE) {
			throw new ParseSnapshotException(snapshot, NAME_TRANSPORT_LAYER, 0, "Transport package is too short");
		}
		
		
		String portFrom = snapshot.getString(0, 1);
		String portTo = snapshot.getString(1, 2);
		
		//Since we don't know how big the snapshot is, rather than transforming it to a String,  we'll use stringbuilder and looking for Hash, we are gonna read one character at a time. If any character isn't a digit or hash, we can fail inmediately. 
		int pointer = 2;
		boolean done = false;
		StringBuilder sb = new StringBuilder();
		while(!done) {
			if(pointer >=snapshot.getLength()) {
				throw new ParseSnapshotException(snapshot, NAME_TRANSPORT_LAYER, pointer, "Couldn't find valid separator '#'");			
			}
			String currentChar = snapshot.getString(pointer, pointer+1);
			if(currentChar.equals(SEPARATOR)) {
				break;
			}
			if(!Character.isDigit(currentChar.codePointAt(0))) {
				throw new ParseSnapshotException(snapshot, NAME_TRANSPORT_LAYER, pointer, "Invalid length");			
			}
			sb.append(currentChar);			
			pointer++;
		}
		int dataLength = Integer.valueOf(sb.toString());
		
		ITrafficSnapshot remains = snapshot.getSnapshotFragment(pointer+1, pointer+1+dataLength);		
		return new PortCompatibleTransportPackage(portFrom, portTo, remains);		
	}
		
	
}
