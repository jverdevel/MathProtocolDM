package net.processed.application.stupidMath.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.processed.application.stupidMath.StupidMathOp;
import net.protocols.application.stupidmath.pack.StupidMathPacket;
import net.protocols.application.stupidmath.parser.StupidMathApplicationLayerParser;
import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;

public class StupidMathOpParserTest {

	private static final int MULTIPLE_PACKETS = 9;

	@Test
	public void checkValidMultiplePacket() throws ParseSnapshotException {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		StupidMathApplicationLayerParser layerParser = Mockito.mock(StupidMathApplicationLayerParser.class);
		List<StupidMathPacket> packets = new ArrayList<>();
		for (int i = 0; i < MULTIPLE_PACKETS; i++) {
			packets.add(Mockito.mock(StupidMathPacket.class));
		}
		Mockito.when(layerParser.parsePackets(Mockito.any())).thenReturn(packets);

		StupidMathOpParser parser = new StupidMathOpParser(layerParser);
		List<StupidMathOp> resPackets = parser.parsePackets(snapshot);
		Assert.assertEquals(MULTIPLE_PACKETS, resPackets.size());
		for (int i = 0; i < MULTIPLE_PACKETS; i++) {
			Assert.assertEquals(packets.get(i), resPackets.get(i).getPacket());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkInvalidInit() {
		new StupidMathOpParser(null);
	}

	@Test(expected = ParseSnapshotException.class)
	public void checkProperExceptionTransport() throws ParseSnapshotException {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		StupidMathApplicationLayerParser layerParser = Mockito.mock(StupidMathApplicationLayerParser.class);
		ParseSnapshotException ex = Mockito.mock(ParseSnapshotException.class);
		Mockito.when(layerParser.parsePackets(Mockito.any())).thenThrow(ex);
		StupidMathOpParser parser = new StupidMathOpParser(layerParser);
		parser.parsePackets(snapshot);
	}

}
