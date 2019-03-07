import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class SwingNoWindowCaptureTimeOutTest {
	@Test
	public void testNothingInvoked() {
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
	public void testWindowNotDisplayed() {
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
	public void testNoWindowDisplayed() {
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
	public void testNoDialogDisplayed() {
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
	public void testFrameInsteadOfExpectedDialogDisplayed() {
		Throwable e = assertThrows( AssertionError.class, () -> {
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
	public void testDialogInsteadOfExpectedFrameDisplayed() {
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
