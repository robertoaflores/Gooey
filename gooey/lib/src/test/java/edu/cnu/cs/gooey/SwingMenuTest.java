package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

import edu.cnu.cs.gooey.Gooey.Match;

class SwingMenuTest {
	private static final String MENU_PROGRAM = "menu.program";
	private static final String MENU_ABOUT   = "menu.program.about";
	private static final String MENU_EXIT    = "menu.program.exit";

	@SuppressWarnings("serial")
	private static class JFrameWithMenu extends JFrame {
		public JFrameWithMenu() {
			JMenuBar  main    = new JMenuBar();
			JMenu     program = new JMenu("Program");
			JMenuItem about   = program.add("About...");
			program.addSeparator();
			JMenuItem exit    = program.add("Exit");
			main.add( program );
			setJMenuBar( main );
			
			program.setName( MENU_PROGRAM );
			about  .setName( MENU_ABOUT );
			exit   .setName( MENU_EXIT );
			
			exit .addActionListener( e-> dispose() );
			about.addActionListener( e-> JOptionPane.showMessageDialog( JFrameWithMenu.this, "Sample About Dialog", "About", JOptionPane.INFORMATION_MESSAGE ) );
			setSize( 400, 120 );
		}
		public static void main(String[] args) {
			JFrame f = new JFrameWithMenu();
			f.setVisible(true);
		}
	}

	@Test
	void testDoesntHaveMenuByText() {
		Throwable e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					JFrameWithMenu.main( new String[]{ } );
				}
				@Override
				public void test(JFrame frame) {
					JMenuBar       menubar = Gooey.getMenuBar( frame );
					Gooey.getMenu( menubar, "Blah" );
				}
			})
		);
		assertEquals( "Menu \"Blah\" not found (searched by label)", e.getMessage() );
		
	}
	@Test
	void testDoesntHaveMenuByName() {
		var e = assertThrows( AssertionError.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					JFrameWithMenu.main( new String[]{ } );
				}
				@Override
				public void test(JFrame frame) {
					JMenuBar       menubar = Gooey.getMenuBar( frame );
					Gooey.getMenu( menubar, "menu.blah", Match.BY_NAME );
				}
			})
		);
		assertEquals( "Menu \"menu.blah\" not found (searched by name)", e.getMessage() );
	}
	@Test
	void testHasMenus() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				JFrameWithMenu.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				JMenuBar        menubar       = Gooey.getMenuBar( frame );
				
				JMenu           programByText = Gooey.getMenu( menubar,       "Program" );
				JMenuItem       aboutByText   = Gooey.getMenu( programByText, "About..." ); 
				JMenuItem       exitByText    = Gooey.getMenu( programByText, "Exit" ); 

				JMenu           programByName = Gooey.getMenu( menubar,       MENU_PROGRAM, Match.BY_NAME );
				JMenuItem       aboutByName   = Gooey.getMenu( programByName, MENU_ABOUT,   Match.BY_NAME ); 
				JMenuItem       exitByName    = Gooey.getMenu( programByName, MENU_EXIT,    Match.BY_NAME ); 

				assertTrue( programByText == programByName );
				assertTrue( aboutByText   == aboutByName );
				assertTrue( exitByText    == exitByName );
				
				List<JMenu>     menus   = Gooey.getMenus( menubar );
				assertEquals( 1, menus.size() );
				assertTrue  (    menus.contains( programByText ));

				List<JMenuItem> items = Gooey.getMenus( programByText );
				assertEquals( 2, items.size() );
				assertTrue  (    items.contains( aboutByText ));
				assertTrue  (    items.contains( exitByText ));
			}
		});
	}
	@Test
	void testExitQuits() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				JFrameWithMenu.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				JMenuBar        menubar = Gooey.getMenuBar( frame );
				JMenu           program = Gooey.getMenu   ( menubar, "Program" );
				JMenuItem       exit    = Gooey.getMenu   ( program, "Exit" ); 

				assertTrue  ( frame.isShowing(), "JFrame should be displayed" );
				exit.doClick();
				assertFalse ( frame.isShowing(), "JFrame should be hidden" );
			}
		});
	}
	@Test
	void testAboutHasDialog() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				JFrameWithMenu.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				JMenuBar        menubar = Gooey.getMenuBar( frame );
				JMenu           program = Gooey.getMenu   ( menubar, "Program" );
				JMenuItem       about   = Gooey.getMenu   ( program, "About..." ); 

				Gooey.capture( new GooeyDialog() {
					@Override
					public void invoke() {
						about.doClick();
					}
					@Override
					public void test(JDialog dialog) {
						Gooey.getLabel ( dialog, "Sample About Dialog" );
						Gooey.getButton( dialog, "OK" );
					}
				});
			}
		});
	}
}
