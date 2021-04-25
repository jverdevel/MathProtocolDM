package integration.net.protocols.stupidMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import net.processed.AddressPort;
import net.processed.Comm;
import net.processed.Msg;
import net.processed.application.stupidMath.StupidMathOp;
import net.protocols.application.stupidmath.pack.StupidMathPacket;
import net.protocols.application.stupidmath.parser.StupidMathApplicationLayerParser;
import net.protocols.stupidmath.NetworkTrafficStupidMathParserProvider;

//TODO: make this generic while we are on it
/**
 * This is a generator of large Comm sets that can be turned into Strings and put back into comms, for not having to generate too big strings automatically
 * @author Javier Verde
 *
 */
public class StupidMathParserAutomaticGeneratorTest {
	
	private static final int ADDRESS_SIZE = 3;
	private static final int PORT_SIZE = 1;
	
	private static final int MAX_NUMBER_VALUE = 1000;
	private static final int PREVIOUS_OP_DENSITY = 10;
	
	//calculated with random generator
	private static final long SHORT_SEED = 03034;
	private static final long MEDIUM_SEED = 39403;
	private static final long LONG_SEED = 90940;
	
	private static final int SHORT_SIZE = 100;
	private static final int MEDIUM_SIZE = 50000;
	private static final int LONG_SIZE = 1_000_000;
	
	private String[] VALID_NON_RESPONSE_OPERATIONS = new String[] {StupidMathPacket.OPERATION_SUM, StupidMathPacket.OPERATION_MINUS, StupidMathPacket.OPERATION_PRODUCT, StupidMathPacket.OPERATION_DIVIDE};
	private String SEPARATOR = "#";
	
	@Test
	public void integrationTestShortTest() {
		this.doTest(SHORT_SEED, SHORT_SIZE);	
	}
	
	@Test
	public void integrationTestMediumTest() {
		this.doTest(MEDIUM_SEED, MEDIUM_SIZE);	
	}
	
	@Test
	public void integrationTestLongTest() {
		this.doTest(LONG_SEED, LONG_SIZE);	
	}
	
	
	private void doTest(long seed, int numComms) {
		SnapshotAndExpectedComms testCase = this.generateTestCase(seed, numComms);
		NetworkTrafficStupidMathParserProvider provider = new NetworkTrafficStupidMathParserProvider();
		List<Comm<StupidMathOp>> result = provider.getParser().crunch(testCase.getSnapshot().getBytes());
		
		//Since we are going to try potentially very big sizes of data ,doing an assert on the whole list is not the best idea, since Junit is very aggressive for constructing messages, and might end up spending a lot more memory running toString on the list than what is actually spend on the test		
		Assert.assertEquals(testCase.getExpectedComms().size(), result.size());
		for(int i=0; i<testCase.getExpectedComms().size(); i++) {
			Assert.assertEquals(testCase.getExpectedComms().get(i), result.get(i));
		}
	}
	
	private SnapshotAndExpectedComms generateTestCase(long seed, int numComms) {
		//VV: gotta have a reproductible case here
		Random random = new Random(seed);
		List<Comm<StupidMathOp>> comms = IntStream.range(0, numComms).sequential().mapToObj(i -> this.autogenerateComm(random)).collect(Collectors.toList());
		return this.generateTestCase(comms, random);
	}
	
	private SnapshotAndExpectedComms generateTestCase(List<Comm<StupidMathOp>> comms, Random random) {
		Queue<Comm<StupidMathOp>> unprocessedComms = new LinkedList<>(comms);
		Queue<Comm<StupidMathOp>> halfProcessedComms = new LinkedList<>();
		List<Comm<StupidMathOp>> processedComms = new ArrayList<>();
		StringBuilder snapshot = new StringBuilder();
		while(!unprocessedComms.isEmpty() || !halfProcessedComms.isEmpty()) {
			if(halfProcessedComms.isEmpty()) {
				//no half processed comms at all, so we need to add a new one
				this.processSnapshotRequest(snapshot, unprocessedComms, halfProcessedComms);
			}else if(unprocessedComms.isEmpty()) {
				//just need to full up the unprocessed queue here. 
				this.processSnapshotResponse(snapshot, halfProcessedComms, processedComms);
			}else if(this.isNextValidResponse(unprocessedComms, halfProcessedComms)){
				//Very much an edge case, but if the half processed top happens to be a valid response to the unprocessed top, we need to do the half processed before to avoid two consecutive equal requests without a response from the same client to the same server, which looks protocol-incompatible
				this.processSnapshotResponse(snapshot, halfProcessedComms, processedComms);
			}else {
				//solve at random
				this.processSnapshotRandom(snapshot, unprocessedComms, halfProcessedComms, processedComms, random);
			}			
		}
		
		return new SnapshotAndExpectedComms(snapshot.toString(), processedComms);		
	}
	
	private void processSnapshotRequest(StringBuilder snapshot, Queue<Comm<StupidMathOp>> unprocessedComms, Queue<Comm<StupidMathOp>> halfProcessedComms) {
		Comm<StupidMathOp> unprocessed = unprocessedComms.poll();
		snapshot.append(this.toSnapshot(unprocessed.getRequest()));
		halfProcessedComms.add(unprocessed);
	}
	
	private void processSnapshotResponse(StringBuilder snapshot, Queue<Comm<StupidMathOp>> halfProcessedComms, List<Comm<StupidMathOp>> processedComms) {
		Comm<StupidMathOp> halfProcessed = halfProcessedComms.poll();
		snapshot.append(this.toSnapshot(halfProcessed.getResponse()));
		processedComms.add(halfProcessed);
	}
	
	private boolean isNextValidResponse(Queue<Comm<StupidMathOp>> unprocessedComms, Queue<Comm<StupidMathOp>> halfProcessedComms) {
		Comm<StupidMathOp> unprocessed = unprocessedComms.peek();
		Comm<StupidMathOp> halfProcessed = halfProcessedComms.peek();
		return unprocessed.getRequest().getOriginAddress().equals(halfProcessed.getResponse().getDestinationAddress()) && unprocessed.getResponse().getOriginAddress().equals(halfProcessed.getRequest().getDestinationAddress());
	}
	
	private void processSnapshotRandom(StringBuilder snapshot, Queue<Comm<StupidMathOp>> unprocessedComms, Queue<Comm<StupidMathOp>> halfProcessedComms, List<Comm<StupidMathOp>> processedComms, Random random) {
		boolean unprocessed = random.nextBoolean();
		if(unprocessed) {
			this.processSnapshotRequest(snapshot, unprocessedComms, halfProcessedComms);
		}else {
			this.processSnapshotResponse(snapshot, halfProcessedComms, processedComms);
		}
	}
	
	
	private String toSnapshot(Msg<StupidMathOp> msg) {
		String snapshotApplication = this.toSnapshot(msg.getApplicationProcessedPackets());
		StringBuilder sb = new StringBuilder(snapshotApplication);
		sb.insert(0, SEPARATOR);
		sb.insert(0, snapshotApplication.length());
		sb.insert(0, msg.getDestinationAddress().getPort());
		sb.insert(0, msg.getOriginAddress().getPort());
		int sizeTransport = sb.length();
		sb.insert(0, SEPARATOR);
		sb.insert(0, sizeTransport);
		sb.insert(0, msg.getDestinationAddress().getIp());
		sb.insert(0, msg.getOriginAddress().getIp());
		return sb.toString();
	}
	
	private String toSnapshot(List<StupidMathOp> data) {
		StringBuilder sb = new StringBuilder();
		sb.append(data.size());
		data.forEach(d -> sb.append(this.toSnapshot(d)));
		return sb.toString();
	}
	
	private String toSnapshot(StupidMathOp data) {
		StringBuilder sb = new StringBuilder();
		String operation = data.getOperation();
		sb.append(operation);
		sb.append("[");
		sb.append(this.toValidOpNumber(data.getOperand1()));
		sb.append(SEPARATOR);
		if(operation.equals(StupidMathPacket.OPERATION_RESULT)){
			sb.append(StupidMathApplicationLayerParser.EXPECTED_SECOND_RESULT);
		}else {
			sb.append(this.toValidOpNumber(data.getOperand2()));
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String toValidOpNumber(int number) {
		if(number == StupidMathPacket.USE_PREVIOUS_OPERAND) {
			return StupidMathApplicationLayerParser.USE_PREVIOUS_OPERAND;
		}
		return String.valueOf(number);
	}
	
	private Comm<StupidMathOp> autogenerateComm(Random random){
		String originAddress = this.autoGenerateaddress(random);
		String destinationAddress= this.autoGenerateaddress(random);
		String originPort = this.autogeneratePort(random);
		String destinationPort = this.autogeneratePort(random);
		List<StupidMathOp> opsRequest = this.generateRandomRequestOps(random);
		List<StupidMathOp> opsResponse = Arrays.asList(this.generateRandomResponseOp(random));
		Msg<StupidMathOp> msgRequest = new Msg<>(new AddressPort(originAddress, originPort), new AddressPort(destinationAddress, destinationPort), opsRequest);
		Msg<StupidMathOp> msgResponse = new Msg<>(new AddressPort(destinationAddress, destinationPort), new AddressPort(originAddress, originPort), opsResponse);
		return new Comm<>(msgRequest, msgResponse);
	}
	
	private List<StupidMathOp> generateRandomRequestOps(Random random){
		int numOPs = random.nextInt(9)+1;
		return IntStream.range(0, numOPs).mapToObj(i -> this.generateRandomRequestOp(random)).collect(Collectors.toList());
	}
	
	private StupidMathOp generateRandomRequestOp(Random random) {
		String operation = this.getRandomOperation(random);
		int firstOperand = this.getOperandValueWithPossibleUseBefore(random);
		int secondOperand = this.getOperandValueWithPossibleUseBefore(random);
		
		StupidMathOp op = new StupidMathOp(new StupidMathPacket(operation, firstOperand, secondOperand));
		return op;
	}
	
	private int getOperandValueWithPossibleUseBefore(Random random) {
		int den = random.nextInt(PREVIOUS_OP_DENSITY);
		if(den ==0) {
			return StupidMathPacket.USE_PREVIOUS_OPERAND;
		}else {
			return this.generateRandomOperand(random);
		}
	}
	
	private StupidMathOp generateRandomResponseOp(Random random) {
		//Take into account we are _not_ validating that the results are actually correct, which is why we can just put random results here as long as they are protocol compatible, since they are equally valid for testing the parser!
		String operation = StupidMathPacket.OPERATION_RESULT;
		int firstOperand = this.generateRandomOperand(random);
		int secondOperand = StupidMathPacket.EXPECTED_OPERAND_2_RESULT;
		
		StupidMathOp op = new StupidMathOp(new StupidMathPacket(operation, firstOperand, secondOperand));
		return op;
	}
	
	private int generateRandomOperand(Random random) {
		return random.nextInt(MAX_NUMBER_VALUE);
	}
	
	private String getRandomOperation(Random random) {
		return VALID_NON_RESPONSE_OPERATIONS[random.nextInt(VALID_NON_RESPONSE_OPERATIONS.length)];
	}
	
	private String autoGenerateaddress(Random random) {
		return this.generateString(random, ADDRESS_SIZE);
	}
	
	private String autogeneratePort(Random random) {
		return this.generateString(random, PORT_SIZE);
	}
	
	private String generateString(Random random, int size) {
		char[] chars = new char[size];
		for(int i=0; i<size; i++) {
			chars[i] = this.rndChar(random);
		}
		return new String(chars);
	}
	
	private char rndChar(Random random) {
	    int rnd = random.nextInt(52);
	    char base = (rnd < 26) ? 'A' : 'a';
	    return (char) (base + rnd % 26);

	}
	
	/**
	 * VV: Snapshot and expected comms resulting from its parsing
	 * @author Javier Verde
	 *
	 */
	private static class SnapshotAndExpectedComms{
		private String snapshot;
		private List<Comm<StupidMathOp>> expectedComms;
		
		/**
		 * VV: Creates the snapshot and expected comms
		 * @param snapshot snapshots
		 * @param expectedComms expected comms
		 */
		public SnapshotAndExpectedComms(String snapshot, List<Comm<StupidMathOp>> expectedComms) {
			super();
			this.snapshot = snapshot;
			this.expectedComms = expectedComms;
		}
		
		/**
		 * VV: Gets the snapshot to parse
		 * @return snapshot to parse
		 */
		public String getSnapshot() {
			return snapshot;
		}
		
		/**
		 * VV: Gets the expected comms
		 * @return expected comms in order
		 */
		public List<Comm<StupidMathOp>> getExpectedComms() {
			return expectedComms;
		}
			

	}
	
}
