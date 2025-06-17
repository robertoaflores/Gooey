package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.junit.jupiter.api.Test;

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
				assertTrue( actual.isEmpty() );
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
				assertTrue  ( frame.isShowing(), "JDialog should be displayed" );
				frame.dispatchEvent(new WindowEvent( frame, WindowEvent.WINDOW_CLOSING ));
				assertFalse ( frame.isShowing(), "JDialog should be hidden" );
			}
		});
	}
}
