package net.protocols.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.processed.Comm;
import net.processed.application.parser.IProcessedApplicationParser;
import net.protocols.exception.ParseSnapshotException;
import net.protocols.network.pack.IpBasedNetworkPacket;
import net.protocols.network.parser.IIpBasedNetworkProtocolParser;
import net.protocols.transport.pack.PortCompatibleTransportPacket;
import net.protocols.transport.parser.IPortCompatibleTransportProtocolParser;
import net.snapshot.ISnapshotGenerator;
import net.snapshot.ITrafficSnapshot;

public class NetworkTrafficParserTest {

	private static final String ORIGIN_IP_1 = "AAA";
	private static final String ORIGIN_IP_2 = "BBB";
	private static final String ORIGIN_IP_3 = "CCC";
	private static final String ORIGIN_IP_4 = "DDD";

	private static final String ORIGIN_PORT_1 = "a";
	private static final String ORIGIN_PORT_2 = "b";
	private static final String ORIGIN_PORT_3 = "c";
	private static final String ORIGIN_PORT_4 = "d";

	@SuppressWarnings("unchecked")
	@Test
	public void testValidPairing() {
		IIpBasedNetworkProtocolParser networkParser = Mockito.mock(IIpBasedNetworkProtocolParser.class);
		IPortCompatibleTransportProtocolParser transportParser = Mockito
				.mock(IPortCompatibleTransportProtocolParser.class);
		IProcessedApplicationParser applicationParser = Mockito.mock(IProcessedApplicationParser.class);
		ISnapshotGenerator generator = Mockito.mock(ISnapshotGenerator.class);
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		Mockito.when(generator.getSnapshot(Mockito.any())).thenReturn(snapshot);
		this.mockPairedPackages(networkParser, transportParser, snapshot);
		NetworkTrafficParser parser = new NetworkTrafficParser<>(networkParser, transportParser, applicationParser,
				generator);
		List<Comm> result = parser.crunch(new byte[0]);
		Assert.assertEquals(2, result.size());
		result.forEach(this::assertValidPair);
	}

	@Test(expected = ParseSnapshotException.class)
	public void testInvalidPairing() {
		IIpBasedNetworkProtocolParser networkParser = Mockito.mock(IIpBasedNetworkProtocolParser.class);
		IPortCompatibleTransportProtocolParser transportParser = Mockito
				.mock(IPortCompatibleTransportProtocolParser.class);
		IProcessedApplicationParser applicationParser = Mockito.mock(IProcessedApplicationParser.class);
		ISnapshotGenerator generator = Mockito.mock(ISnapshotGenerator.class);
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		Mockito.when(generator.getSnapshot(Mockito.any())).thenReturn(snapshot);
		this.mockNonPairedPackages(networkParser, transportParser, snapshot);
		NetworkTrafficParser parser = new NetworkTrafficParser<>(networkParser, transportParser, applicationParser,
				generator);
		parser.crunch(new byte[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkWrongNetworkParser() {
		IPortCompatibleTransportProtocolParser transportParser = Mockito
				.mock(IPortCompatibleTransportProtocolParser.class);
		IProcessedApplicationParser applicationParser = Mockito.mock(IProcessedApplicationParser.class);
		ISnapshotGenerator generator = Mockito.mock(ISnapshotGenerator.class);
		new NetworkTrafficParser<>(null, transportParser, applicationParser, generator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkWrongTransportParser() {
		IIpBasedNetworkProtocolParser networkParser = Mockito.mock(IIpBasedNetworkProtocolParser.class);
		IProcessedApplicationParser applicationParser = Mockito.mock(IProcessedApplicationParser.class);
		ISnapshotGenerator generator = Mockito.mock(ISnapshotGenerator.class);
		new NetworkTrafficParser<>(networkParser, null, applicationParser, generator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkWrongApplicationParser() {
		IIpBasedNetworkProtocolParser networkParser = Mockito.mock(IIpBasedNetworkProtocolParser.class);
		IPortCompatibleTransportProtocolParser transportParser = Mockito
				.mock(IPortCompatibleTransportProtocolParser.class);
		ISnapshotGenerator generator = Mockito.mock(ISnapshotGenerator.class);
		new NetworkTrafficParser<>(networkParser, transportParser, null, generator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkWrongSnapshotGenerator() {
		IIpBasedNetworkProtocolParser networkParser = Mockito.mock(IIpBasedNetworkProtocolParser.class);
		IPortCompatibleTransportProtocolParser transportParser = Mockito
				.mock(IPortCompatibleTransportProtocolParser.class);
		IProcessedApplicationParser applicationParser = Mockito.mock(IProcessedApplicationParser.class);
		new NetworkTrafficParser<>(networkParser, transportParser, applicationParser, null);
	}

	private void assertValidPair(Comm comm) {
		Assert.assertEquals(comm.getRequest().getOriginAddress(), comm.getResponse().getDestinationAddress());
	}

	private void mockPairedPackages(IIpBasedNetworkProtocolParser parser,
			IPortCompatibleTransportProtocolParser transportParser, ITrafficSnapshot snapshot) {
		List<IpBasedNetworkPacket> packets = new ArrayList<>();
		packets.add(this.mockPacket(transportParser, ORIGIN_IP_1, ORIGIN_IP_2, ORIGIN_PORT_1, ORIGIN_PORT_2));
		packets.add(this.mockPacket(transportParser, ORIGIN_IP_3, ORIGIN_IP_4, ORIGIN_PORT_3, ORIGIN_PORT_4));
		packets.add(this.mockPacket(transportParser, ORIGIN_IP_4, ORIGIN_IP_3, ORIGIN_PORT_4, ORIGIN_PORT_3));
		packets.add(this.mockPacket(transportParser, ORIGIN_IP_2, ORIGIN_IP_1, ORIGIN_PORT_2, ORIGIN_PORT_1));
		Mockito.when(parser.processPackages(Mockito.any())).thenReturn(packets);
	}

	private void mockNonPairedPackages(IIpBasedNetworkProtocolParser parser,
			IPortCompatibleTransportProtocolParser transportParser, ITrafficSnapshot snapshot) {
		List<IpBasedNetworkPacket> packets = new ArrayList<>();
		packets.add(this.mockPacket(transportParser, ORIGIN_IP_1, ORIGIN_IP_2, ORIGIN_PORT_1, ORIGIN_PORT_2));
		packets.add(this.mockPacket(transportParser, ORIGIN_IP_3, ORIGIN_IP_4, ORIGIN_PORT_3, ORIGIN_PORT_4));
		Mockito.when(parser.processPackages(Mockito.any())).thenReturn(packets);
	}

	private IpBasedNetworkPacket mockPacket(IPortCompatibleTransportProtocolParser mockTransport, String ipOrigin,
			String ipDestination, String portOrigin, String portDestination) {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		IpBasedNetworkPacket packet = new IpBasedNetworkPacket(ipOrigin, ipDestination, snapshot, 6);
		ITrafficSnapshot mockSnapshotTransport = Mockito.mock(ITrafficSnapshot.class);
		PortCompatibleTransportPacket packetTransport = new PortCompatibleTransportPacket(portOrigin, portDestination,
				mockSnapshotTransport);
		Mockito.when(mockTransport.processPackage(snapshot)).thenReturn(packetTransport);
		return packet;
	}

}
