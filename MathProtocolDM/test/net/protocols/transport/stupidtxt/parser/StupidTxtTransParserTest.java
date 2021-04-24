package net.protocols.transport.stupidtxt.parser;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.transport.pack.PortCompatibleTransportPackage;
import net.snapshot.ITrafficSnapshot;
import net.snapshot.ITrafficSnapshotTestUtil;

public class StupidTxtTransParserTest {
	
	private static final String VALID_PACKAGE_1 = "ui13#2+[2#2]-[*#3]";
	private static final String VALID_PACKAGE_2 = "iu7#1=[1#-]";
	
	private static final String INVALID_PACKAGE_TOO_SHORT = "iu";
	private static final String INVALID_PACKAGE_NOT_A_NUMBER = "iuC#1=[1#-]";
	private static final String INVALID_PACKAGE_NO_HASH = "iu2aa";
	
	@Test
	public void checkValidPackage1() throws ParseSnapshotException {
		PortCompatibleTransportPackage transportPackage = this.testTransportPackage(VALID_PACKAGE_1, 5, 18);
		Assert.assertEquals("u", transportPackage.getOriginPort());
		Assert.assertEquals("i", transportPackage.getDestinationPort());		
	}
	
	@Test
	public void checkValidPackage2() throws ParseSnapshotException {
		PortCompatibleTransportPackage transportPackage = this.testTransportPackage(VALID_PACKAGE_2, 4, 11);
		Assert.assertEquals("i", transportPackage.getOriginPort());
		Assert.assertEquals("u", transportPackage.getDestinationPort());		
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void checkInvalidTooShort() throws ParseSnapshotException {
		this.testTransportPackage(INVALID_PACKAGE_TOO_SHORT, 0, 2);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void checkInvalidNotANumber() throws ParseSnapshotException {
		this.testTransportPackage(INVALID_PACKAGE_NOT_A_NUMBER, 0, 2);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void checkInvalidNoHash() throws ParseSnapshotException {
		this.testTransportPackage(INVALID_PACKAGE_NO_HASH, 0, 2);
	}
	
	
	private PortCompatibleTransportPackage testTransportPackage(String text, int expectedStart, int expectedEnd) throws ParseSnapshotException {
		ITrafficSnapshot snapshot = ITrafficSnapshotTestUtil.createSnapshot(text);
		StupidTxtTransParser parser = new StupidTxtTransParser();
		PortCompatibleTransportPackage pack = parser.processPackage(snapshot);
		Mockito.verify(snapshot).getSnapshotFragment(expectedStart, expectedEnd);
		return pack;
	}
}
