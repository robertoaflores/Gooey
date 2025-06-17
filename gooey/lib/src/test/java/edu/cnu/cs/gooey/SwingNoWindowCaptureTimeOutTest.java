package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

class SwingNoWindowCaptureTimeOutTest {
	@Test
	void testNothingInvoked() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
				}
				@Override
				public void test(JFrame frame) {
				}
			})
		);
		assertEquals( "JFrame not detected", e.getMessage() );
	}

	
	@SuppressWarnings("serial")
	private static class InvisibleWindow extends JFrame {
		/**
		 * A JFrame is created but not displayed.
		 */
		public static void main(String[] args) {
			JFrame frame = new InvisibleWindow();
			frame.setLocationRelativeTo( null );
			/*
			 * Normal behavior would be to call setVisible. It was
			 * commented out for testing purposes so that a window 
			 * is created but not made visible.
			 */
			 // frame.setVisible( true );
		}
	}
	@Test
	void testWindowNotDisplayed() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					InvisibleWindow.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
				}
			})
		);
		assertEquals( "JFrame not detected", e.getMessage() );
	}
	
	private static class NoWindow {
		public static void main(String[] args) {
		}
	}
	@Test
	void testNoWindowDisplayed() {
		Throwable e = assertThrows( AssertionError.class, () ->
		Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NoWindow.main( new String[]{} );
				}
				@Override
				public void test(JFrame window) {
				}
			})
		);
		assertEquals( "JFrame not detected", e.getMessage() );
	}
	@Test
	void testNoDialogDisplayed() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyDialog() {
				@Override
				public void invoke() {
					NoWindow.main( new String[]{} );
				}
				@Override
				public void test(JDialog window) {
				}
			})
		);
		assertEquals( "JDialog not detected", e.getMessage() );
	}

	// A JFrame (not a JDialog) displayed 
	@Test
	void testFrameInsteadOfExpectedDialogDisplayed() {
		var e = assertThrows( AssertionError.class, () -> {
			JFrame frame = new JFrame("I'm a JFrame");
			frame.setSize( 300, 100 );
			try {
				Gooey.capture( new GooeyDialog() {
					@Override
					public void invoke() {
						frame.setVisible( true );
					}
					@Override
					public void test(JDialog frame) {
					}
				});
			} finally {
				frame.dispose();
			}
		});
		assertEquals( "JDialog not detected", e.getMessage() );
	}
	// A JDialog (not a JFrame) displayed 
	@Test
	void testDialogInsteadOfExpectedFrameDisplayed() {
		Throwable e = assertThrows( AssertionError.class, () -> {
			JDialog dialog = new JDialog(new JFrame(),"I'm a JDialog",true);
			dialog.setSize ( 300, 200 );
			try {
				Gooey.capture( new GooeyFrame() {
					@Override
					public void invoke() {
						dialog.setVisible( true );
					}
					@Override
					public void test(JFrame frame) {
					}
				});
			} finally {
				dialog.dispose();
			}
		});
		assertEquals( "JFrame not detected", e.getMessage() );
	}
}
