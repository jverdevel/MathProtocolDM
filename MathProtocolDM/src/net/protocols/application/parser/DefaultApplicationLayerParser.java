package net.protocols.application.parser;

import net.protocols.application.IApplicationLayerPacket;
import net.protocols.parser.DefaultSnapshotTxtBasedParser;

/**
 * Parser that extracts application package information from a network traffic snapshot
 * @author Javier Verde
 * 
 * @param <T> type of application layer packet parsed
 *
 */
public abstract class DefaultApplicationLayerParser<T extends IApplicationLayerPacket> extends DefaultSnapshotTxtBasedParser implements IApplicationLayerParser<T>{

}
