package net.protocols.application.stupidmath.parser;

import java.util.List;

import net.protocols.application.parser.DefaultApplicationLayerParser;
import net.protocols.application.stupidmath.pack.StupidMathOp;
import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;

public class StupidMathApplicationLayerParser extends DefaultApplicationLayerParser<StupidMathOp>{

	@Override
	public List<StupidMathOp> parsePackets(ITrafficSnapshot snapshot)
			throws ParseSnapshotException {
		// TODO Auto-generated method stub
		return null;
	}
}
