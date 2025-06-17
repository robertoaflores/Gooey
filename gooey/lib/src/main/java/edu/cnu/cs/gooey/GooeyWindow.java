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


import java.awt.AWTEvent;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.function.Predicate;

import javax.swing.SwingUtilities;

public abstract class GooeyWindow <T extends Window> extends GooeyDisplayable<T> {
	private final GooeySwingToolkitListener<T> listener;

	protected GooeyWindow(final Class<T> type, String noWindowMessage) {
		super( noWindowMessage );
		Objects.requireNonNull( type );
		Predicate<AWTEvent> captureCriteria = e -> e.getID() == WindowEvent.WINDOW_OPENED &&
				                                   type.isInstance( e.getSource() ); 
		listener = new GooeySwingToolkitListener<>( captureCriteria );
	}
	@Override
	protected final void setEnableCapture(boolean on) {
		listener.setEnableCapture( on );
	}
	@Override
	protected void close(T window) {
		SwingUtilities.invokeLater(() -> window.setVisible( false ));
	}
	@Override
	protected T getTarget() {
		return listener.getTarget(); 
	}
}
