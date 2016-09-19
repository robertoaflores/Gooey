/**
 * <p>Copyright: Copyright (c) 2013, JoSE Group, Christopher Newport University. 
 * Permission to use, copy, modify, distribute and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation.  
 * The JoSE Group makes no representations about the suitability
 * of  this software for any purpose. It is provided "as is" without express
 * or implied warranty.</p>
 * <p>Company: JoSE Group, Christopher Newport University</p>
 */

import javax.swing.JFrame;
import java.awt.Window;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyFrame;
import edu.cnu.cs.gooey.GooeyWindow;

public class SwingExceptionsTest {

	@Test(expected=IllegalArgumentException.class)
	public void testCaptureGooeyWindowWithNullClass() {
		Gooey.capture( new GooeyWindow<Window>( null, "" ) {
			@Override
			public void invoke() {
			}
			@Override
			public void test(Window capturedWindow) {
			}
		});
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCaptureGooeyWindowWithNullMessage() {
		Gooey.capture( new GooeyFrame( null ) {
			@Override
			public void invoke() {
			}
			@Override
			public void test(JFrame capturedWindow) {
			}
		});
	}

	// Exception test
	@Test(expected=RuntimeException.class)
	public void testInvokeThrowsException() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					throw new RuntimeException();
				}
				@Override
				public void test(JFrame window) {
				}
			});
	}
	@Test(expected=RuntimeException.class)
	public void testTestThrowsException() {
		JFrame frame = new JFrame();
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					frame.setVisible( true );
				}
				@Override
				public void test(JFrame window) {
					throw new RuntimeException();
				}
			});
		frame.dispose();
	}

}
