package net.processed.application.stupidMath.parser;

import java.util.List;
import java.util.stream.Collectors;

import net.processed.application.parser.IProcessedApplicationParser;
import net.processed.application.stupidMath.StupidMathOp;
import net.protocols.application.stupidmath.pack.StupidMathPacket;
import net.protocols.application.stupidmath.parser.StupidMathApplicationLayerParser;
import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;

/**
 * VV: Parwser for processed stupid math operation packages
 * 
 * @author Javier Verde
 *
 */
public class StupidMathOpParser implements IProcessedApplicationParser<StupidMathOp> {

	private StupidMathApplicationLayerParser applicationLayerParser;

	public StupidMathOpParser(StupidMathApplicationLayerParser applicationLayerParser) {
		super();
		this.validateInput(applicationLayerParser);

		this.applicationLayerParser = applicationLayerParser;
	}

	private void validateInput(StupidMathApplicationLayerParser applicationLayerParser) {
		if (applicationLayerParser == null) {
			throw new IllegalArgumentException("Cannot use a null parser");
		}
	}

	@Override
	public List<StupidMathOp> parsePackets(ITrafficSnapshot snapshot) throws ParseSnapshotException {
		List<StupidMathPacket> packets = this.applicationLayerParser.parsePackets(snapshot);

		// it's very unlikely this is ever going to take any important amount of time,
		// so no need to do it in a task format
		return packets.stream().map(this::createOp).collect(Collectors.toList());
	}

	/**
	 * Transforms the packet into a processed packet
	 * 
	 * @param packet packet to process
	 * @return processed package
	 */
	private StupidMathOp createOp(StupidMathPacket packet) {
		return new StupidMathOp(packet);
	}

}
