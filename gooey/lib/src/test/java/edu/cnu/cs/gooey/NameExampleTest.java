package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class NameExampleTest {
	@BeforeAll
	public static void preLoad() {
		JFrame f = new NameExample();
		f.setVisible(true);
		f.dispose();
	}
	@Nested
	class NonFunctional {
		@Test
		void testTitleMenuComponents() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					String actual   = frame.getTitle();
					String expected = "Example";
					assertEquals( expected, actual );

					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           program = Gooey.getMenu( menubar, "Program" );
					JMenuItem       edit    = Gooey.getMenu( program, "Edit Name..." ); 
					JMenuItem       exit    = Gooey.getMenu( program, "Exit" ); 

					List<JMenu>     menus   = Gooey.getMenus( menubar );
					assertEquals( 1, menus.size(), "Unexpected number of menus in menubar" );
					assertTrue  ( menus.contains( program ), "Menubar doesn't have 'Program' menu" );

					List<JMenuItem> items = Gooey.getMenus( program );
					assertEquals( 2, items.size(), "Unexpected number of options in menu 'Program'" );
					assertTrue  (    items.contains( edit ), "'Program' menu doesn't have 'Edit Name...' option" );
					assertTrue  (    items.contains( exit ), "'Program' menu doesn't have 'Exit' option" );

					Gooey.getButton(frame, "Edit Name");
					Gooey.getLabel (frame, "Name: ");
					Gooey.getLabel (frame, "<no name>");
				}
			});
		}
		@Test
		void testProgramClosesWithExit() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           program = Gooey.getMenu( menubar, "Program" );
					JMenuItem       exit    = Gooey.getMenu( program, "Exit" ); 

					assertTrue  ( frame.isShowing(), "JFrame should be displayed" );
					exit.doClick();
					assertFalse ( frame.isShowing(), "JFrame should be hidden" );
				}
			});
		}
		@Test
		void testEditDialogShowsWithMenu() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           program = Gooey.getMenu( menubar, "Program" );
					JMenuItem       edit    = Gooey.getMenu( program, "Edit Name..." ); 

					Gooey.capture( new GooeyDialog() {
						@Override
						public void invoke() {
							edit.doClick();
						}
						@Override
						public void test(JDialog dialog) {
							JButton cancel = Gooey.getButton( dialog, "Cancel" );
							cancel.doClick();
						}
					});
				}
			});
		}
		@Test
		void testEditDialogHasComponents() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					JMenuBar        menubar = Gooey.getMenuBar( frame );
					JMenu           program = Gooey.getMenu( menubar, "Program" );
					JMenuItem       edit    = Gooey.getMenu( program, "Edit Name..." ); 

					Gooey.capture( new GooeyDialog() {
						@Override
						public void invoke() {
							edit.doClick();
						}
						@Override
						public void test(JDialog dialog) {										
							JButton ok     = Gooey.getButton( dialog, "OK");
							JButton cancel = Gooey.getButton( dialog, "Cancel");

							List<JButton> items = Gooey.getComponents( dialog, JButton.class );
							assertEquals( 2, items.size(), "Unexpected number of buttons" );
							assertTrue  (    items.contains( ok ), "'OK' button not found");
							assertTrue  (    items.contains( cancel ), "'Cancel' button not found" );

							Gooey.getLabel ( dialog, "Write a name");

							JTextField jtf = Gooey.getComponent( dialog, JTextField.class );
							assertEquals( "<no name>", jtf.getText(), "Unexpected text in text field" );

							cancel.doClick();
						}
					});
				}
			});
		}
		@Test
		void testEditDialogShowsWithButton() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					JButton edit = Gooey.getButton( frame, "Edit Name" ); 

					Gooey.capture( new GooeyDialog() {
						@Override
						public void invoke() {
							edit.doClick();
						}
						@Override
						public void test(JDialog dialog) {
							JButton ok     = Gooey.getButton( dialog, "OK");
							JButton cancel = Gooey.getButton( dialog, "Cancel");

							List<JButton> items = Gooey.getComponents( dialog, JButton.class );
							assertEquals( 2, items.size(), "Unexpected number of buttons" );
							assertTrue  (    items.contains( ok ), "'OK' button not found" );
							assertTrue  (    items.contains( cancel ), "'Cancel' button not found" );

							Gooey.getLabel ( dialog, "Write a name");

							JTextField jtf = Gooey.getComponent( dialog, JTextField.class );
							assertEquals( "<no name>", jtf.getText(), "Unexpected text in text field" );

							cancel.doClick();
						}
					});
				}
			});
		}
	}
	@Nested
	class Functional {
		@Test
		void testEditDialog_CancellingDoesNotChangeName() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					JLabel  label = Gooey.getLabel ( frame, "<no name>" );

					Gooey.capture( new GooeyDialog() {
						@Override
						public void invoke() {
							Gooey.getButton( frame, "Edit Name" ).doClick();
						}
						@Override
						public void test(JDialog dialog) {
							Gooey.getButton( dialog, "Cancel").doClick();
						}
					});
					assertEquals( "<no name>", label.getText(), "Name should not be changed" );
				}
			});
		}
		@Test
		void testEditDialog_GivingNameChangesName() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					JLabel  label = Gooey.getLabel ( frame, "<no name>" );
					JButton edit  = Gooey.getButton( frame, "Edit Name" ); 

					Gooey.capture( new GooeyDialog() {
						@Override
						public void invoke() {
							edit.doClick();
						}
						@Override
						public void test(JDialog dialog) {
							JTextField jtf = Gooey.getComponent( dialog, JTextField.class );
							JButton    ok  = Gooey.getButton   ( dialog, "OK");
							jtf.setText( "Neil Peart" );
							ok .doClick();
						}
					});
					assertEquals( "Neil Peart", label.getText(), "Name should have been changed" );
				}
			});
		}
		@Test
		void testEditDialog_EmptyNameShowsErrorDialog() {
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					NameExample.main(new String[]{});
				}
				@Override
				public void test(JFrame frame) {
					JLabel  label = Gooey.getLabel ( frame, "<no name>" );
					JButton edit  = Gooey.getButton( frame, "Edit Name" ); 

					Gooey.capture( new GooeyDialog() {
						@Override
						public void invoke() {
							edit.doClick();
						}
						@Override
						public void test(JDialog dialog) {
							JTextField jtf = Gooey.getComponent( dialog, JTextField.class );
							JButton    ok  = Gooey.getButton   ( dialog, "OK");
							jtf.setText( "" );
							Gooey.capture( new GooeyDialog() {
								@Override
								public void invoke() {
									ok.doClick();
								}
								@Override
								public void test(JDialog dialog) {
									Gooey.getLabel ( dialog, "Name cannot be empty");

									JButton       ok    = Gooey.getButton   ( dialog, "OK");
									List<JButton> items = Gooey.getComponents( dialog, JButton.class );
									assertEquals( 1, items.size(), "Unexpected number of buttons" );
									assertTrue  (    items.contains( ok ), "'OK' button not found" );

									ok .doClick();
								}
							});
						}
					});
					assertEquals( "<no name>", label.getText(), "Name should not change" );
				}
			});
		}
	}
}
