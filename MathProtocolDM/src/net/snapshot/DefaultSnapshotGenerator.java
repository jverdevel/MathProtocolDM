package net.snapshot;

/**
 * Default generator for snapshots based on byte arrays
 * 
 * @author Javi
 *
 */
public class DefaultSnapshotGenerator implements ISnapshotGenerator {

	@Override
	public ITrafficSnapshot getSnapshot(byte[] bytes) {
		return new ByteArrayBasedTrafficSnapshot(bytes);
	}
}
