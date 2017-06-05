
/**
 * <p>Copyright: Copyright (c) 2013, JoSE Group, Christopher Newport University. 
 * Permission to use, copy, modify, distribute and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation.  
 * The JoSE Group makes no representations about the suitability
 * of  this software for any purpose. It is provided "as is" without express
 * or implied warranty.</p>
 * <p>Company: JoSE Group, Christopher Newport University</p>
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.Gooey.Match;
import edu.cnu.cs.gooey.GooeyFrame;

public class SwingTabbedPaneTest {
	private static final String TAB_1 = "#tab.1";
	private static final String TAB_2 = "#tab.2";
	private static final String TAB_3 = "#tab.3";
	private static final String TAB_4 = "#tab.4";
	
	@SuppressWarnings("serial")
	private static class PanelWithTabs extends JPanel {
		public PanelWithTabs() {
			super(new GridLayout(1, 1));

			JTabbedPane tabbedPane = new JTabbedPane();
			// ImageIcon icon = createImageIcon("images/middle.gif");
			ImageIcon icon = null;

			JComponent panel1 = makeTextPanel( "Panel #1", TAB_1 );
			tabbedPane.addTab("Tab 1", icon, panel1, "Does nothing");
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

			JComponent panel2 = makeTextPanel("Panel #2", TAB_2 );
			tabbedPane.addTab("Tab 2", icon, panel2, "Does twice as much nothing");
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

			JComponent panel3 = makeTextPanel("Panel #3", TAB_3 );
			tabbedPane.addTab("Tab 3", icon, panel3, "Still does nothing");
			tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

			JComponent panel4 = makeTextPanel("Panel #4 (has a preferred size of 410 x 50).", TAB_4 );
			panel4.setPreferredSize(new Dimension(410, 50));
			tabbedPane.addTab("Tab 4", icon, panel4, "Does nothing at all");
			tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

			// Add the tabbed pane to this panel.
			add(tabbedPane);

			// The following line enables to use scrolling tabs.
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}
		protected JComponent makeTextPanel(String text, String name) {
			JPanel panel  = new JPanel( false );
			JLabel filler = new JLabel( text  );
			filler.setHorizontalAlignment( JLabel.CENTER );
			panel.setLayout(new GridLayout(1, 1));
			panel.add(filler);
			panel.setName(name);
			return panel;
		}
		public static void main(String[] args) {
			JFrame frame = new JFrame("TabbedPaneDemo");
			frame.add(new PanelWithTabs(), BorderLayout.CENTER);
			frame.pack();
			frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();	

//	@Test
	public void testHasTabs() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				PanelWithTabs.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				JTabbedPane tabPane = Gooey.getComponent( frame, JTabbedPane.class );
				assertEquals ( "Incorrect result", 4, tabPane.getTabCount() );
				
				Component tab1ByText = Gooey.getTab( tabPane, "Tab 1" );
				Component tab2ByText = Gooey.getTab( tabPane, "Tab 2" );
				Component tab3ByText = Gooey.getTab( tabPane, "Tab 3" );
				Component tab4ByText = Gooey.getTab( tabPane, "Tab 4" );
				
				Component tab1ByName = Gooey.getTab( tabPane, TAB_1, Match.BY_NAME );
				Component tab2ByName = Gooey.getTab( tabPane, TAB_2, Match.BY_NAME );
				Component tab3ByName = Gooey.getTab( tabPane, TAB_3, Match.BY_NAME );
				Component tab4ByName = Gooey.getTab( tabPane, TAB_4, Match.BY_NAME );

				assertTrue( "", tab1ByText == tab1ByName );
				assertTrue( "", tab2ByText == tab2ByName );
				assertTrue( "", tab3ByText == tab3ByName );
				assertTrue( "", tab4ByText == tab4ByName );
				
				List<JPanel> panels = Gooey.getComponents( tabPane, JPanel.class );
				assertEquals( "Incorrect result", 4, panels.size());

				assertEquals  ( "Incorrect result", "Does nothing",               tabPane.getToolTipTextAt(0) );
				assertEquals  ( "Incorrect result", "Does twice as much nothing", tabPane.getToolTipTextAt(1) );
				assertEquals  ( "Incorrect result", "Still does nothing",         tabPane.getToolTipTextAt(2) );
				assertEquals  ( "Incorrect result", "Does nothing at all",        tabPane.getToolTipTextAt(3) );

				Gooey.getLabel( panels.get(0), "Panel #1" );
				Gooey.getLabel( panels.get(1), "Panel #2" );
				Gooey.getLabel( panels.get(2), "Panel #3" );
				Gooey.getLabel( panels.get(3), "Panel #4 (has a preferred size of 410 x 50)." );
			}
		});
	}
//	@Test
	public void testDoesntHaveTabByText() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage("Tab \"Tab 9\" not found (searched by label)");
		
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				PanelWithTabs.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				JTabbedPane   tabPane = Gooey.getComponent( frame, JTabbedPane.class );				
				Gooey.getTab( tabPane, "Tab 9" );
			}
		});
	}
//	@Test
	public void testDoesntHaveTabByName() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage("Tab \"my.tab\" not found (searched by name)");

//		Debug.Me("++++++++++");
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
//				Debug.Me("invoke++++");
				PanelWithTabs.main( new String[]{} );
//				Debug.Me("invoke----");
			}
			@Override
			public void test(JFrame frame) {
//				Debug.Me("test++++++");
				JTabbedPane   tabPane = Gooey.getComponent( frame, JTabbedPane.class );				
				Gooey.getTab( tabPane, "my.tab", Match.BY_NAME );
//				Debug.Me("test------");
			}
		});
//		Debug.Me("----------");
	}
//	@Test
	public void testHasPanels() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				PanelWithTabs.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				JTabbedPane  tabPane = Gooey.getComponent ( frame, JTabbedPane.class );				
				List<JPanel> panels  = Gooey.getComponents( tabPane, JPanel.class );
				assertEquals( "Incorrect result", 4, panels.size());

				Gooey.getLabel( panels.get(0), "Panel #1" );
				Gooey.getLabel( panels.get(1), "Panel #2" );
				Gooey.getLabel( panels.get(2), "Panel #3" );
				Gooey.getLabel( panels.get(3), "Panel #4 (has a preferred size of 410 x 50)." );
			}
		});
	}

//	@Test
	public void testHasToolTips() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				PanelWithTabs.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				JTabbedPane tabPane = Gooey.getComponent( frame, JTabbedPane.class );
				
				assertEquals  ( "Incorrect result", "Does nothing",               tabPane.getToolTipTextAt(0) );
				assertEquals  ( "Incorrect result", "Does twice as much nothing", tabPane.getToolTipTextAt(1) );
				assertEquals  ( "Incorrect result", "Still does nothing",         tabPane.getToolTipTextAt(2) );
				assertEquals  ( "Incorrect result", "Does nothing at all",        tabPane.getToolTipTextAt(3) );
			}
		});
	}
}
