package net.protocols.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.processed.AddressPort;
import net.processed.Comm;
import net.processed.Msg;
import net.processed.application.IProcessedApplicationPacket;
import net.processed.application.parser.IProcessedApplicationParser;
import net.protocols.exception.ParseSnapshotException;
import net.protocols.network.pack.IpBasedNetworkPacket;
import net.protocols.network.parser.IIpBasedNetworkProtocolParser;
import net.protocols.transport.pack.PortCompatibleTransportPacket;
import net.protocols.transport.parser.IPortCompatibleTransportProtocolParser;
import net.snapshot.ByteArrayBasedTrafficSnapshot;
import net.snapshot.ITrafficSnapshot;

/**
 * Parser for network traffic
 * 
 * @author Javier Verde
 *
 * @param <T> type of application data expected
 */
public class NetworkTrafficParser<T extends IProcessedApplicationPacket> {

	private IIpBasedNetworkProtocolParser networkParser;
	private IPortCompatibleTransportProtocolParser transportParser;
	private IProcessedApplicationParser<T> applicationParser;

	private static final String NETWORK_LAYER = "network";

	/**
	 * Creates a new parser
	 * 
	 * @param networkParser     network-layer packet parser
	 * @param transportParser   transport-layer packet parser
	 * @param applicationParser application-layer processed parser
	 */
	public NetworkTrafficParser(IIpBasedNetworkProtocolParser networkParser,
			IPortCompatibleTransportProtocolParser transportParser, IProcessedApplicationParser<T> applicationParser) {
		super();
		this.validateInput(networkParser, transportParser, applicationParser);

		this.networkParser = networkParser;
		this.transportParser = transportParser;
		this.applicationParser = applicationParser;
	}

	/**
	 * Validates that the input is correct, throws an Exception if it's not
	 * 
	 * @param networkParser     network-layer packet parser
	 * @param transportParser   transport-layer packet parser
	 * @param applicationParser application-layer processed parser
	 */
	private void validateInput(IIpBasedNetworkProtocolParser networkParser,
			IPortCompatibleTransportProtocolParser transportParser, IProcessedApplicationParser<T> applicationParser) {
		if (networkParser == null) {
			throw new IllegalArgumentException("Network parser cannot be null");
		}
		if (transportParser == null) {
			throw new IllegalArgumentException("Transport parser cannot be null");
		}
		if (applicationParser == null) {
			throw new IllegalArgumentException("Application parser cannot be null");
		}
	}

	/**
	 * Returns the list of communcations contained in the snapshot
	 * 
	 * @param raw byte array. Byte is assumed to be gotten from the getBytes()
	 *            string method
	 * @return communications. Communications will be ordered by COMPLETION order.
	 * @throws ParseSnapshotException
	 */
	public List<Comm<T>> crunch(byte[] raw) throws ParseSnapshotException {
		ITrafficSnapshot snapshot = this.getSnapshot(raw);

		// VV: first we find out the packet information for network
		List<IpBasedNetworkPacket> networkPackets = this.parseNetworkPackets(snapshot);
		// VV: now, for each network packet, we find out the associate transport packet
		// information.
		Stream<AddressDataWithApplicationSnapshot> applicationSnapshots = this
				.getAddressDataFromNetwork(networkPackets);
		// VV: we pair them in valid pairs.
		Stream<PairedCommuncationsData> validPairs = this.pairCommunicationsData(snapshot, applicationSnapshots);

		return this.getCommsFromPairedData(validPairs);
	}

	/**
	 * Gets the snapshot to be used from the raw traffic
	 * 
	 * @param raw raw traffic
	 * @return snapshot
	 */
	protected ITrafficSnapshot getSnapshot(byte[] raw) {
		// TODO: add failsafe in case ISO is not working
		return new ByteArrayBasedTrafficSnapshot(raw);
	}

	/**
	 * Extracts network packets from the initial snapshot
	 * 
	 * @param snapshot snapshot
	 * @return network packets
	 * @throws ParseSnapshotException
	 */
	private List<IpBasedNetworkPacket> parseNetworkPackets(ITrafficSnapshot snapshot) throws ParseSnapshotException {
		ITrafficSnapshot currentSnapshot = snapshot;
		List<IpBasedNetworkPacket> networkPackages = new ArrayList<>();
		while (currentSnapshot.getLength() != 0) {
			IpBasedNetworkPacket newPackage = networkParser.processPackage(currentSnapshot);
			networkPackages.add(newPackage);
			currentSnapshot = currentSnapshot.getSnapshotFragment(newPackage.getTotalLength(),
					currentSnapshot.getLength());
		}
		return networkPackages;
	}

	/**
	 * Extracts transport-layer data from packet and readies full IP and port based
	 * address data from network packets
	 * 
	 * @param packets network packets
	 * @return stream of addresses with remaining application data
	 */
	private Stream<AddressDataWithApplicationSnapshot> getAddressDataFromNetwork(
			Collection<IpBasedNetworkPacket> packets) {
		return packets.parallelStream().map(this::getAddressDataFromNetwork);
	}

	/**
	 * Extracts transport-layer data from packet and readies full IP and port based
	 * address data from network packets
	 * 
	 * @param packet network data
	 * @return address with remaining application data
	 * @throws ParseSnapshotException
	 */
	private AddressDataWithApplicationSnapshot getAddressDataFromNetwork(IpBasedNetworkPacket packet)
			throws ParseSnapshotException {
		PortCompatibleTransportPacket transportPacket = this.transportParser
				.processPackage(packet.getTransportLayerData());
		AddressPort addressOrigin = new AddressPort(packet.getOriginIp(), transportPacket.getOriginPort());
		AddressPort addressDestination = new AddressPort(packet.getDestinationIp(),
				transportPacket.getDestinationPort());
		return new AddressDataWithApplicationSnapshot(addressOrigin, addressDestination,
				transportPacket.getApplicationLayerData());
	}

	/**
	 * Pairs communication data in coupled request-response pairs. Expected all
	 * communication data to be paired
	 * 
	 * @param mainSnapshot main snapshot. Should only be used for error reporting
	 * @param data         message data to pair
	 * @return paired communication data
	 * @throws ParseSnapshotException
	 */
	private Stream<PairedCommuncationsData> pairCommunicationsData(ITrafficSnapshot mainSnapshot,
			Stream<AddressDataWithApplicationSnapshot> data) throws ParseSnapshotException {
		// Reading how the protocol works, this makes an assumption:
		// 1) Client won't make another request to the same server before first one is
		// answered. Since application doesn't use any identification for the OP, and
		// trusts on the client address, this seems to be the case
		Map<String, AddressDataWithApplicationSnapshot> dataMap = new HashMap<>();
		List<PairedCommuncationsData> pairedComms = new ArrayList<>();

		// necessarily sequential, as we need to process this in strict order
		data.sequential().forEach(d -> this.pairCommunication(dataMap, d, pairedComms));

		// If map is not empty, there's unpaired comms. that should not happen. If we
		// wanted to process these, we could just fill another list, extract application
		// data with the same methods, and return it in an object different that Comm.
		if (!dataMap.isEmpty()) {
			AddressDataWithApplicationSnapshot unpaired = dataMap.get(dataMap.keySet().iterator().next());
			throw new ParseSnapshotException(unpaired.getRemainingApplication(), NETWORK_LAYER, 0,
					"Unpaired communication");
		}

		return pairedComms.stream();
	}

	/**
	 * Pairs a communication
	 * 
	 * @param currentStatus unpaired messages sent before
	 * @param data          communication to pair
	 * @param result        list of paired communications
	 */
	private void pairCommunication(Map<String, AddressDataWithApplicationSnapshot> currentStatus,
			AddressDataWithApplicationSnapshot data, List<PairedCommuncationsData> result) {
		String addressDataResponse = data.getResponseKey();
		AddressDataWithApplicationSnapshot correspondingData = currentStatus.get(addressDataResponse);
		if (correspondingData != null) {
			// Pair found. we remove it from map and add the pair
			currentStatus.remove(addressDataResponse);
			PairedCommuncationsData pair = new PairedCommuncationsData(correspondingData, data);
			result.add(pair);
		} else {
			// No pair, we leave it on the map for a future
			currentStatus.put(data.getRequestKey(), data);
		}

	}

	/**
	 * Extracts comm processed object from paired messages
	 * 
	 * @param validPairs valid stream of pairs
	 * @return comm object
	 * @throws ParseSnapshotException
	 */
	private List<Comm<T>> getCommsFromPairedData(Stream<PairedCommuncationsData> validPairs)
			throws ParseSnapshotException {
		return validPairs.parallel().map(this::getCommFromPairedData).collect(Collectors.toList());
	}

	/**
	 * Extracts comm processed object from paired messags
	 * 
	 * @param pair valid pair
	 * @return comm processed object
	 * @throws ParseSnapshotException
	 */
	private Comm<T> getCommFromPairedData(PairedCommuncationsData pair) throws ParseSnapshotException {
		AddressDataWithApplicationSnapshot requestData = pair.getDataRequest();
		AddressDataWithApplicationSnapshot responseData = pair.getDataResponse();

		Msg<T> msgRequest = this.getMsgFromAddressData(requestData);
		Msg<T> msgResponse = this.getMsgFromAddressData(responseData);

		return new Comm<>(msgRequest, msgResponse);
	}

	/**
	 * Creates message from address data
	 * 
	 * @param data address data plus application snapshot
	 * @return message
	 */
	private Msg<T> getMsgFromAddressData(AddressDataWithApplicationSnapshot data) {
		List<T> applicationP = this.parseApplicationData(data);
		return new Msg<>(data.getAddressDataOrigin(), data.getAddressDataDestination(), applicationP);
	}

	/**
	 * Gets application processed data
	 * 
	 * @param data data plus application snapshot
	 * @return data
	 */
	private List<T> parseApplicationData(AddressDataWithApplicationSnapshot data) {
		return this.applicationParser.parsePackets(data.getRemainingApplication());
	}

	/**
	 * Address data for a message
	 * 
	 * @author VV
	 *
	 */
	private static class AddressDataWithApplicationSnapshot {
		private AddressPort addressDataOrigin;
		private AddressPort addressDataDestination;
		private ITrafficSnapshot remainingApplication;

		private static final String SEPARATOR = "_";

		/**
		 * Creates address data
		 * 
		 * @param addressDataOrigin      IP and port for origin network point
		 * @param addressDataDestination IP and port for destination network point
		 * @param remainingApplication   application data
		 */
		public AddressDataWithApplicationSnapshot(AddressPort addressDataOrigin, AddressPort addressDataDestination,
				ITrafficSnapshot remainingApplication) {
			super();
			this.addressDataOrigin = addressDataOrigin;
			this.addressDataDestination = addressDataDestination;
			this.remainingApplication = remainingApplication;
		}

		/**
		 * Gets the IP and port for origin network point
		 * 
		 * @return address data
		 */
		public AddressPort getAddressDataOrigin() {
			return addressDataOrigin;
		}

		/**
		 * Gets the IP and port for destination network point
		 * 
		 * @return address data
		 */
		public AddressPort getAddressDataDestination() {
			return addressDataDestination;
		}

		/**
		 * Gets the remaining application data
		 * 
		 * @return application data
		 */
		public ITrafficSnapshot getRemainingApplication() {
			return remainingApplication;
		}

		/**
		 * Gets key identifying the expected request
		 * 
		 * @return key
		 */
		public String getRequestKey() {
			return this.getKey(addressDataOrigin, addressDataDestination);
		}

		/**
		 * Gets key identifying the expected response
		 * 
		 * @return key
		 */
		public String getResponseKey() {
			return this.getKey(addressDataDestination, addressDataOrigin);
		}

		/**
		 * Gets key
		 * 
		 * @param origin      origin address
		 * @param destination destination address
		 * @return key
		 */
		private String getKey(AddressPort origin, AddressPort destination) {
			StringBuilder sb = new StringBuilder();
			sb.append(origin.getIp());
			sb.append(SEPARATOR);
			sb.append(origin.getPort());
			sb.append(SEPARATOR);
			sb.append(destination.getIp());
			sb.append(SEPARATOR);
			sb.append(destination.getPort());
			return sb.toString();
		}
	}

	/**
	 * Paired communications data
	 * 
	 * @author Javier Verde
	 *
	 */
	private static class PairedCommuncationsData {
		private AddressDataWithApplicationSnapshot dataRequest;
		private AddressDataWithApplicationSnapshot dataResponse;

		/**
		 * Creates a pair
		 * 
		 * @param dataRequest  data for the request
		 * @param dataResponse data for the response
		 */
		public PairedCommuncationsData(AddressDataWithApplicationSnapshot dataRequest,
				AddressDataWithApplicationSnapshot dataResponse) {
			super();
			this.dataRequest = dataRequest;
			this.dataResponse = dataResponse;
		}

		/**
		 * Creates the data for the request
		 * 
		 * @return data for the request
		 */
		public AddressDataWithApplicationSnapshot getDataRequest() {
			return dataRequest;
		}

		/**
		 * Creates the data for the response
		 * 
		 * @return data for the response
		 */
		public AddressDataWithApplicationSnapshot getDataResponse() {
			return dataResponse;
		}

	}

}
