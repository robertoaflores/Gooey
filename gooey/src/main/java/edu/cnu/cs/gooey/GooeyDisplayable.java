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

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class GooeyDisplayable <T> {
	private static final int TIMEOUT = 5000; // in milliseconds
	
	public    abstract void invoke();
	public    abstract void test (T capturedWindow);
	protected abstract void close(T capturedWindow);
	
	protected abstract void setEnableCapture(boolean on);
	protected abstract T    getTarget();

	private final String noWindowMessage;
	
	protected GooeyDisplayable(String noWindowMessage) {
		this.noWindowMessage = noWindowMessage;
	}

	/**
	 * This method is the capture , which calls 
	 * the method displaying a window, waits for the window to display (within a timeout period) and calls 
	 * the method testing this window. It calls abstract methods <code>invoke</code> 
	 * (calls the code to display a window) and <code>test</code> (calls the code to test the window). 
	 * If no window is detected within a waiting period the method throws an AssertionError.  
	 * @throws AssertionError if no window is displayed.
	 */
	public final synchronized void capture() {
		if (noWindowMessage == null) {
			throw new IllegalArgumentException( "parameter cannot be null" );
		}
		// enables capture criteria and begins listening
		setEnableCapture( true );

		// runs methods 
		// "invoke", which displays a window
		// "getTarget", which returns captured window
		ExecutorService      executor   = Executors.newCachedThreadPool();
		CompletionService<T> completion = new ExecutorCompletionService<>( executor );
		Future<?>            invoke     = completion.submit( ()->{ invoke(); return null; } ); 
		Future<T>            capture    = completion.submit( ()->  getTarget() );
		Future<?>            timeout    = completion.submit( ()->{ Thread.sleep( TIMEOUT ); return null; } );
		try {
			do {
				Future<?> done = completion.take();
				if (invoke == done) {
					invoke.get(); // if "invoke" threw an exception then "get" throws an ExecutionException (caught below);  
                                  // otherwise "invoke" terminated normally and we ignore the value it returned.
				}
				if (timeout == done) {
					throw new AssertionError( noWindowMessage );
				}
			} while (!capture.isDone());

			T capturedWindow = capture.get();
			try {
				test ( capturedWindow );
			} finally {
				close( capturedWindow );
			}
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			if (t instanceof RuntimeException) {
				throw (RuntimeException)t;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// disables capture criteria and stops listening
			setEnableCapture( false );
		}
	}
}
