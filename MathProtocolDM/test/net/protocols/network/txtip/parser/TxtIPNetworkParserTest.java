package net.protocols.network.txtip.parser;

import static org.mockito.Mockito.spy;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.network.pack.IpBasedNetworkPacket;
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
		IpBasedNetworkPacket networkPackage = this.testNetworkPackage(VALID_PACKAGE_1, 9, 27);
		Assert.assertEquals("ABC", networkPackage.getOriginIp());
		Assert.assertEquals("XYZ", networkPackage.getDestinationIp());
		Assert.assertEquals(VALID_PACKAGE_1.length(), networkPackage.getTotalLength());
	}

	@Test
	public void checkValidPackage2() throws ParseSnapshotException {
		IpBasedNetworkPacket networkPackage = this.testNetworkPackage(VALID_PACKAGE_2, 9, 20);
		Assert.assertEquals("XYZ", networkPackage.getOriginIp());
		Assert.assertEquals("ABC", networkPackage.getDestinationIp());
		Assert.assertEquals(VALID_PACKAGE_2.length(), networkPackage.getTotalLength());
	}

	@Test
	public void checkMultiProcessing() throws ParseSnapshotException {
		ITrafficSnapshot firstSnapshot = Mockito.mock(ITrafficSnapshot.class);
		Mockito.when(firstSnapshot.getLength()).thenReturn(1);
		ITrafficSnapshot secondSnapshot = Mockito.mock(ITrafficSnapshot.class);
		Mockito.when(secondSnapshot.getLength()).thenReturn(1);
		ITrafficSnapshot lastSnapshot = Mockito.mock(ITrafficSnapshot.class);
		Mockito.when(lastSnapshot.getLength()).thenReturn(0);
		Mockito.when(firstSnapshot.getSnapshotFragment(Mockito.anyInt(), Mockito.anyInt())).thenReturn(secondSnapshot);
		Mockito.when(secondSnapshot.getSnapshotFragment(Mockito.anyInt(), Mockito.anyInt())).thenReturn(lastSnapshot);

		IpBasedNetworkPacket mockedPacket = Mockito.mock(IpBasedNetworkPacket.class);
		TxtIPNetworkParser spyParser = Mockito.mock(TxtIPNetworkParser.class);
		Mockito.when(spyParser.processPackage(Mockito.any())).thenReturn(mockedPacket);
		Mockito.when(spyParser.processPackages(firstSnapshot)).thenCallRealMethod();

		List<IpBasedNetworkPacket> packages = spyParser.processPackages(firstSnapshot);
		Assert.assertEquals(2, packages.size());
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

	private IpBasedNetworkPacket testNetworkPackage(String text, int expectedStart, int expectedEnd)
			throws ParseSnapshotException {
		ITrafficSnapshot snapshot = ITrafficSnapshotTestUtil.createSnapshot(text);
		TxtIPNetworkParser parser = new TxtIPNetworkParser();
		IpBasedNetworkPacket pack = parser.processPackage(snapshot);
		Mockito.verify(snapshot).getSnapshotFragment(expectedStart, expectedEnd);
		return pack;
	}
}
