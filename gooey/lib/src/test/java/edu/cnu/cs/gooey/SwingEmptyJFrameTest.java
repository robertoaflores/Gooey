package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

import org.junit.jupiter.api.Test;

class SwingEmptyJFrameTest {
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
	void testEmptyTitle() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "" });
			}
			@Override
			public void test(JFrame frame) {
				String actual = frame.getTitle();
				assertTrue( actual.isEmpty() );
			}
		});
	}

	@Test
	void testDoesntHaveMenuBar() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					EmptyJFrame.main(new String[]{ "no menu" });
				}
				@Override
				public void test(JFrame frame) {
					Gooey.getMenuBar( frame );
				}
			})
		);
		assertEquals( "Menubar not found", e.getMessage());		
	}
	@Test
	void testDoesntHaveButton() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					EmptyJFrame.main(new String[]{ "no button" });
				}
				@Override
				public void test(JFrame frame) {
					Gooey.getComponent( frame, JButton.class );
				}
			})
		);
		assertEquals( "Component \"javax.swing.JButton\" not found", e.getMessage());		
	}
	@Test
	void testDoesntHaveLabel() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					EmptyJFrame.main(new String[]{ "no label" });
				}
				@Override
				public void test(JFrame frame) {
					Gooey.getComponent( frame, JLabel.class );
				}
			})
		);
		assertEquals( "Component \"javax.swing.JLabel\" not found", e.getMessage());		
	}
	@Test
	void testDoesntHaveTextFields() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					EmptyJFrame.main(new String[]{ "no text field" });
				}
				@Override
				public void test(JFrame frame) {
					Gooey.getComponent( frame, JTextField.class );
				}
			})
		);
		assertEquals( "Component \"javax.swing.JTextField\" not found", e.getMessage());		
	}
	@Test
	void testHasJPanelsOnly() {
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
			@Override
			protected long getTimeout() {
				return 3000L;
			}
		});
	}
	@Test
	void testFrameClosesWithExit() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				EmptyJFrame.main(new String[]{ "close icon" });
			}
			@Override
			public void test(JFrame frame) {
				assertTrue  ( frame.isShowing(), "JFrame should be displayed" );
				frame.dispatchEvent(new WindowEvent( frame, WindowEvent.WINDOW_CLOSING ));
				assertFalse ( frame.isShowing(), "JFrame should be hidden" );
			}
		});
	}
}
