
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;

public class SwingEmptyJDialogTest {
	@SuppressWarnings("serial")
	private static class EmptyJDialog extends JDialog {
		public EmptyJDialog() {
			setSize( 200, 100 );
		}
		public static void main(final String[] args) {
			JDialog frame = new EmptyJDialog();
			frame.setTitle( args[0] ); // for testing purposes (to identify if a window was left open after a test)
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		}
	}

	@Test
	public void testEmptyTitle() {
		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				EmptyJDialog.main(new String[]{ "" });
			}
			@Override
			public void test(JDialog dialog) {
				String actual = dialog.getTitle();
				assertTrue( "", actual.isEmpty() );
			}
		});
	}
	@Test
	public void testHasJPanelsOnly() {
		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				EmptyJDialog.main(new String[]{ "jpanels" });
			}
			@Override
			public void test(JDialog frame) {
				List<JComponent> components = Gooey.getComponents( frame, JComponent.class );
				components.stream()
				          .filter ( c -> !( c instanceof JPanel || 
				                            c instanceof JRootPane ||
				                            c instanceof JLayeredPane ))
				          .forEach( c-> fail("Unexpected JComponent: "+ c.getClass().getName()));
			}
		});
	}
	@Test
	public void testFrameClosesWithExit() {
		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				EmptyJDialog.main(new String[]{ "close icon" });
			}
			@Override
			public void test(JDialog frame) {
				assertTrue  ( "JFrame should be displayed", frame.isShowing() );
				frame.dispatchEvent(new WindowEvent( frame, WindowEvent.WINDOW_CLOSING ));
				assertFalse ( "JFrame should be hidden",    frame.isShowing() );
			}
		});
	}
}
