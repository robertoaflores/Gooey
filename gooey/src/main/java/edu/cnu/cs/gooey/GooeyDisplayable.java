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
				AtomicReference<RuntimeException> exception = new AtomicReference<>( null );
				capture.thenAccept(t->test(t)).exceptionally(e->{
					Throwable cause = e.getCause();
					if (cause instanceof RuntimeException) {
						exception.set( (RuntimeException) cause );
					}
					return null;
				}).join();
				capture.thenAccept(t ->close(t));
				RuntimeException hey = exception.get();  
				if (hey != null) {
					throw hey;
				}
			} else {
				throw new AssertionError( noWindowMessage );
			}
		} finally {
			setEnableCapture( false );
		}
/*
		Consumer<T> test    = a->test ( a );
		Consumer<T> close   = a->close( a );

		try {
			CompletableFuture<T>    capture = CompletableFuture.supplyAsync( ()->getTarget() ); 
			CompletableFuture<Void> invoke  = CompletableFuture.   runAsync( ()->new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					SwingUtilities.invokeAndWait( ()->invoke() );
					return null;
				}
			}).thenRun(()->{
				try {
					Thread.sleep( TIMEOUT );
				} catch (InterruptedException e) {
				}
			});
//			CompletableFuture<Void> a       = CompletableFuture.runAsync(invoke).thenRun(timeout); 
//			CompletableFuture<T>    b       = CompletableFuture.supplyAsync(capture); 

			CompletableFuture.anyOf( capture, invoke ).join();
			System.out.printf("invoke+timeout[%-5s] capture[%-5s]%n", invoke.isDone(), capture.isDone() );
			if (capture.isDone()) {
				invoke .join();
				capture.thenAccept(t->test.andThen(close)).join();
			} else {
				throw new AssertionError( noWindowMessage );
			}
		} finally {
			setEnableCapture( false );
		}
 */
/*
//		System.out.printf("%s,%d,%s%n",Thread.currentThread().getName(),System.currentTimeMillis(),"1.capture  [begin]");
		// enables capture criteria and begins listening
		setEnableCapture( true );

//		System.out.printf("%s,%d,%s%n",Thread.currentThread().getName(),System.currentTimeMillis(),"2.execute [begin]");
		// runs methods 
		// "invoke", which displays a window
		// "getTarget", which returns captured window
		ExecutorService      executor   = Executors.newCachedThreadPool();
		CompletionService<T> completion = new ExecutorCompletionService<>( executor );
//		Future<?>            invoke     = completion.submit( ()->{ invoke(); return null; } ); 
		Future<T>            capture    = completion.submit( ()->  getTarget() );
		Future<?>            timeout    = completion.submit( ()->{ Thread.sleep( TIMEOUT ); return null; } );
		Future<?>            invoke     = completion.submit( new SwingWorker<Void,Void>(){
			@Override
			protected Void doInBackground() throws Exception {
				SwingUtilities.invokeAndWait(()->invoke());
				return null;
			}
		}, null );
//		System.out.printf("%s,%d,%s%n",Thread.currentThread().getName(),System.currentTimeMillis(),"3.execute [end]");
		try {
			executor.shutdown();
//			System.out.printf("%s,%d,%s%n",Thread.currentThread().getName(),System.currentTimeMillis(),"4.invoke  [begin]");
			do {
				Future<?> done = completion.take();
//				System.out.printf("%s,%d,%s,%s%n",Thread.currentThread().getName(),System.currentTimeMillis(),"a.take",done==invoke?"invoke":done==timeout?"timeout":done==capture?"capture":"what?");
				if (invoke == done) {
					invoke.get(); // if "invoke" threw an exception then "get" throws an ExecutionException (caught below);  
                                  // otherwise "invoke" terminated normally and we ignore the value it returned.
				}
				if (timeout == done) {
					throw new AssertionError( noWindowMessage );
				}
			} while (!capture.isDone());
//			System.out.printf("%s,%d,%s%n",Thread.currentThread().getName(),System.currentTimeMillis(),"5.invoke  [end]");

			T captured = capture.get();
			try {
				test ( captured );
			} finally {
				close( captured );
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
//			System.out.printf("%s,%d,%s%n",Thread.currentThread().getName(),System.currentTimeMillis(),"8.capture [end]");
		}
*/
	}
}
