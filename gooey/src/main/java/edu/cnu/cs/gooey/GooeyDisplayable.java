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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public abstract class GooeyDisplayable <T> {
	private static final int TIMEOUT  = 2000; // in milliseconds

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
	 * This method is the capture , which calls 
	 * the method displaying a window, waits for the window to display (within a timeout period) and calls 
	 * the method testing this window. It calls abstract methods <code>invoke</code> 
	 * (calls the code to display a window) and <code>test</code> (calls the code to test the window). 
	 * If no window is detected within a waiting period the method throws an AssertionError.  
	 * @throws InvocationTargetException 
	 * @throws AssertionError if no window is displayed.
	 */
	public final void capture() {
		// enables capture criteria and begins listening
		setEnableCapture( true );

//		SwingWorker<Void,Void> invoke  = new SwingWorker<Void,Void>(){
//			@Override
//			protected Void doInBackground() throws Exception {
//				SwingUtilities.invokeAndWait( ()->invoke() );
//				return null;
//			}
//		};
//		Runnable    timeout = ()->{
//			try {
//				Thread.sleep( TIMEOUT );
//			} catch (InterruptedException e) {
//			}
//		};
//		Supplier<T> capture = ()->getTarget();
//		Consumer<T> test    = t->test ( t );
//		Consumer<T> close   = t->close( t );

		try {
			CompletableFuture.runAsync(new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					SwingUtilities.invokeAndWait( ()->invoke() );
					return null;
				}
			}); 
			CompletableFuture<Void> timeout = CompletableFuture.runAsync(()->{
				try {
					Thread.sleep( TIMEOUT );
				} catch (InterruptedException e) {
				}
			}); 
			CompletableFuture<T>    capture = CompletableFuture.supplyAsync(()->getTarget()); 
			CompletableFuture.anyOf( capture, timeout ).join();
//			System.out.printf("invoke[%-5s]+timeout[%-5s] capture[%-5s]%n", invoke.isDone(), timeout.isDone(), capture.isDone() );
			if (capture.isDone()) {
				AtomicReference<Throwable> thrownInTest = new AtomicReference<>( null );
				capture.thenAccept(t->test(t)).exceptionally(e->{
					thrownInTest.set( e.getCause() );
					return null;
				}).join();
				capture.thenAccept(t ->close(t));
				Throwable hey = thrownInTest.get();  
				if (hey != null) {
					if      (hey instanceof RuntimeException) throw (RuntimeException) hey;
					else if (hey instanceof AssertionError)   throw (AssertionError)   hey;
					else { 
//						System.out.println("What do I do with: "+hey);
						throw new AssertionError( hey );
					}
				}
			} else {
				throw new AssertionError( noWindowMessage );
			}
		} finally {
			setEnableCapture( false );
		}
	}
}
