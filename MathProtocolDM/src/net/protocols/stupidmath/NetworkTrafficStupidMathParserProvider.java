package net.protocols.stupidmath;

import net.processed.application.stupidMath.StupidMathOp;
import net.processed.application.stupidMath.parser.StupidMathOpParser;
import net.protocols.application.stupidmath.parser.StupidMathApplicationLayerParser;
import net.protocols.network.txtip.parser.TxtIPNetworkParser;
import net.protocols.parser.NetworkTrafficParser;
import net.protocols.transport.stupidtxt.parser.StupidTxtTransParser;
import net.snapshot.DefaultSnapshotGenerator;

/**
 * Class that provides a working Stupid-Math full parser
 * 
 * @author Javier Verde
 *
 */
public class NetworkTrafficStupidMathParserProvider {

	/**
	 * Compose and return the parser for the Stupid-Math protocol
	 * @return parser
	 */
	public NetworkTrafficParser<StupidMathOp> getParser() {
		return new NetworkTrafficParser<>(new TxtIPNetworkParser(), new StupidTxtTransParser(),
				new StupidMathOpParser(new StupidMathApplicationLayerParser()), new DefaultSnapshotGenerator());
	}
}
