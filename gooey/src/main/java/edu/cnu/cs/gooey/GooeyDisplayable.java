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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

public abstract class GooeyDisplayable <T> {
	/*
		hack: for unknown reasons the Swing window/dialog created by the first 
		test takes a couple of seconds to display, while subsequent windows/dialogs
		take comparatively shorter. The method below helps circumvent this problem by 
		allocating a longer wait time to the first timeout in a capture.  
	 */
	private static boolean FIRST = true; 
	private static long getTimeout() {
		if (FIRST) {
//			FIRST = false;
			return 3000L;
		}
		return 1000L;
	}

	public    abstract void invoke();
	public    abstract void test (T window);
	protected abstract void close(T window);
	
	protected abstract void setEnableCapture(boolean on);
	protected abstract T    getTarget();

	private final String noWindowMessage;
	
	protected GooeyDisplayable(String noWindowMessage) {
		if (noWindowMessage == null) {
			throw new IllegalArgumentException( "parameter cannot be null" );
		}
		this.noWindowMessage = noWindowMessage;
	}

	/**
	 * This method calls the code displaying a window, waits for the window to display (within a timeout period) and calls 
	 * the method to test this window. It calls abstract methods <code>invoke</code> 
	 * (calls the code to display a window) and <code>test</code> (calls the code to test the window). 
	 * If no window is detected within a waiting period the method throws an AssertionError.  
	 * @throws AssertionError if no window is displayed.
	 */
	public final void capture() {
		setEnableCapture( true );
		try {
			SwingUtilities.invokeLater( ()->invoke() );
			CompletableFuture<Void> timeout = CompletableFuture.runAsync(()->{
				try {
					Thread.sleep( getTimeout() );
				} catch (InterruptedException e) {
				}
			}); 
			CompletableFuture<T>     capture = CompletableFuture.supplyAsync(()->getTarget()); 
			CompletableFuture.anyOf( capture, timeout ).join();
			if (capture.isDone()) {
				AtomicReference<Throwable> thrownInTest = new AtomicReference<>( null );
				capture.thenAccept(t->test(t)).exceptionally(e->{
					thrownInTest.set( e.getCause() );
					return null;
				}).thenRun(()->{
					/*
		 			   hack: this timeout gives Swing threads a chance to execute actions 
		 			   invoked during testing (e.g., closing a frame using a menu).  
					 */
					try {
						Thread.sleep( 100 );
					} catch (InterruptedException e) {
					}
				});
				capture.thenAccept(t ->close(t));
				Throwable hey = thrownInTest.get();  
				if (hey != null) {
					if      (hey instanceof RuntimeException) throw (RuntimeException) hey;
					else if (hey instanceof AssertionError)   throw (AssertionError)   hey;
					else                                      throw new AssertionError( hey );
				}
			} else {
				throw new AssertionError( noWindowMessage );
			}
		} finally {
			setEnableCapture( false );
//			Debug.Me = false;
		}
	}
}
