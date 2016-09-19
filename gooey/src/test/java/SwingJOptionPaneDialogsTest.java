
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;

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
				assertEquals( "Unexpected title", "Message", dialog.getTitle() );
				assertTrue  ( "", Gooey.getButton( dialog, "OK"    ).isDefaultButton());

				Gooey.getLabel( dialog, "random message" );
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
				assertEquals( "Unexpected title", "Select an Option", dialog.getTitle() );
				assertTrue  ( "", Gooey.getButton( dialog, "Yes"    ).isDefaultButton());

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
				assertEquals( "Unexpected title", "Input", dialog.getTitle() );
				assertTrue  ( "", Gooey.getButton( dialog, "OK" ).isDefaultButton());

				Gooey.getButton( dialog, "Cancel" );
				Gooey.getLabel ( dialog, "random message" );
			}
		});
	}
}
