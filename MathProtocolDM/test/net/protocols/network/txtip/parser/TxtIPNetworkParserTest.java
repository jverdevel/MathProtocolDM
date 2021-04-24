package net.protocols.network.txtip.parser;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.network.pack.IpBasedNetworkPackage;
import net.snapshot.ITrafficSnapshot;
import net.snapshot.ITrafficSnapshotTestUtil;

public class TxtIPNetworkParserTest {

	private static final String VALID_PACKAGE_1 = "ABCXYZ18#ui13#2+[2#2]-[*#3]";
	private static final String VALID_PACKAGE_2 = "XYZABC11#iu7#1=[1#-]";
	
	private static final String INVALID_PACKAGE_TOO_SHORT = "ABCEDF";
	private static final String INVALID_PACKAGE_NOT_A_NUMBER = "ABCXYZXVIII#ui13#2+[2#2]-[*#3]";
	private static final String INVALID_PACKAGE_NO_HASH = "ABCXYZ18ui13#2+[2#2]-[*#3]";
	
	@Test
	public void checkValidPackage1() throws ParseSnapshotException {
		IpBasedNetworkPackage networkPackage = this.testNetworkPackage(VALID_PACKAGE_1, 9, 27);
		Assert.assertEquals("ABC", networkPackage.getOriginIp());
		Assert.assertEquals("XYZ", networkPackage.getDestinationIp());		
	}
	
	@Test
	public void checkValidPackage2() throws ParseSnapshotException {
		IpBasedNetworkPackage networkPackage = this.testNetworkPackage(VALID_PACKAGE_2, 9, 20);
		Assert.assertEquals("XYZ", networkPackage.getOriginIp());
		Assert.assertEquals("ABC", networkPackage.getDestinationIp());		
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void checkInvalidTooShort() throws ParseSnapshotException {
		this.testNetworkPackage(INVALID_PACKAGE_TOO_SHORT, 0, 2);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void checkInvalidNotANumber() throws ParseSnapshotException {
		this.testNetworkPackage(INVALID_PACKAGE_NOT_A_NUMBER, 0, 2);
	}
	
	@Test(expected = ParseSnapshotException.class)
	public void checkInvalidNoHash() throws ParseSnapshotException {
		this.testNetworkPackage(INVALID_PACKAGE_NO_HASH, 0, 2);
	}
	
	private IpBasedNetworkPackage testNetworkPackage(String text, int expectedStart, int expectedEnd) throws ParseSnapshotException {
		ITrafficSnapshot snapshot = ITrafficSnapshotTestUtil.createSnapshot(text);
		TxtIPNetworkParser parser = new TxtIPNetworkParser();
		IpBasedNetworkPackage pack = parser.processPackage(snapshot);
		Mockito.verify(snapshot).getSnapshotFragment(expectedStart, expectedEnd);
		return pack;
	}
}
