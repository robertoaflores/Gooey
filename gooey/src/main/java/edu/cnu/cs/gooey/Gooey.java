package edu.cnu.cs.gooey;
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

/**
 * <p>Title: Gooey</p>
 * <p>Description: Gooey is a jUnit Swing test library of static methods 
 * for capturing windows and retrieving their components.<br>
 * Methods in this class are designed to be called in JUnit tests. There
 * are methods to capture displayed windows/dialogs and query their
 * components. Once a window is captured, methods can retrieve any component by type, by name,
 * and (in the case of buttons, menus, tabs and labels) by displayed text.  
 * </p>
 * @see GooeyDisplayable interface to access a captured window.
 * @author <a href="http://www.pcs.cnu.edu/~flores/">Roberto A. Flores</a>
 * @version 2016.8
 */
public class Gooey {
	/**
	 * Current build version.
	 */
	private static final double BUILD_VERSION = 1.801;

	/**
	 * Enumerated type indicating whether a component is sought by name or displayed text.
	 * When omitted in retrievals of labeled components (e.g., {@link #getLabel}, {@link #getButton}, 
	 * {@link #getMenu}) the default is by text. When omitted in retrieval of generic components 
	 * (e.g., {@link #getComponent} the default is by name. 
	 * @author robertoflores
	 * @since 1.8
	 */
	public enum Match {
		BY_NAME ( "by name" ),
		BY_LABEL( "by label" );
		
		private String string;
		
		private Match(String string) {
			this.string = string;
		}
		@Override
		public String toString() {
			return string;
		}
	}

	/**
	 * Private default (and only) constructor. No instance of Gooey can be created.
	 */
	private Gooey() {
	}
	
	/**
	 * Returns Gooey's current version number. 
	 * Intended to prevent that older versions are used in current projects.
	 * @return current build version number.
	 */
	public static final double getVersion() {
		return BUILD_VERSION;
	}
	/**
	 * Given a tab pane it returns the tab component with the given title.
	 * @param tabPane pane holding the tab.
	 * @param title title of the tab sought.
	 * @return component found.
	 * @throws AssertionError if no tab with the given title is found.
	 */
	public static Component getTab(JTabbedPane tabPane, String title) {
		return getTab( tabPane, title, Match.BY_LABEL );
	}
	/**
	 * Given a tab pane it returns the component associated with the given string.
	 * The string acts as an identifier for the tab depending on the flag used.
	 * The flag indicates whether we're seeking tabs by title or by name.   
	 * @param tabPane pane holding the tab.
	 * @param string identifier of the tab sought.
	 * @param flag to search by title (By_Text) or by name.
	 * @return component found.
	 * @throws AssertionError if no tab with the given string is found.
	 */
	public static Component getTab(JTabbedPane tabPane, String string, Match flag) {
		Supplier<String>    notFound = ()-> String.format( "Tab \"%s\" not found (searched %s)", string, flag );
		Optional<Component> result   = IntStream
    			.range   ( 0, tabPane.getTabCount() )
    			.filter  ( i->string.equals(flag == Match.BY_LABEL ? tabPane.getTitleAt(i) 
    					                                          : tabPane.getComponentAt(i).getName() ))
    			.mapToObj( i->tabPane.getComponentAt( i ))
    			.findAny ();
    	if (result.isPresent()) return                      result.get();
    	else                    throw new AssertionError( notFound.get() );
	}
	/**
	 * Given a container it returns the label displaying the given text. 
	 * @param container container holding the label.
	 * @param text text of the label sought.
	 * @return label found.
	 * @throws AssertionError if no label with the given text is found.
	 */
	public static JLabel getLabel(Container container, String text) {
		return getLabel( container, text, Match.BY_LABEL );
	}
	/**
	 * Given a container it returns the label associated with the given string.
	 * The string acts as an identifier for the label depending on the flag used.
	 * The flag indicates whether we're seeking labels by text or by name.   
	 * @param container container holding the label.
	 * @param string identifier of the label sought.
	 * @param flag to search by text or by name.
	 * @return label found.
	 * @throws AssertionError if no label with the given string is found.
	 */
	public static JLabel getLabel(Container container, final String string, final Match flag) {
		Supplier<String>     notFound = ()-> String.format( "Label \"%s\" not found (searched %s)", string, flag );
		return getComponent( notFound, container, JLabel.class, l -> string.equals(flag == Match.BY_LABEL ? l.getText() : l.getName()), retrieveComponents );
	}
	/**
	 * Given a container it returns the button displaying the given text.
	 * @param container container holding the button.
	 * @param text text of the button sought.
	 * @return button found.
	 * @throws AssertionError if no button with the given text is found.
	 */
	public static JButton getButton(Container container, String text) {
		return getButton( container, text, Match.BY_LABEL );
	}
	/**
	 * Given a container it returns the button associated with the given string.
	 * The string acts as an identifier for the button depending on the flag used.
	 * The flag indicates whether we're seeking buttons by text or by name.   
	 * @param container container holding the button.
	 * @param string identifier of the button sought.
	 * @param flag to search by text or by name.
	 * @return button found.
	 * @throws AssertionError if no button with the given string is found.
	 */
	public static JButton getButton(Container container, final String string, final Match flag) {
		Supplier<String>     notFound = ()-> String.format( "Button \"%s\" not found (searched %s)", string, flag );
		return getComponent( notFound, container, JButton.class, b -> string.equals(flag == Match.BY_LABEL ? b.getText() : b.getName()), retrieveComponents );
	}
	/**
	 * Returns the menu bar associated with a frame.
	 * 
	 * @param frame the frame whose menu is requested.
	 * @return the frame's menu bar.
	 * @throws AssertionError if no menu bar is found.
	 */
	public static JMenuBar getMenuBar(JFrame frame) {
		JMenuBar menubar = frame.getJMenuBar();
		if (menubar == null) {
			throw new AssertionError( "Menubar not found" );
		}
		return menubar;
	}
	/**
	 * Given a menu bar it returns the menu displaying the given text.
	 * @param menubar menu bar holding the menu.
	 * @param text text of the menu sought.
	 * @return menu found.
	 * @throws AssertionError if no menu with the given text is found.
	 */
	public static JMenu getMenu(JMenuBar menubar, String text) {
		return getMenu( menubar, text, Match.BY_LABEL );
	}
	/**
	 * Given a menu bar it returns the menu associated with the given string.
	 * The string acts as an identifier for the menu depending on the flag used.
	 * The flag indicates whether we're seeking menus by text or by name.   
	 * @param menubar menu bar holding the menu.
	 * @param string identifier of the menu sought.
	 * @param flag to search by text or by name.
	 * @return menu found.
	 * @throws AssertionError if no menu with the given string is found.
	 */
	public static JMenu getMenu(JMenuBar menubar, String string, Match flag) {
		return getMenu( menubar, JMenu.class, string, flag );
	}
	/**
	 * Given a menu it returns the sub-menu (i.e., another menu or a menu item) displaying the given text.
	 * This method searches into nested menus for matches and returns an object of type T sub-classing from JMenuItem.
	 * @param <T> class type sub-classing from JMenuItem. 
	 * @param menu menu holding the sub-menu.
	 * @param text text of the menu sought.
	 * @return sub-menu found.
	 * @throws AssertionError if no sub-menu with the given text is found.
	 */
	public static <T extends JMenuItem> T getMenu(JMenu menu, String text) {
		return getMenu( menu, text, Match.BY_LABEL );
	}
	/**
	 * Given a menu it returns the sub-menu (i.e., another menu or a menu item) associated with the given string.
	 * The string acts as an identifier for the sub-menu depending on the flag used.
	 * The flag indicates whether we're seeking sub-menus by text or by name.   
	 * @param <T> class type sub-classing from JMenuItem. 
	 * @param menu menu holding the sub-menu.
	 * @param string identifier of the sub-menu sought.
	 * @param flag to search by text or by name.
	 * @return sub-menu found.
	 * @throws AssertionError if no sub-menu with the given string is found.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends JMenuItem> T getMenu(JMenu menu, String string, Match flag) {
		return (T)getMenu( menu, JMenuItem.class, string, flag );
	}
	/**
	 * (Funnel method for public getMenu methods) Given a menu and a type it returns a sub-menu of the specified
	 * type that is associated with the given string. It a creates a criteria based on the given string and flag,
	 * and an error message in case that the menu is not found.
	 * @param menu menu holding the sub-menu.
	 * @param string identifier of the sub-menu sought. 
	 * @param flag to search by text or by name.
	 * @return sub-menu found.
	 * @throws AssertionError if no menu with the given text is found.
	 */
	private static <T extends JMenuItem> T getMenu(Container menu, final Class<T> type, final String string, Match flag) {
		Supplier<String>     notFound = ()-> String.format( "Menu \"%s\" not found (searched %s)", string, flag );
		return getComponent( notFound, menu, type, m -> string.equals(flag == Match.BY_LABEL ? m.getText() : m.getName()), retrieveMenus );
	}
	
	/**
	 * Returns a list with top-level menus in the menu bar. Nested menus are not traversed.  
	 * @param menubar menu bar to evaluate. 
	 * @return list with the menus in the menu bar.
	 */
	public static List<JMenu> getMenus(JMenuBar menubar) {
		return IntStream.range   ( 0, menubar.getMenuCount() )
				        .mapToObj( i->menubar.getMenu(i) )
				        .collect ( Collectors.toList() );
	}
	/**
	 * Returns a list with all sub-menus (i.e., another menu or a menu item) in the menu provided. Nested sub-menus are not traversed.
	 * @param menu menu to evaluate.
	 * @return list with the sub-menus in the menu.
	 */
	public static List<JMenuItem> getMenus(JMenu menu) {
		return Stream.of     ( menu.getMenuComponents()  )
				     .filter ( c->c instanceof JMenuItem )
				     .map    ( c->(JMenuItem) c )
				     .collect( Collectors.toList() );
	}

	/**
	 * Returns the first component of a class found in a container.
	 * @param <T> class type sub-classing from Component. 
	 * @param container container to evaluate.
	 * @param type class of component sought.
	 * @return component found.
	 * @throws AssertionError if no component of the given class is found.
	 */
	public static <T extends Component> T getComponent(Container container, Class<T> type) {
		Supplier<String>     notFound = ()-> String.format( "Component \"%s\" not found", type.getName() );
		return getComponent( notFound, container, type, t->true, retrieveComponents );
	}
	/**
	 * Returns a component found in the container that matches the given class type and associated name.
	 * @param <T> class type sub-classing from Component. 
	 * @param container container to evaluate.
	 * @param type class of component sought.
	 * @param name name of the component sought.
	 * @return component found.
	 * @throws AssertionError if no component with the given class and name is found.
	 */
	public static <T extends Component> T getComponent(Container container, final Class<T> type, final String name) {
		Supplier<String>     notFound = ()-> String.format( "No \"%s\" component \"%s\" found (searched %s)", type.getName(), name, Match.BY_NAME );
		return getComponent( notFound, container, type, t->name.equals( t.getName() ), retrieveComponents );
	}
	
	/**
	 * (Funnel method for public getComponent methods) Returns a component in the container that matches the given class type and criteria.
	 * A function is provided to traverse different tree structures (e.g., menus, containers).
	 * @param notFound creates the assertion message used when no component is found.
	 * @param container container to evaluate.
	 * @param type class type of components sought.
	 * @param criteria criteria indicating an accepting component.
	 * @param retrieveFrom functions building a tree given a component.
	 * @return component found.
	 * @throws AssertionError if no component with the given class and criteria is found.
	 */
	private static <T extends Component> T getComponent(Supplier<String> notFound, Container container, Class<T> type, Predicate<T> criteria, Function<Component,Tree> retrieveFrom) {
		Optional<T> result = getComponentStream( container, type, criteria, retrieveFrom ).findAny();
		if (result.isPresent()) return                      result.get();
		else                    throw new AssertionError( notFound.get() );
	}
	/**
	 * Returns a list with all components of a given class found in a container.
	 * @param <T> class type sub-classing from Component. 
	 * @param container container to evaluate.
	 * @param type class of components sought.
	 * @return list of components found.
	 */
	public static <T extends Component> List<T> getComponents(Container container, Class<T> type) {
		return getComponents( container, type, c->true );
	}
	/**
	 * Returns a list with all components of a given class found in a container that match the given criteria.
	 * @param <T> class type sub-classing from Component. 
	 * @param container container to evaluate.
	 * @param type class of components sought.
	 * @return list of components found.
	 */
	public static <T extends Component> List<T> getComponents(Container container, Class<T> type, Predicate<T> criteria) {
		return getComponents( container, type, criteria, retrieveComponents );
	}
	/**
	 * (Funnel method for the public getComponents method) Returns a list with all components in a container that match the given class type and criteria.
	 * A function is provided to traverse different tree structures (e.g., menus, containers).
	 * @param <T> class type sub-classing from Component. 
	 * @param container container to evaluate.
	 * @param type class type of components sought.
	 * @param criteria criteria indicating an accepting component.
	 * @param retrieveFrom functions building a tree given a component.
	 * @return list of components found.
	 */
	private static <T extends Component> List<T> getComponents(Container container, Class<T> type, Predicate<T> criteria, Function<Component,Tree> retrieveFrom ) {
		return getComponentStream( container, type, criteria, retrieveFrom ).collect( Collectors.toList() );
	}
	/**
	 * (Funnel method for the private getComponent and getComponents methods) Returns a stream with all components in a container that match the given class type and criteria.
	 * A function is provided to traverse different tree structures (e.g., menus, containers).
	 * @param <T> class type sub-classing from Component. 
	 * @param container container to evaluate.
	 * @param type class type of components sought.
	 * @param criteria criteria indicating an accepting component.
	 * @param retrieveFrom functions building a tree given a component.
	 * @return list of components found.
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Component> Stream<T> getComponentStream(Container container, Class<T> type, Predicate<T> criteria, Function<Component,Tree> retrieveFrom) {
		Tree   tree = retrieveFrom.apply( container );
		return tree.flattened()
                   .map      ( Tree::getComponent )
                   .filter   ( c->type.isInstance(c) )
                   .map      ( c->(T)c )
                   .filter   ( criteria );
	}
	/**
	 * This method uses a capture mechanism, which invokes 
	 * the method displaying a window, waits for the window to display (within a timeout period) and calls 
	 * the method to test this window. 
	 * The parameter displayable is an instance of GooeyDisplayable that implements methods: <code>invoke</code> 
	 * (calls the code to display a window) and <code>test</code> (calls the code to test the window). 
	 * If no window is detected within a waiting period the method throws an AssertionError.  
	 * @param <T> class type sub-classing from Component. 
	 * @param <U> class type sub-classing from GooeyDisplayable. 
	 * @param displayable interface to display and handle the test of a window.
	 * @throws AssertionError if no window is displayed.
	 * @see GooeyDisplayable
	 */
	public synchronized static <T extends GooeyDisplayable<U>, U extends Window> void capture(T displayable) {
		displayable.capture();
	}
	// fields used when building trees in getComponents()
	private static final Function<Component,Tree> retrieveComponents = new Function<Component,Tree>() {
		@Override
		public Tree apply(Component component) {
			Tree result = new Tree( component );
			if (component instanceof Container) {
				Container  container = (Container) component;
				Stream.of( container.getComponents() ).map( c->apply(c) ).forEach( t->result.add(t) );
			}
			return result;
		}
	};
	private static final Function<Component,Tree> retrieveMenus = new Function<Component,Tree>() {
		@Override
		public Tree apply(Component component) {
			Tree result   = new Tree( component );
			if (component instanceof Container) {
				Component[] children;
				if (component instanceof JMenu) {
					JMenu menu = (JMenu) component;
					children   = menu.getMenuComponents();
				} else {
					Container container = (Container)component;
					children            = container.getComponents();
				}
				Stream.of( children ).map( c->apply(c) ).forEach( t->result.add(t) );
			}
			return result;
		}
	};
	/**
	 * @author konrad.garus
	 * http://squirrel.pl/blog/2015/03/04/walking-recursive-data-structures-using-java-8-streams/
	 */
	private static class Tree {
	    private Component  self;
	    private List<Tree> children;
	 
	    public Tree(Component self) {
	        this.self     = self;
	        this.children = new LinkedList<>();
	    }
	    public Component getComponent() {
	        return self;
	    }
	    public void add(Tree child) {
	    	children.add( child );
	    }
	    public Stream<Tree> flattened() {
	        return Stream.concat(
	               Stream.of( this ),
	               children.stream().flatMap( Tree::flattened ));
	    }
	}
}
