package net.protocols.application.stupidmath.parser;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.protocols.application.stupidmath.pack.StupidMathPacket;
import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;
import net.snapshot.ITrafficSnapshotTestUtil;

public class StupidMathApplicationLayerParserTest {
	
	private static final String VALID_TEXT_1 = "2+[2#2]-[*#3]";
	private static final String VALID_TEXT_2 = "1=[1#-]";
	private static final String VALID_TEXT_3 = "0";
	
	private static final String INVALID_TEXT_NO_OPCOUNT = "+[2#2]-[*#3]";
	private static final String INVALID_TEXT_WRONG_OPCOUNT = "2=[1#-]";
	private static final String INVALID_TEXT_TOO_BIG = "2+[2#2]-[*#3]+";
	private static final String INVALID_OPERATION = "2p[2#2]-[*#3]";
	private static final String INVALID_NO_OPENING_BRACKET= "2+(2#2]-[*#3]";
	private static final String INVALID_NO_FIRST_NUMBER= "2+[a#2]-[*#3]";
	private static final String INVALID_NO_SECOND_NUMBER= "2+[a#2]-[*#3]";
	private static final String INVALID_MULTIPLE_HASH = "+[2#2]-[*##3]";
	private static final String INVALID_RESULT = "1=[1#2]";
	
	@Test
	public void testWorkingParserRequest() throws ParseSnapshotException {
		List<StupidMathPacket> packets = this.testParser(VALID_TEXT_1);
		Assert.assertEquals(2, packets.size());
		this.assertPacket(packets.get(0), "+", 2, 2);
		this.assertPacket(packets.get(1), "-", StupidMathPacket.USE_PREVIOUS_OPERAND, 3);		
	}
	
	@Test
	public void testWorkingParserResponse() throws ParseSnapshotException {
		List<StupidMathPacket> packets = this.testParser(VALID_TEXT_2);
		Assert.assertEquals(1, packets.size());
		this.assertPacket(packets.get(0), "=", 1, 0);
	}
	
	@Test
	public void testWorkingEmptyResponse() throws ParseSnapshotException{
		List<StupidMathPacket> packets = this.testParser(VALID_TEXT_3);
		Assert.assertEquals(0, packets.size());
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorNoOpCount() throws ParseSnapshotException{
		this.testParser(INVALID_TEXT_NO_OPCOUNT);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorWrongOpCount() throws ParseSnapshotException{
		this.testParser(INVALID_TEXT_WRONG_OPCOUNT);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorTooBig() throws ParseSnapshotException{
		this.testParser(INVALID_TEXT_TOO_BIG);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorTooInvalidOperation() throws ParseSnapshotException{
		this.testParser(INVALID_OPERATION);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorNoOpeningBracket() throws ParseSnapshotException{
		this.testParser(INVALID_NO_OPENING_BRACKET);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorNoFirstNumber() throws ParseSnapshotException{
		this.testParser(INVALID_NO_FIRST_NUMBER);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorNoSecondNumber() throws ParseSnapshotException{
		this.testParser(INVALID_NO_SECOND_NUMBER);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorMultipleHash() throws ParseSnapshotException{
		this.testParser(INVALID_MULTIPLE_HASH);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void testParsingErrorInvalidResult() throws ParseSnapshotException{
		this.testParser(INVALID_RESULT);
	}
	
	private List<StupidMathPacket> testParser(String text) throws ParseSnapshotException {
		ITrafficSnapshot snapshot = ITrafficSnapshotTestUtil.createSnapshot(text);
		StupidMathApplicationLayerParser parser = new StupidMathApplicationLayerParser();
		List<StupidMathPacket> packets = parser.parsePackets(snapshot);
		return packets;
	}
	
	private void assertPacket(StupidMathPacket packet, String operation, int op1, int op2) {
		Assert.assertEquals(operation, packet.getOperation());
		Assert.assertEquals(op1, packet.getOperand1());
		Assert.assertEquals(op2, packet.getOperand2());
	}

}
