package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.junit.jupiter.api.Test;


public class SwingNameJFrameTest {

	@SuppressWarnings("serial")
	private static class NameJFrame extends JFrame {
		private static class NameDialog extends JDialog {
			private JTextField jtfName;

			private NameDialog() {
				setTitle( "Edit Name" );

				JPanel jpnPanel = new JPanel();
				add(   jpnPanel );

				JLabel        jlbLabel = new JLabel( "Name: " );
				jpnPanel.add( jlbLabel );
				jlbLabel.setName( "label" );

				jtfName = new JTextField( 10 );
				jtfName.setName( "name" );
				jpnPanel.add( jtfName );

				JButton jbtOk     = new JButton( "Ok" );
				JButton jbtCancel = new JButton( "Cancel" );

				jpnPanel.add( jbtOk );
				jpnPanel.add( jbtCancel );

				jbtOk.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent event) {
						final String name = jtfName.getText();
						if (name.length() > 0) {
							dispose();
						}
						else {
							JOptionPane.showMessageDialog( NameDialog.this, "The name cannot be empty.", "Name Input Error", JOptionPane.ERROR_MESSAGE );
						}
					}
				});
				jbtCancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent event) {
						jtfName.setText( "" );
						dispose();
					}
				});

				pack();

				setModal( true );
				setLocationRelativeTo( null );
			}
			public static String editName(String aName) {
				NameDialog dialog = new NameDialog();
				// set data
				dialog.jtfName.setText( aName );
				dialog.jtfName.selectAll();
				// display dialog
				dialog.setVisible( true );
				// get data
				String result = dialog.jtfName.getText();
				return result;
			}
		}

		private JLabel jlbText;

		public NameJFrame() {
			super( "Example" );

			// menu
			JMenuBar     jmbMain = new JMenuBar();
			setJMenuBar( jmbMain );

			JMenu        jmnProgram = new JMenu( "Program" );
			jmbMain.add( jmnProgram );

			JMenuItem jmiEdit = jmnProgram.add( "Edit Name..." );
			jmnProgram.addSeparator();
			JMenuItem jmiExit = jmnProgram.add( "Exit" );

			jmiEdit.addActionListener( e->editName() );
			jmiExit.addActionListener( e->closeWindow() );

			// labels & button
			JPanel jpnPanel = new JPanel();
			add(   jpnPanel );

			JLabel jlbLabel = new JLabel( "Name: " );
			jlbLabel.setName( "label" );
			jpnPanel.add( jlbLabel );

			jlbText = new JLabel( "<no name>" ) {
				@Override
				public Dimension getPreferredSize() {
					final Dimension dimension = super.getPreferredSize();
					dimension.width = 200;
					return dimension;
				}
			};
			jlbText.setName( "current" );
			jpnPanel.add( jlbText );

			JButton       jbtEdit = new JButton( "Edit Name" );
			jpnPanel.add( jbtEdit );
			jbtEdit.addActionListener( e->editName() );
			addWindowListener( new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					closeWindow();
				}
			});
			pack();
			setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		}
		private void closeWindow() {
			int what = JOptionPane.showConfirmDialog( NameJFrame.this, "Wanna close?" );
			if (what == JOptionPane.YES_OPTION) {
				dispose();
			}
		}
		private void editName() {
			String name  = jlbText.getText();
			String aName = NameDialog.editName( name );
			if (aName.length() > 0 && !name.equals( aName )) {
				jlbText.setText( aName );
			}
		}

		public static void main(final String[] args) {
			JFrame frame = new NameJFrame();
			frame.setLocationRelativeTo( null );
			frame.setVisible( true );
		}
	}

	@Test
	public void testHasTitle() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
				String actual   = frame.getTitle();
				String expected = "Example";
				assertEquals( expected, actual );
			}
		});
	}
	@Test
	public void testHasMenu() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
				JMenuBar        menubar = Gooey.getMenuBar( frame );
				JMenu           program = Gooey.getMenu   ( menubar, "Program" );
				JMenuItem       edit    = Gooey.getMenu   ( program, "Edit Name..." ); 
				JMenuItem       exit    = Gooey.getMenu   ( program, "Exit" ); 

				List<JMenu>     menus   = Gooey.getMenus( menubar );
				assertEquals( 1, menus.size() );
				assertTrue  (    menus.contains( program ));

				List<JMenuItem> items = Gooey.getMenus( program );
				assertEquals( 2, items.size() );
				assertTrue  (    items.contains( edit ));
				assertTrue  (    items.contains( exit ));
			}
		});
	}
	@Test
	public void testHasLabel() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
				Gooey.getLabel (frame, "<no name>");
			}
		});
	}
	@Test
	public void testHasButton() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
				Gooey.getButton(frame, "Edit Name");
			}
		});
	}
	@Test
	public void testProgramClosesWithExitMenu() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
				JMenuBar  menubar = Gooey.getMenuBar( frame );
				JMenu     program = Gooey.getMenu   ( menubar, "Program" );
				JMenuItem exit    = Gooey.getMenu   ( program, "Exit" ); 

				assertTrue ( frame.isShowing(), "JFrame should be displayed" );
				Gooey.capture( new GooeyDialog() {
					@Override
					public void invoke() {
						exit.doClick();
					}
					@Override
					public void test(JDialog dialog) {
						Gooey.getButton( dialog, "Yes" ).doClick();
					}
				});
				assertFalse( frame.isShowing(), "JFrame should be hidden" );
			}
		});
	}
	@Test
	public void testEditDialogShowsWithMenu() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
				JMenuBar        menubar = Gooey.getMenuBar( frame );
				JMenu           program = Gooey.getMenu( menubar, "Program" );
				JMenuItem       edit    = Gooey.getMenu   ( program, "Edit Name..." ); 

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
	public void testEditDialogHasComponents() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
			}
			@Override
			public void test(JFrame frame) {
				JMenuBar        menubar = Gooey.getMenuBar( frame );
				JMenu           program = Gooey.getMenu   ( menubar, "Program" );
				JMenuItem       edit    = Gooey.getMenu   ( program, "Edit Name..." ); 

				Gooey.capture( new GooeyDialog() {
					@Override
					public void invoke() {
						edit.doClick();
					}
					@Override
					public void test(JDialog dialog) {
						assertEquals( "Edit Name", dialog.getTitle() );

						JButton ok     = Gooey.getButton( dialog, "Ok");
						JButton cancel = Gooey.getButton( dialog, "Cancel");

						List<JButton> items = Gooey.getComponents( dialog, JButton.class );
						assertEquals( 2, items.size() );
						assertTrue  (    items.contains( ok ));
						assertTrue  (    items.contains( cancel ));

						Gooey.getLabel ( dialog, "Name: ");

						JTextField jtf = Gooey.getComponent( dialog, JTextField.class );
						assertEquals( "<no name>", jtf.getText() );
					}
				});
			}
		});
	}
	@Test
	public void testEditDialogShowsWithButton() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
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
						assertEquals( "Edit Name", dialog.getTitle() );

						JButton ok     = Gooey.getButton( dialog, "Ok");
						JButton cancel = Gooey.getButton( dialog, "Cancel");

						List<JButton> items = Gooey.getComponents( dialog, JButton.class );
						assertEquals( 2, items.size() );
						assertTrue  (    items.contains( ok ));
						assertTrue  (    items.contains( cancel ));

						Gooey.getLabel ( dialog, "Name: ");

						JTextField jtf = Gooey.getComponent( dialog, JTextField.class );
						assertEquals( "<no name>", jtf.getText() );
					}
				});
			}
		});
	}
	@Test
	public void testEditDialogCancellingDoesNotChangeName() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
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
						JButton cancel = Gooey.getButton( dialog, "Cancel");
						cancel.doClick();
					}
				});
				assertEquals( "<no name>", label.getText() );
			}
		});
	}
	@Test
	public void testEditDialogGivingNameChangesName() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
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
						JButton    ok  = Gooey.getButton   ( dialog, "Ok");
						jtf.setText( "Neil Peart" );
						ok .doClick();
					}
				});
				assertEquals( "Neil Peart", label.getText() );
			}
		});
	}
	@Test
	public void testEditDialogEmptyNameHasErrorDialog() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				NameJFrame.main(new String[]{});
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
						JButton    ok  = Gooey.getButton   ( dialog, "Ok");
						jtf.setText( "" );
						Gooey.capture( new GooeyDialog() {
							@Override
							public void invoke() {
								ok.doClick();
							}
							@Override
							public void test(JDialog dialog) {
								assertEquals( "Name Input Error", dialog.getTitle() );

								Gooey.getLabel ( dialog, "The name cannot be empty.");

								JButton       ok    = Gooey.getButton   ( dialog, "OK");
								List<JButton> items = Gooey.getComponents( dialog, JButton.class );
								assertEquals( 1, items.size() );
								assertTrue  (    items.contains( ok ));

								ok .doClick();
							}
						});
						JButton    cancel = Gooey.getButton   ( dialog, "Cancel");
						cancel.doClick();
					}
				});
				assertEquals( "<no name>", label.getText() );
			}
		});
	}
}
