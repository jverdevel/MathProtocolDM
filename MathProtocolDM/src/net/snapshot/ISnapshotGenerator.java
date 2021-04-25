package net.snapshot;

@FunctionalInterface
/**
 * Generator for traffic snapshots
 * @author VV
 *
 */
public interface ISnapshotGenerator {

	/**
	 * Creates a snapshot from a byte array
	 * @param bytes bytes
	 * @return snapshot generator
	 */
	public ITrafficSnapshot getSnapshot(byte[] bytes);
}
