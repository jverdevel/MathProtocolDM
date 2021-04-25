package integration.net.protocols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;

import integration.net.protocols.ParserAutomaticGeneratorTest.SnapshotAndExpectedComms;
import net.processed.AddressPort;
import net.processed.Comm;
import net.processed.Msg;
import net.processed.application.IProcessedApplicationPacket;
import net.processed.application.stupidMath.StupidMathOp;
import net.protocols.stupidmath.NetworkTrafficStupidMathParserProvider;

/**
 * Base class for automatically generating large snapshots for the parser. Assumes network and transport protocol are txt-ip and stupid-trans. Allows for changing for any application protocol
 * @author VV 
 *
 * @param <T> type of application packages
 */
public abstract class ParserAutomaticGeneratorTest<T extends IProcessedApplicationPacket> {
	private static final int ADDRESS_SIZE = 3;
	private static final int PORT_SIZE = 1;
	
	protected static final String SEPARATOR = "#";

	/**
	 * Performs a test
	 * @param seed seed, for reproductibility
	 * @param numComms number of communications (including a request and response each) generated
	 */
	protected final void doTest(long seed, int numComms) {
		SnapshotAndExpectedComms testCase = this.generateTestCase(seed, numComms);
		NetworkTrafficStupidMathParserProvider provider = new NetworkTrafficStupidMathParserProvider();
		List<Comm<StupidMathOp>> result = provider.getParser().crunch(testCase.getSnapshot().getBytes());
		
		//Since we are going to try potentially very big sizes of data ,doing an assert on the whole list is not the best idea, since Junit is very aggressive for constructing messages, and might end up spending a lot more memory running toString on the list than what is actually spend on the test		
		Assert.assertEquals(testCase.getExpectedComms().size(), result.size());
		for(int i=0; i<testCase.getExpectedComms().size(); i++) {
			Assert.assertEquals(testCase.getExpectedComms().get(i), result.get(i));
		}
	}
	
	/**
	 * Generates a valid random communication
	 * @param random random generator
	 * @return valid random communication
	 */
	private Comm<T> autogenerateComm(Random random){
		String originAddress = this.autoGenerateaddress(random);
		String destinationAddress= this.autoGenerateaddress(random);
		String originPort = this.autogeneratePort(random);
		String destinationPort = this.autogeneratePort(random);
		List<T> opsRequest = this.generateRandomRequestOps(random);
		List<T> opsResponse = this.generateRandomResponseOps(random);
		Msg<T> msgRequest = new Msg<>(new AddressPort(originAddress, originPort), new AddressPort(destinationAddress, destinationPort), opsRequest);
		Msg<T> msgResponse = new Msg<>(new AddressPort(destinationAddress, destinationPort), new AddressPort(originAddress, originPort), opsResponse);
		return new Comm<>(msgRequest, msgResponse);
	}
	
	/**
	 * VV: Generates valid application packet data for the request
	 * @param random randomness generator 
	 * @return application packet data
	 */
	protected abstract List<T> generateRandomRequestOps(Random random);
	
	/**
	 * VV: Generates valid application packet data for the response
	 * @param random randomness generator 
	 * @return application packet data
	 */
	protected abstract List<T> generateRandomResponseOps(Random random);
	
	/**
	 * Generates a test case
	 * @param seed seed for reproductibility
	 * @param numComms number of communications (including a request and response each) 
	 * @return test case
	 */
	private SnapshotAndExpectedComms generateTestCase(long seed, int numComms) {
		//VV: gotta have a reproductible case here
		Random random = new Random(seed);
		List<Comm<T>> comms = IntStream.range(0, numComms).sequential().mapToObj(i -> this.autogenerateComm(random)).collect(Collectors.toList());
		return this.generateTestCase(comms, random);
	}
	
	/**
	 * Generates a test case 
	 * @param comms communications to use. Mixes up responses and requests in a way consistent with protocol
	 * @param random randomness generator
	 * @return snapshot
	 */
	private SnapshotAndExpectedComms generateTestCase(List<Comm<T>> comms, Random random) {
		Queue<Comm<T>> unprocessedComms = new LinkedList<>(comms);
		Queue<Comm<T>> halfProcessedComms = new LinkedList<>();
		List<Comm<T>> processedComms = new ArrayList<>();
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
	
	/**
	 * Adds a request to the snapshot and marks it as half-processed (only response left to send) 
	 * @param snapshot snapshot
	 * @param unprocessedComms queue of communications that haven't been sent
	 * @param halfProcessedComms queue of communications whose request has been sent, but not the response
	 */
	private void processSnapshotRequest(StringBuilder snapshot, Queue<Comm<T>> unprocessedComms, Queue<Comm<T>> halfProcessedComms) {
		Comm<T> unprocessed = unprocessedComms.poll();
		snapshot.append(this.toSnapshot(unprocessed.getRequest()));
		halfProcessedComms.add(unprocessed);
	}
	
	/**
	 * Adds a response to the snapshot and marks it as half-processed (only response left to send) 
	 * @param snapshot snapshot
	 * @param halfProcessedComms whose request has been sent, but not the response
	 * @param processedComms list of fully processed communications
	 */
	private void processSnapshotResponse(StringBuilder snapshot, Queue<Comm<T>> halfProcessedComms, List<Comm<T>> processedComms) {
		Comm<T> halfProcessed = halfProcessedComms.poll();
		snapshot.append(this.toSnapshot(halfProcessed.getResponse()));
		processedComms.add(halfProcessed);
	}
	
	/**
	 * Calculates if the first members in the queue could be misunderstood as a reply to the other
	 * @param unprocessedComms queue of communications that haven't been sent 
	 * @param halfProcessedComms queue of communications whose request has been sent, but not the response
	 * @return true if that could happen
	 */
	private boolean isNextValidResponse(Queue<Comm<T>> unprocessedComms, Queue<Comm<T>> halfProcessedComms) {
		Comm<T> unprocessed = unprocessedComms.peek();
		Comm<T> halfProcessed = halfProcessedComms.peek();
		return unprocessed.getRequest().getOriginAddress().equals(halfProcessed.getResponse().getDestinationAddress()) && unprocessed.getResponse().getOriginAddress().equals(halfProcessed.getRequest().getDestinationAddress());
	}
	

	/**
	 * Adds either a request or response to the snapshot, randomly
	 * @param snapshot snapshot
	 * @param unprocessedComms queue of communications that haven't been sent
	 * @param halfProcessedComms queue of communications whose request has been sent, but not the response
	 * @param processedComms list of fully processed communications
	 * @param random randomness generator
	 */
	private void processSnapshotRandom(StringBuilder snapshot, Queue<Comm<T>> unprocessedComms, Queue<Comm<T>> halfProcessedComms, List<Comm<T>> processedComms, Random random) {
		boolean unprocessed = random.nextBoolean();
		if(unprocessed) {
			this.processSnapshotRequest(snapshot, unprocessedComms, halfProcessedComms);
		}else {
			this.processSnapshotResponse(snapshot, halfProcessedComms, processedComms);
		}
	}
	
	/**
	 * Transform the message into a valid packet snapshot
	 * @param msg message
	 * @return snapshot
	 */
	private String toSnapshot(Msg<T> msg) {
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
	
	/**
	 * Transforms the application data into a valid packet snapshot
	 * @param data application data
	 * @return packet snapshot
	 */
	protected abstract String toSnapshot(List<T> data);
	
	/**
	 * Autogenerates a valid 3-character address for txt-ip protocol
	 * @param random randomness generator
	 * @return address
	 */
	private String autoGenerateaddress(Random random) {
		return this.generateString(random, ADDRESS_SIZE);
	}
	
	/**
	 * Autogenerates a valid 1-character port for stupid-txt protocol
	 * @param random randomness generator
	 * @return port
	 */
	private String autogeneratePort(Random random) {
		return this.generateString(random, PORT_SIZE);
	}
	
	/**
	 * Generates a string generated with [a-z][A-Z] characters at random
	 * @param random randomness generator
	 * @param size size
	 * @return string
	 */
	protected final String generateString(Random random, int size) {
		char[] chars = new char[size];
		for(int i=0; i<size; i++) {
			chars[i] = this.rndChar(random);
		}
		return new String(chars);
	}
	
	/**
	 * Generates a random character in the [a-z][A-Z] range at random
	 * @param random randomness generator
	 * @return character
	 */
	protected final char rndChar(Random random) {
	    int rnd = random.nextInt(52);
	    char base = (rnd < 26) ? 'A' : 'a';
	    return (char) (base + rnd % 26);
	}
	
	/**
	 * VV: Snapshot and expected comms resulting from its parsing
	 * @author Javier Verde
	 *
	 * @param <T> type of processed application packet
	 */
	protected static class SnapshotAndExpectedComms<T extends IProcessedApplicationPacket>{
		private String snapshot;
		private List<Comm<T>> expectedComms;
		
		/**
		 * VV: Creates the snapshot and expected comms
		 * @param snapshot snapshots
		 * @param expectedComms expected comms
		 */
		public SnapshotAndExpectedComms(String snapshot, List<Comm<T>> expectedComms) {
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
		public List<Comm<T>> getExpectedComms() {
			return expectedComms;
		}
			

	}
}
