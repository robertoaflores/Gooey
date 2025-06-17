package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

public class SwingJOptionPaneDialogsTest {

	private static class MessageDialog {
		public static void main(String[] args) {
			JOptionPane.showMessageDialog( null, "random message" );
		}
	}
	@Test
	public void testMessageDialogButtons() {
		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				MessageDialog.main( new String[]{ } );
			}
			@Override
			public void test(JDialog dialog) {
				assertEquals( "Message", dialog.getTitle(), "Unexpected title" );
				assertTrue  ( Gooey.getButton( dialog, "OK"    ).isDefaultButton());

				Gooey.getLabel( dialog, "random message" );
			}
			@Override
			public long getTimeout() {
				return 3000L;
			}
		});
	}

	private static class ConfirmDialog {
		public static void main(String[] args) {
			JOptionPane.showConfirmDialog( null, "random message" );
		}
	}
	@Test
	public void testConfirmDialogButtons() {
		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				ConfirmDialog.main( new String[]{ } );
			}
			@Override
			public void test(JDialog dialog) {
				assertEquals( "Select an Option", dialog.getTitle(), "Unexpected title" );
				assertTrue  ( Gooey.getButton( dialog, "Yes"    ).isDefaultButton());

				Gooey.getButton( dialog, "No"     );
				Gooey.getButton( dialog, "Cancel" );
				Gooey.getLabel ( dialog, "random message" );
			}
		});
	}

	private static class InputDialog {
		public static void main(String[] args) {
			JOptionPane.showInputDialog( null, "random message" );
		}
	}
	@Test
	public void testInputDialogButtons() {
		Gooey.capture( new GooeyDialog() {
			@Override
			public void invoke() {
				InputDialog.main( new String[]{ } );
			}
			@Override
			public void test(JDialog dialog) {
				assertEquals( "Input", dialog.getTitle(), "Unexpected title" );
				assertTrue  ( Gooey.getButton( dialog, "OK" ).isDefaultButton());

				Gooey.getButton( dialog, "Cancel" );
				Gooey.getLabel ( dialog, "random message" );
			}
		});
	}
}
