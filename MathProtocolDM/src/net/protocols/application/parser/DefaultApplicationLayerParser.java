package net.protocols.application.parser;

import net.protocols.application.IApplicationLayerPacket;
import net.protocols.parser.DefaultSnapshotParser;

/**
 * Parser that extracts application package information from a network traffic snapshot
 * @author Javier Verde
 * 
 * @param <T> type of application layer packet parsed
 *
 */
public abstract class DefaultApplicationLayerParser<T extends IApplicationLayerPacket> extends DefaultSnapshotParser implements IApplicationLayerParser<T>{

}
