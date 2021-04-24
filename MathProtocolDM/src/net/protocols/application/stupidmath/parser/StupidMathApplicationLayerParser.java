package net.protocols.application.stupidmath.parser;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.protocols.application.parser.DefaultApplicationLayerParser;
import net.protocols.application.stupidmath.pack.StupidMathPacket;
import net.protocols.exception.ParseSnapshotException;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser for processing stupid-math-rpc packets
 * @author Javier Verde
 *
 */
public class StupidMathApplicationLayerParser extends DefaultApplicationLayerParser<StupidMathPacket> {

	private static final String LAYER_APPLICATION = "application";

	private static final String EXPRESSION_CLOSING_BRACKET = "\\]";
	private static final String EXPRESSION_HASH = "#";
	private static final String USE_PREVIOUS_OPERAND = "*";
	private static final String EXPECTED_SECOND_RESULT = "-";

	private Pattern patternClosingBracket;

	/**
	 * Creates a new parser
	 */
	public StupidMathApplicationLayerParser() {
		patternClosingBracket = Pattern.compile(EXPRESSION_CLOSING_BRACKET);
	}

	@Override
	public List<StupidMathPacket> parsePackets(ITrafficSnapshot snapshot) throws ParseSnapshotException {
		if (snapshot.getLength() == 0) {
			throw new ParseSnapshotException(snapshot, LAYER_APPLICATION, 0);
		}
		String firstElement = snapshot.getString(0, 1);
		if (!Character.isDigit(firstElement.toCharArray()[0])) {
			throw new ParseSnapshotException(snapshot, LAYER_APPLICATION, 0,
					"Expected a digit on stupid-math-rpc opcount");
		}
		// VV: protocol doesn't seem to have a problem with 0 operations. We'll assume
		// it is a valid value, and provide a test.
		int opcount = Integer.valueOf(firstElement);
		List<TaskExtractPacket> tasks = this
				.extractTasksFromSnapshot(snapshot.getSnapshotFragment(1, snapshot.getLength()), opcount)
				.collect(Collectors.toList());
		tasks.forEach(TaskExtractPacket::calculateResult);
		ParseSnapshotException exception = tasks.stream().map(TaskExtractPacket::getParseException)
				.filter(Objects::nonNull).findFirst().orElse(null);
		if (exception != null) {
			throw exception;
		}
		
		//VV: would be worth considering checking if first package doesn't refer to "use previous". Might be valid since it doesn't say it comes in the same packet, but would be worth checking with the full specification
		return tasks.stream().map(TaskExtractPacket::getResult).collect(Collectors.toList());
	}

	/**
	 * Transforms the text into a task for each operation that needs to be extracted
	 * @param snapshot snapshot
	 * @param opcount expected count of operations
	 * @return a task for each operation to be parsed
	 * @throws ParseSnapshotException
	 */
	private Stream<TaskExtractPacket> extractTasksFromSnapshot(ITrafficSnapshot snapshot, int opcount)
			throws ParseSnapshotException {
		// Given that the protocol allows for 9 operations at most, and that no further
		// processing other than extracting package information is going to happen, we
		// can use Strings now. Obviously, if we were dealing with a full snapshot in
		// the Tb range, we would need to store the results as they are calculated, but
		// given the expected size of the strings this particular parser can deal with
		// according to protocol definition, this is not needed here
		if (opcount == 0) {
			return Stream.empty();
		}

		String string = snapshot.getString(0, snapshot.getLength());
		Matcher matcher = patternClosingBracket.matcher(string);
		int[] positions = new int[opcount];
		int previousPosition = 0;
		for (int i = 0; i < opcount; i++) {
			if (!matcher.find()) {
				throw new ParseSnapshotException(snapshot, LAYER_APPLICATION, previousPosition,
						"Not enough closing brackets for the opcount provided");
			}

			int end = matcher.end();
			positions[i] = end;
		}

		if (positions[opcount - 1] != string.length()) {
			throw new ParseSnapshotException(snapshot, LAYER_APPLICATION, previousPosition,
					"Extra data found after last operation");
		}
		return IntStream.range(0, opcount).mapToObj(
				i -> new TaskExtractPacket(snapshot.getSnapshotFragment(i == 0 ? 0 : positions[i - 1], positions[i])));
	}

	/**
	 * Task that turns string fragments into operations
	 * @author Javier Verde
	 *
	 */
	private class TaskExtractPacket {
		private ITrafficSnapshot snapshot;

		private StupidMathPacket result = null;
		private ParseSnapshotException parseException = null;

		/**
		 * VV: Creates the task
		 * @param snapshot string fragment
		 */
		public TaskExtractPacket(ITrafficSnapshot snapshot) {
			super();
			this.snapshot = snapshot;
		}

		/**
		 * VV: Calculates the result. If there's an exception, doesn't throw it but instead stores it
		 */
		public void calculateResult() {
			if (this.result != null || this.parseException != null) {
				throw new IllegalStateException("Task is already calculated!");
			}
			String subString = snapshot.getString(0, snapshot.getLength());
			String operation = subString.substring(0, 1);
			// Make sure it's a valid operation
			if (!StupidMathPacket.VALID_OPERATIONS.contains(operation)) {
				this.parseException = new ParseSnapshotException(snapshot, LAYER_APPLICATION, 0,
						"Invalid operation at stupid-math-rpc package");
				return;
			}
			this.parseException = validateBracketsAndSize(subString);
			if (this.parseException != null) {
				return;
			}

			// VV: make sure all values are ok according to protocol
			String operationString = subString.substring(2, subString.length() - 1);
			String[] hashSplit = operationString.split(EXPRESSION_HASH);
			if (hashSplit.length < 2) {
				this.parseException = new ParseSnapshotException(snapshot, LAYER_APPLICATION, 0,
						"No  # character found at stupid-math-rpc operand");
				return;
			}
			if (hashSplit.length > 2) {
				this.parseException = new ParseSnapshotException(snapshot, LAYER_APPLICATION, 0,
						"Too many # characters found at stupid-math-rpc operand");
				return;
			}
			int operation1;
			try {
				operation1 = this.resolveToNumber(hashSplit[0]);
			} catch (NumberFormatException ex) {
				this.parseException = new ParseSnapshotException(snapshot, LAYER_APPLICATION, 2,
						"First operand is not a number");
				return;
			}
			int operation2;
			if (operation.equals(StupidMathPacket.OPERATION_RESULT)) {
				if (hashSplit[1].equals(EXPECTED_SECOND_RESULT)) {
					operation2 = 0;
				} else {
					this.parseException = new ParseSnapshotException(snapshot, LAYER_APPLICATION, 2,
							"Second operand for result is not " + EXPECTED_SECOND_RESULT);
					return;
				}
			} else {
				try {
					operation2 = this.resolveToNumber(hashSplit[1]);
				} catch (NumberFormatException ex) {
					this.parseException = new ParseSnapshotException(snapshot, LAYER_APPLICATION,
							operationString.indexOf(EXPRESSION_HASH), "Second operand is not a number");
					return;
				}
			}

			this.result = new StupidMathPacket(operation, operation1, operation2);
		}

		/**
		 * VV: Resolves a string to a valid number according to stupid-math-rpc protocol
		 * @param operand operand
		 * @return number
		 */
		private int resolveToNumber(String operand) {
			if (operand.equals(USE_PREVIOUS_OPERAND)) {
				return StupidMathPacket.USE_PREVIOUS_OPERAND;
			}
			return Integer.valueOf(operand);
		}

		/**
		 * Validates that brackets are in text in their correct places
		 * @param subString string to validate
		 * @return exception, null if it's correct
		 */
		private ParseSnapshotException validateBracketsAndSize(String subString) {
			if (subString.length() < 3) {
				return new ParseSnapshotException(snapshot, LAYER_APPLICATION, 0,
						"Unparseable stupid-math-rpc package");
			}
			if (subString.charAt(1) != '[') {
				return new ParseSnapshotException(snapshot, LAYER_APPLICATION, 1,
						"Opening bracket not found at stupid-math-rpc-package");
			}

			if (subString.charAt(subString.length() - 1) != ']') {
				return new ParseSnapshotException(snapshot, LAYER_APPLICATION, 1,
						"Closing bracket not found at stupid-math-rpc-package");
			}
			return null;

		}

		/**
		 * VV: Gets the calculated results
		 * @return result. Null if not calculated or if there was an exception
		 */
		public StupidMathPacket getResult() {
			return result;
		}

		/**
		 * VV: Gets any parse exception that happened during work
		 * @return exception, null if not calculated or if all was okay
		 */
		public ParseSnapshotException getParseException() {
			return parseException;
		}

	}
}
