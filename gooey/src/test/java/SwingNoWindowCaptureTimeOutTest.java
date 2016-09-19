
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class SwingNoWindowCaptureTimeOutTest {
	
	@Test(expected=AssertionError.class)
	public void testNothingInvoked() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
			}
			@Override
			public void test(JFrame frame) {
			}
		});
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
	@Test(expected=AssertionError.class)
	public void testWindowNotDisplayed() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				InvisibleWindow.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
			}
		});
	}
	
	
	private static class NoWindow {
		public static void main(String[] args) {
		}
	}
	@Test(expected=AssertionError.class)
	public void testNoWindowDisplayed() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					NoWindow.main( new String[]{} );
				}
				@Override
				public void test(JFrame window) {
				}
			});
	}
	@Test(expected=AssertionError.class)
	public void testNoDialogDisplayed() {
		Gooey.capture(
			new GooeyDialog() {
				@Override
				public void invoke() {
					NoWindow.main( new String[]{} );
				}
				@Override
				public void test(JDialog window) {
				}
			});
	}

	// A JFrame (not a JDialog) displayed 
	@Test(expected=AssertionError.class)
	public void testFrameInsteadOfExpectedDialogDisplayed() {
		JFrame frame = new JFrame("I'm a JFrame");
		frame.setSize( 300, 100 );
		try {
			Gooey.capture(
					new GooeyDialog() {
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
	}
	// A JDialog (not a JFrame) displayed 
	@Test(expected=AssertionError.class)
	public void testDialogInsteadOfExpectedFrameDisplayed() {
		JDialog dialog = new JDialog(new JFrame(),"I'm a JDialog",true);
		dialog.setSize ( 300, 200 );
		try {
			Gooey.capture(
					new GooeyFrame() {
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
	}
}
