package net.protocols.transport.parser;

import java.util.List;

import net.protocols.exception.ParseSnapshotException;
import net.protocols.transport.pack.PortCompatibleTransportPackage;
import net.snapshot.ITrafficSnapshot;

/**
 * VV: Parser para un protocolo de transporte compatible con el concepto de puerto
 * @author Javier Verde
 *
 */
public interface IPortCompatibleTransportProtocolParser {
	
	/**
	 * VV: Procesa el paquete contenido en un snapshot de trafico
	 * @param snapshot snapshot
	 * @return paquete
	 * @throws ParseSnapshotException
	 */
	public PortCompatibleTransportPackage processPackage(ITrafficSnapshot snapshot) throws ParseSnapshotException;
}
