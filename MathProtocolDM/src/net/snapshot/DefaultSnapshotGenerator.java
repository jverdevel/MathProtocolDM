package net.snapshot;

/**
 * Default generator for snapshots based on byte arrays
 * 
 * @author Javier Verde
 *
 */
public class DefaultSnapshotGenerator implements ISnapshotGenerator {

	@Override
	public ITrafficSnapshot getSnapshot(byte[] bytes) {
		// We won't take charsets into account here since there test allows for using
		// standard getBytes()
		return new StringBasedTrafficSnapshot(new String(bytes));
	}

	// A bit of an explanation here. Snapshots are used to decouple the data storage
	// from the parser. For all we know, data might come out of an actual
	// connection, or a database or anything like that. While solution asks for
	// byte[], replacing byte[] in the crunch function for one of these snapshots or
	// generators would be straightforward.
	// my integration tests didn't find much of a difference in memory or
	// performance from the original byte-backed snapshot and this simpler, a lot
	// less platform-dependant one, so decided to go simple. Left the other one, as
	// it might be useful for some edge case concerning REALLY big strings.
}
