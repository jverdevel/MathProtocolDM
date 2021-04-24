package net.protocols.parser;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultSnapshotParserTest {

	@Test
	public void testCountNoCharacters() {
		this.testCountCharacters("Loren Ipsum", 'P', 0);
	}
	
	@Test
	public void testCountMultipleCharacters() {
		this.testCountCharacters("Loren Ipsum ", ' ', 2);
	}
	
	@Test
	public void testCountSpecialCharacters() {
		this.testCountCharacters("\\folder\\OtherFolder\\User", '\\', 3);
	}
	
	private void testCountCharacters(String text, char character, long expectedTimes) {
		long result = this.getParserToTest().countStringCharacters(text, character);
		Assert.assertEquals(expectedTimes, result);
	}
	
	
	private DefaultSnapshotParser getParserToTest() {
		return Mockito.mock(DefaultSnapshotParser.class, Mockito.CALLS_REAL_METHODS);
	}
}
