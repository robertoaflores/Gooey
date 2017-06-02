
import static org.junit.Assert.*;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.Gooey.Match;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class SwingMenuTest {
	private static final String MENU_PROGRAM = "menu.program";
	private static final String MENU_ABOUT   = "menu.program.about";
	private static final String MENU_EXIT    = "menu.program.exit";

	@Rule
	public ExpectedException thrown = ExpectedException.none();	

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
	public void testDoesntHaveMenuByText() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage( "Menu \"Blah\" not found (searched by label)" );
		
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
		});
	}
	@Test
	public void testDoesntHaveMenuByName() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage( "Menu \"menu.blah\" not found (searched by name)" );
		
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
		});
	}
	@Test
	public void testHasMenus() {
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

				assertTrue( "", programByText == programByName );
				assertTrue( "", aboutByText   == aboutByName );
				assertTrue( "", exitByText    == exitByName );
				
				List<JMenu>     menus   = Gooey.getMenus( menubar );
				assertEquals( "Incorrect result", 1, menus.size() );
				assertTrue  ( "Incorrect result",    menus.contains( programByText ));

				List<JMenuItem> items = Gooey.getMenus( programByText );
				assertEquals( "Incorrect result", 2, items.size() );
				assertTrue  ( "Incorrect result",    items.contains( aboutByText ));
				assertTrue  ( "Incorrect result",    items.contains( exitByText ));
			}
		});
	}
	@Test
	public void testExitQuits() {
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

				assertTrue  ( "JFrame should be displayed", frame.isShowing() );
				exit.doClick();
				assertFalse ( "JFrame should be hidden",    frame.isShowing() );
			}
		});
	}
	@Test
	public void testAboutHasDialog() {
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
