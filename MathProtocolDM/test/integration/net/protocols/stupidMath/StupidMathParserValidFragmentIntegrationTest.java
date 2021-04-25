package integration.net.protocols.stupidMath;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.processed.AddressPort;
import net.processed.Comm;
import net.processed.Msg;
import net.processed.application.stupidMath.StupidMathOp;
import net.protocols.application.stupidmath.pack.StupidMathPacket;
import net.protocols.stupidmath.NetworkTrafficStupidMathParserProvider;
import test.IIntegrationTest;

public class StupidMathParserValidFragmentIntegrationTest implements IIntegrationTest{
	
	private static final String VALID_FRAGMENT = "ABCXYZ18#ui13#2+[2#2]-[*#3]EFGZYX19#Yt14#2*[6#1]-[85#*]ZYXEFG12#tY8#1=[79#-]XYZABC11#iu7#1=[1#-]";
	
	@Test
	public void simpleTestValidFragment() {
		List<Comm<StupidMathOp>> expectedComms = this.getExpectedCommsFromValidFragment();
		
		NetworkTrafficStupidMathParserProvider provider = new NetworkTrafficStupidMathParserProvider();
		List<Comm<StupidMathOp>> result = provider.getParser().crunch(VALID_FRAGMENT.getBytes());
		Assert.assertEquals(expectedComms, result);
	}
	
	private List<Comm<StupidMathOp>> getExpectedCommsFromValidFragment(){
		List<Comm<StupidMathOp>> comms = new ArrayList<>();
		comms.add(this.getLastCommFromValidFragment());
		comms.add(this.getFirstCommFromValidFragment());
		return comms;
	}
	
	private Comm<StupidMathOp> getFirstCommFromValidFragment() {
		Msg<StupidMathOp> msgFirst = this.getFirstMsgFromFirstComm();
		Msg<StupidMathOp> msgLast = this.getLastMsgFromFirstComm();
		return new Comm<>(msgFirst, msgLast);
	}
	
	private Msg<StupidMathOp> getFirstMsgFromFirstComm(){
		AddressPort addressOrigin = new AddressPort("ABC","u");
		AddressPort addressDestination = new AddressPort("XYZ", "i");
		List<StupidMathOp> ops = new ArrayList<>();
		ops.add(new StupidMathOp(new StupidMathPacket("+", 2, 2)));
		ops.add(new StupidMathOp(new StupidMathPacket("-", StupidMathPacket.USE_PREVIOUS_OPERAND, 3)));
		return new Msg<>(addressOrigin, addressDestination, ops);
	}
	
	private Msg<StupidMathOp> getLastMsgFromFirstComm(){
		AddressPort addressOrigin = new AddressPort("XYZ", "i");
		AddressPort addressDestination = new AddressPort("ABC","u");
		List<StupidMathOp> ops = new ArrayList<>();
		ops.add(new StupidMathOp(new StupidMathPacket("=", 1, StupidMathPacket.EXPECTED_OPERAND_2_RESULT)));
		return new Msg<>(addressOrigin, addressDestination, ops);
	}
	
	private Comm<StupidMathOp> getLastCommFromValidFragment() {
		Msg<StupidMathOp> msgFirst = this.getFirstMsgFromLastComm();
		Msg<StupidMathOp> msgLast = this.getLastMsgFromLastComm();
		return new Comm<>(msgFirst, msgLast);
	}
	
	private Msg<StupidMathOp> getFirstMsgFromLastComm(){
		AddressPort addressOrigin = new AddressPort("EFG", "Y");
		AddressPort addressDestination = new AddressPort("ZYX","t");
		List<StupidMathOp> ops = new ArrayList<>();
		ops.add(new StupidMathOp(new StupidMathPacket("*", 6, 1)));
		ops.add(new StupidMathOp(new StupidMathPacket("-", 85, StupidMathPacket.USE_PREVIOUS_OPERAND)));
		return new Msg<>(addressOrigin, addressDestination, ops);
	}
	
	private Msg<StupidMathOp> getLastMsgFromLastComm(){
		AddressPort addressOrigin = new AddressPort("ZYX","t");
		AddressPort addressDestination = new AddressPort("EFG", "Y");
		List<StupidMathOp> ops = new ArrayList<>();
		ops.add(new StupidMathOp(new StupidMathPacket("=", 79, StupidMathPacket.EXPECTED_OPERAND_2_RESULT)));
		return new Msg<>(addressOrigin, addressDestination, ops);

	}
	
	
	

}
