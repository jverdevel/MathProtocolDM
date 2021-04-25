package net.protocols.exception;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import net.snapshot.ITrafficSnapshot;

public class ParseSnapshotExceptionTest {

	@Test
	public void validExceptionTest() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		ParseSnapshotException exception = new ParseSnapshotException(snapshot, "exampleLayer", 300);
		Assert.assertEquals(300, exception.getPosition());
	}

	@Test
	public void validMessageTest() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		ParseSnapshotException exception = new ParseSnapshotException(snapshot, "exampleLayer", 300);
		String msg = exception.getMessage();
		Assert.assertTrue(msg.contains("exampleLayer"));
		Assert.assertTrue(msg.contains("300"));
	}

	@Test
	public void alwaysFullSnapshotTest() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		ITrafficSnapshot fullSnapshot = Mockito.mock(ITrafficSnapshot.class);
		Mockito.when(snapshot.getFullSnapshot()).thenReturn(fullSnapshot);
		ParseSnapshotException exception = new ParseSnapshotException(snapshot, "exampleLayer", 300);
		Assert.assertEquals(fullSnapshot, exception.getSnapshot());
	}

	@Test
	public void checkExtraMsgTest() {
		ITrafficSnapshot snapshot = Mockito.mock(ITrafficSnapshot.class);
		ParseSnapshotException exception = new ParseSnapshotException(snapshot, "exampleLayer", 300, "extraMsg");
		String msg = exception.getMessage();
		Assert.assertTrue(msg.endsWith("extraMsg"));
	}

}
