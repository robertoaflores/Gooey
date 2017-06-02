
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyFrame;

public class SwingEmptyJFrameTest {
	@SuppressWarnings("serial")
	private static class EmptyJFrame extends JFrame {
		public EmptyJFrame() {
			setSize( 200, 100 );
		}
		public static void main(final String[] args) {
			JFrame frame = new EmptyJFrame();
			frame.setTitle( args[0] ); // for testing purposes (to identify if a window was left open after a test)
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		}
	}

	@Test
	public void testEmptyTitle() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "" });
			}
			@Override
			public void test(JFrame frame) {
				String actual = frame.getTitle();
				assertTrue( "", actual.isEmpty() );
			}
		});
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();	
	
	@Test
	public void testDoesntHaveMenuBar() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage( "Menubar not found" );
		
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "no menu" });
			}
			@Override
			public void test(JFrame frame) {
				Gooey.getMenuBar( frame );
			}
		});
	}
	@Test
	public void testDoesntHaveButton() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage( "Component \"javax.swing.JButton\" not found" );
		
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "no button" });
			}
			@Override
			public void test(JFrame frame) {
				Gooey.getComponent( frame, JButton.class );
			}
		});
	}
	@Test
	public void testDoesntHaveLabel() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage( "Component \"javax.swing.JLabel\" not found" );
		
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "no label" });
			}
			@Override
			public void test(JFrame frame) {
				Gooey.getComponent( frame, JLabel.class );
			}
		});
	}
	@Test
	public void testDoesntHaveTextFields() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage( "Component \"javax.swing.JTextField\" not found" );
		
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "no text field" });
			}
			@Override
			public void test(JFrame frame) {
				Gooey.getComponent( frame, JTextField.class );
			}
		});
	}
	@Test
	public void testHasJPanelsOnly() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "jpanels" });
			}
			@Override
			public void test(JFrame frame) {
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
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "close icon" });
			}
			@Override
			public void test(JFrame frame) {
				assertTrue  ( "JFrame should be displayed", frame.isShowing() );
				frame.dispatchEvent(new WindowEvent( frame, WindowEvent.WINDOW_CLOSING ));
				assertFalse ( "JFrame should be hidden",    frame.isShowing() );
			}
		});
	}
}
