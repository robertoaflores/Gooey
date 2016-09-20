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
import java.util.function.Predicate;

public abstract class GooeyWindow <T extends Window> extends GooeyDisplayable<T> {
	private final GooeySwingToolkitListener<T> listener;

	protected GooeyWindow(final Class<T> type, String noWindowMessage) {
		super( noWindowMessage );
		if (type == null) {
			throw new IllegalArgumentException( "parameter cannot be null" );
		}
		Predicate<AWTEvent> captureCriteria = e -> type.isInstance( e.getSource() ) && e.getID() == WindowEvent.WINDOW_OPENED; 
		listener = new GooeySwingToolkitListener<>( captureCriteria );
	}
	@Override
	protected final void setEnableCapture(boolean on) {
		listener.setEnableCapture( on );
	}

	@Override
	protected void close(T window) {
		/* HACK
		   For reasons to be discovered, swing threads are stopped while this thread is running. This inhibits any changes in the state of the 
		   GUI program when manipulating its components in test(), e.g., updating labels with setText, processing JOptionPane dialog results 
		   (e.g., to close a parent window).
		   The sleep() call below allows room for swing threads to execute and carry on these changes.
		   100ms is a healthy compromise (guess): in this computer +/-35ms was the minimum for other threads to carry on. 
		*/
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//
		window.dispose();
	}

	@Override
	protected T getTarget() {
		return listener.getTarget(); 

	}
}
