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
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.function.Predicate;

/**
 * Listener receiving window events from the toolkit (refer to {@link java.awt.Toolkit} for 
 * details on the handling of GUI components). Listener is indirectly enabled by tests 
 * expecting that a window will be displayed.
 */
public class GooeySwingToolkitListener<T> implements GooeyToolkitListener<T>, AWTEventListener {
	private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

	private T                   target;
	private Predicate<AWTEvent> criteria;
	
	public GooeySwingToolkitListener(Predicate<AWTEvent> criteria) {
		this.target   = null;
		this.criteria = criteria;
	}
	@Override
	public void setEnableCapture(boolean on) {
		if (on) TOOLKIT.   addAWTEventListener( this, AWTEvent.WINDOW_EVENT_MASK );
		else    TOOLKIT.removeAWTEventListener( this );
	}
	public T getTarget() {
		while (target == null) {
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException e) {
					/*
					 * don't print stack trace, since "wait" gets interrupted 
					 * whenever a window doesn't display (which happens often).
					 */
//					e.printStackTrace();
				}
			}
		}
		return target;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void eventDispatched(AWTEvent event) {
		if (target == null) {
			synchronized(this) {
				if (criteria.test( event )) {
					target = (T)event.getSource();
					notifyAll();
				}
			}
		}
	}
}
