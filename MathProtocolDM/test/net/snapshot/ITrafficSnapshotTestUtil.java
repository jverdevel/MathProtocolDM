package net.snapshot;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * VV: Test class to provide mocks for a traffic snapshot that substrings and creates other snapshots properly without worring about details
 * @author Javi
 *
 */
public class ITrafficSnapshotTestUtil {

	public static ITrafficSnapshot createSnapshot(String text) {
		ITrafficSnapshot mockSnapshot = Mockito.mock(ITrafficSnapshot.class);
		Mockito.when(mockSnapshot.getLength()).thenReturn(text.length());
		Mockito.when(mockSnapshot.getString(Mockito.anyInt(), Mockito.anyInt())).thenAnswer(new AnswerSubString(text));
		Mockito.when(mockSnapshot.getFullSnapshot()).thenReturn(mockSnapshot);
		Mockito.when(mockSnapshot.getSnapshotFragment(Mockito.anyInt(), Mockito.anyInt())).thenAnswer(new AnswerSubSnapshot(text));
		return mockSnapshot;
	}
	
	private static class AnswerSubString implements Answer<String>{

		private String stringValue;
		
		public AnswerSubString(String stringValue) {
			this.stringValue = stringValue;
		}
		
		@Override
		public String answer(InvocationOnMock invocation) throws Throwable {
			Integer from = invocation.getArgument(0, Integer.class);
			Integer to = invocation.getArgument(1, Integer.class);
			return this.stringValue.substring(from, to);
		}
		
	}
	
	private static class AnswerSubSnapshot implements Answer<ITrafficSnapshot>{
		
		private String stringValue;				

		public AnswerSubSnapshot(String stringValue) {
			super();
			this.stringValue = stringValue;
		}

		@Override
		public ITrafficSnapshot answer(InvocationOnMock invocation) throws Throwable {
			Integer from = invocation.getArgument(0, Integer.class);
			Integer to = invocation.getArgument(1, Integer.class);
			String subString = this.stringValue.substring(from, to);
			return createSnapshot(subString);
		}
		
		
	}
	
	
}
