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
	/*
		hack: for unknown reasons the Swing window/dialog created by the first 
		test takes a couple of seconds to display, while subsequent windows/dialogs
		take comparatively shorter. The method below helps circumvent this problem by 
		allocating a longer wait time to the first timeout in a capture.  
	 */
	private static boolean FIRST = true; 
	private static long getTimeout() {
		if (FIRST) {
			FIRST = false;
			return 3000L;
		}
		return 2000L;
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
	/*
	public final void capture() {
		Debug.it("capture+++");

		setEnableCapture( true );
		AtomicReference<Throwable> thrown     = new AtomicReference<>( null );
		SwingTask<Throwable>       invokeTask = new SwingTask<Throwable>() {
			@Override
			protected Throwable compute() throws Exception {
				Debug.it("invoke++++");
				invoke();
				Debug.it("invoke----");
				return thrown.get();
			}
			@Override
			protected void onCompletion(Throwable result, Throwable exception, boolean cancelled) {
				if (exception != null && thrown.get() == null) {
					Debug.it("invoke.exception: " + exception);
					thrown.set( exception );
				}
			}
		};
		SwingTask<T> captureTask = new SwingTask<T>() {
			@Override
			protected T compute() throws Exception {
				return getTarget();
			}
			@Override
			protected void onCompletion(T target, Throwable exception, boolean cancelled) {
				if (exception != null && thrown.get() == null) {
					Debug.it("capture.exception: " + exception);
					thrown.set( exception );
				}
			}
		};
		Runnable timeoutTask = ()->{
			try {
				Thread.sleep( getTimeout() );
			} catch (InterruptedException e) {
			}
		}; 
		Runnable gapRunnable = ()->{
			try {
				Thread.sleep( 200 );
			} catch (InterruptedException e) {
			}
		}; 
//		CompletableFuture<Void> invoke  = CompletableFuture.runAsync(  invokeTask );
//		CompletableFuture<Void> timeout = CompletableFuture.runAsync( timeoutTask );
//		CompletableFuture<Void> capture = CompletableFuture.runAsync( captureTask );
		Runnable        timeoutRunnable = timeoutTask;
		Supplier<T>     captureSupplier = ()->getTarget();
//		Runnable        invokeRunnable  = ()->{
//			try {
//				SwingUtilities.invokeAndWait( ()->invoke() );
//			} catch (InvocationTargetException | InterruptedException e) {
//				Debug.it("invoke.exception:"+e.getCause());
//				thrown.set( e.getCause() );
//			}
//		};
//		CompletableFuture<Void> invoke  = CompletableFuture.   runAsync(  invokeRunnable );
		Runnable                 invokeRunnable  = ()->invoke();
		Function<Throwable,Void> invokeCatcher   = t->{
			if (t != null && thrown.get() == null) {
				Debug.it("invoke.exception: " + t.getCause());
				thrown.set( t.getCause() );
			}
			return null;
		};
		CompletableFuture<Void> invoke  = CompletableFuture.   runAsync(  invokeRunnable, r->SwingUtilities.invokeLater(r)).exceptionally( invokeCatcher );
		CompletableFuture<Void> timeout = CompletableFuture.   runAsync( timeoutRunnable );
		CompletableFuture<T>    capture = CompletableFuture.supplyAsync( captureSupplier );
		try {
			CompletableFuture.anyOf( capture, timeout ).join();
			Debug.it(String.format( "invoke(%s) capture(%s) timeout(%s)", invoke.isDone(), capture.isDone(), timeout.isDone() ));
			if (thrown.get() == null) {
				if (capture.isDone()) {
					try {
						T           target        = capture.get();
						Runnable    testRunnable  = ()->test(target);
						Runnable    closeRunnable = ()->close(target);
//						Runnable    testWrapper;
//						if (invoke.isDone()) {
//							testWrapper = ()->{
//								try {
//									SwingUtilities.invokeAndWait( testRunnable );
//								} catch (InvocationTargetException | InterruptedException e) {
//									Debug.it("test.exception:"+e.getCause());
//									thrown.set( e.getCause() );
//								}
//							};
//						} else {
//							testWrapper = testRunnable;
//						}
//						Debug.it("test++++");
//						CompletableFuture<Void> test = CompletableFuture.runAsync( testWrapper ).thenRun( closeRunnable );
//						test.join();
//						Debug.it("test----");

//						Function<Throwable,Void> testCatcher   = t->{
//							if (t != null && thrown.get() == null) {
//								Debug.it("test.exception: " + t.getCause());
//								thrown.set( t.getCause() );
//							}
//							return null;
//						};
//						Debug.it("test++++");
//						CompletableFuture<Void> test = CompletableFuture.runAsync( testRunnable, r->SwingUtilities.invokeLater(r) ).exceptionally( testCatcher ).thenRun( closeRunnable );
//						test.join();
//						Debug.it("test----");

						Function<Throwable,Void> testCatcher   = t->{
						if (t != null && thrown.get() == null) {
							Debug.it("test.exception: " + t.getCause());
							thrown.set( t.getCause() );
						}
						return null;
					};
					Debug.it("test++++");
					CompletableFuture<Void> test = CompletableFuture.runAsync( testRunnable, r->SwingUtilities.invokeLater(r) ).exceptionally( testCatcher ).thenRun( gapRunnable ).thenRun( closeRunnable );
					Debug.it("test%%%%");
					test.join();
					Debug.it("test----");
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				} else {
					if (timeout.isDone() && thrown.get() == null) {
						thrown.set( new AssertionError( noWindowMessage ));
					}
				}
			}
//			if (capture.isDone()) {
//				try {
//					T           t       = capture.get();
//					SwingTask<Void> testTask = new SwingTask<Void>() {
//						@Override
//						protected Void compute() throws Exception {
//							test ( t );
//							close( t );
//							return null;
//						}
//						@Override
//						protected void onCompletion(Void target, Throwable exception, boolean cancelled) {
//							if (exception != null && thrown.get() == null) {
//								Debug.Me("test.exception: " + exception);
//								thrown.set( exception );
//							}
//						}
//					};
//					CompletableFuture.runAsync( testTask ).join();
//				} catch (InterruptedException | ExecutionException e) {
//					e.printStackTrace();
//				}
//			} else {
//				if (timeout.isDone() && thrown.get() == null) {
//					thrown.set( new AssertionError( noWindowMessage ));
//				}
//			}
			Throwable caught = thrown.get();  
			if (caught != null) {
				Debug.it("exception "+caught);
				if      (caught instanceof RuntimeException) throw (RuntimeException)  caught;
				else if (caught instanceof AssertionError)   throw (AssertionError)    caught;
				else                                         throw new AssertionError( caught );
			}
		} finally {
			setEnableCapture( false );
			Debug.it("capture---");
		}
		/*
		Thread thread = new Thread(()->{
		Debug.Me("capture+++");
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
		 			//   hack: this timeout gives Swing threads a chance to execute actions 
		 			//   invoked during testing (e.g., closing a frame using a menu).  
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
			Debug.Me("capture---");
//			Debug.Me = false;
		}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	 */
	/*
	public final synchronized void capture() {
		// enables capture criteria and begins listening
		setEnableCapture( true );

		AtomicReference<Throwable> thrown          = new AtomicReference<>( null );
		Runnable                   invokeRunnable  = ()->invoke(); 
		Function<Throwable,Void>   invokeCatcher   = t->{
			if (t != null && thrown.get() == null) {
				Debug.it("invoke.exception: " + t.getCause());
				thrown.set( t.getCause() );
			}
			return null;
		};
		Runnable                   timeoutRunnable = ()->{
			try {
				Thread.sleep( getTimeout() );
			} catch (InterruptedException e) {
			}
		};
		Supplier<T>                captureSupplier = ()->getTarget();
		CompletableFuture<Void>    invoke          = CompletableFuture.   runAsync(  invokeRunnable ).exceptionally( invokeCatcher );
		CompletableFuture<Void>    timeout         = CompletableFuture.   runAsync( timeoutRunnable );
		CompletableFuture<T>       capture         = CompletableFuture.supplyAsync( captureSupplier );
		CompletableFuture.anyOf( capture, timeout ).join();
		Debug.it(String.format( "invoke(%s) capture(%s) timeout(%s)", invoke.isDone(), capture.isDone(), timeout.isDone() ));
		try {
			if (thrown.get() == null) {
				if (capture.isDone()) {
					try {
						T target = capture.get();
						try {
							Debug.it("test----");
							test(target);
						} finally {
							close(target);
							Debug.it("test----");
						}
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				} else {
					if (timeout.isDone() && thrown.get() == null) {
						thrown.set( new AssertionError( noWindowMessage ));
					}
				}
			}
			Throwable caught = thrown.get();  
			if (caught != null) {
				Debug.it("exception "+caught);
				if      (caught instanceof RuntimeException) throw (RuntimeException)  caught;
				else if (caught instanceof AssertionError)   throw (AssertionError)    caught;
				else                                         throw new AssertionError( caught );
			}
		} finally {
			setEnableCapture( false );
			Debug.it("capture---");
		}
	}
	*/
	public final void capture() {
		// enables capture criteria and begins listening
		setEnableCapture( true );

		// runs methods 
		// "invoke", which displays a window
		// "getTarget", which returns captured window
		ExecutorService      executor   = Executors.newCachedThreadPool();
		CompletionService<T> completion = new ExecutorCompletionService<>( executor );
		Future<?>            invoke     = completion.submit( ()->{ invoke(); return null; } ); 
		Future<T>            capture    = completion.submit( ()->  getTarget() );
		Future<?>            timeout    = completion.submit( ()->{ Thread.sleep( getTimeout() ); return null; } );
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

			T captured = capture.get();
			try {
				test( captured );
			} finally {
				Thread.sleep( 200 );
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
			setEnableCapture( false );
		}
	}
}
