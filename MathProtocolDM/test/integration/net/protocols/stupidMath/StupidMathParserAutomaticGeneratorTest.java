package integration.net.protocols.stupidMath;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import integration.net.protocols.ParserAutomaticGeneratorTest;
import net.processed.application.stupidMath.StupidMathOp;
import net.protocols.application.stupidmath.pack.StupidMathPacket;
import net.protocols.application.stupidmath.parser.StupidMathApplicationLayerParser;
import net.protocols.parser.NetworkTrafficParser;
import net.protocols.stupidmath.NetworkTrafficStupidMathParserProvider;

/**
 * This is a generator of large Comm sets that can be turned into Strings and
 * put back into comms, so we don't have to generate too big strings
 * automatically
 * 
 * @author Javier Verde
 *
 */
public class StupidMathParserAutomaticGeneratorTest extends ParserAutomaticGeneratorTest<StupidMathOp> {

	private static final int MAX_NUMBER_VALUE = 1000;
	private static final int PREVIOUS_OP_DENSITY = 10;

	// calculated with random generator
	private static final long SHORT_SEED = 03034;
	private static final long MEDIUM_SEED = 39403;
	private static final long LONG_SEED = 90940;

	private static final int SHORT_SIZE = 100;
	private static final int MEDIUM_SIZE = 50000;
	private static final int LONG_SIZE = 500_000;

	private static final String[] VALID_NON_RESPONSE_OPERATIONS = new String[] { StupidMathPacket.OPERATION_SUM,
			StupidMathPacket.OPERATION_MINUS, StupidMathPacket.OPERATION_PRODUCT, StupidMathPacket.OPERATION_DIVIDE };

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

	@Override
	protected NetworkTrafficParser<StupidMathOp> initParser() {
		NetworkTrafficStupidMathParserProvider provider = new NetworkTrafficStupidMathParserProvider();
		return provider.getParser();
	}

	@Override
	protected String toSnapshot(List<StupidMathOp> data) {
		StringBuilder sb = new StringBuilder();
		sb.append(data.size());
		data.forEach(d -> sb.append(this.toSnapshot(d)));
		return sb.toString();
	}

	/**
	 * Transforms a valid stupid-math packet back into its snapshot
	 * 
	 * @param data data
	 * @return snapshot
	 */
	private String toSnapshot(StupidMathOp data) {
		StringBuilder sb = new StringBuilder();
		String operation = data.getOperation();
		sb.append(operation);
		sb.append("[");
		sb.append(this.toValidOpNumber(data.getOperand1()));
		sb.append(SEPARATOR);
		if (operation.equals(StupidMathPacket.OPERATION_RESULT)) {
			sb.append(StupidMathApplicationLayerParser.EXPECTED_SECOND_RESULT);
		} else {
			sb.append(this.toValidOpNumber(data.getOperand2()));
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Transforms the number value into its snapshot equivalent
	 * 
	 * @param number number
	 * @return snapshot
	 */
	private String toValidOpNumber(int number) {
		if (number == StupidMathPacket.USE_PREVIOUS_OPERAND) {
			return StupidMathApplicationLayerParser.USE_PREVIOUS_OPERAND;
		}
		return String.valueOf(number);
	}

	@Override
	protected List<StupidMathOp> generateRandomRequestOps(Random random) {
		int numOPs = random.nextInt(9) + 1;
		return IntStream.range(0, numOPs).mapToObj(i -> this.generateRandomRequestOp(random))
				.collect(Collectors.toList());
	}

	/**
	 * Generates a valid operation for a request
	 * 
	 * @param random randomness generator
	 * @return valid operation
	 */
	private StupidMathOp generateRandomRequestOp(Random random) {
		String operation = this.getRandomOperation(random);
		int firstOperand = this.getOperandValueWithPossibleUseBefore(random);
		int secondOperand = this.getOperandValueWithPossibleUseBefore(random);

		StupidMathOp op = new StupidMathOp(new StupidMathPacket(operation, firstOperand, secondOperand));
		return op;
	}

	/**
	 * Obtains a random integer value for operation, allowing for some chance of
	 * "use previous value" (*)
	 * 
	 * @param random randomness generator
	 * @return operand
	 */
	private int getOperandValueWithPossibleUseBefore(Random random) {
		int den = random.nextInt(PREVIOUS_OP_DENSITY);
		if (den == 0) {
			return StupidMathPacket.USE_PREVIOUS_OPERAND;
		} else {
			return this.generateRandomOperand(random);
		}
	}

	@Override
	protected List<StupidMathOp> generateRandomResponseOps(Random random) {
		return Arrays.asList(this.generateRandomResponseOp(random));
	}

	/**
	 * Generates a valid operation for a response
	 * 
	 * @param random randomness generator
	 * @return valid operation
	 */
	private StupidMathOp generateRandomResponseOp(Random random) {
		// Take into account we are _not_ validating that the results are actually
		// correct, which is why we can just put random results here as long as they are
		// protocol compatible, since they are equally valid for testing the parser!
		String operation = StupidMathPacket.OPERATION_RESULT;
		int firstOperand = this.generateRandomOperand(random);
		int secondOperand = StupidMathPacket.EXPECTED_OPERAND_2_RESULT;

		StupidMathOp op = new StupidMathOp(new StupidMathPacket(operation, firstOperand, secondOperand));
		return op;
	}

	/**
	 * Generates a valid random operand value
	 * 
	 * @param random randomness generation
	 * @return value value
	 */
	private int generateRandomOperand(Random random) {
		return random.nextInt(MAX_NUMBER_VALUE);
	}

	/**
	 * Generates a valid non-result operation value
	 * 
	 * @param random randomness generator
	 * @return operation
	 */
	private String getRandomOperation(Random random) {
		return VALID_NON_RESPONSE_OPERATIONS[random.nextInt(VALID_NON_RESPONSE_OPERATIONS.length)];
	}

}
