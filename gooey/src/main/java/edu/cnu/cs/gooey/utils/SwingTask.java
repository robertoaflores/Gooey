package edu.cnu.cs.gooey.utils;
import java.util.concurrent.*;

/**
 * BackgroundTask
 * <p/>
 * Background task class supporting cancellation, completion notification, and progress notification
 *
 * @author Brian Goetz and Tim Peierls
 */

public abstract class SwingTask<T> implements RunnableFuture<T> {
    private final FutureTask<T> computation = new Computation();

    private class Computation extends FutureTask<T> {
        public Computation() {
            super(new Callable<T>() {
                public T call() throws Exception {
                    return SwingTask.this.compute();
                }
            });
        }
        @Override
        protected final void done() {
        	Debug.it("done++++++");
            SwingExecutor.instance().execute(()->{
            	Debug.it("done.run++");
            	T         value     = null;
            	Throwable thrown    = null;
            	boolean   cancelled = false;
            	try {
            		value  = get();
            	} catch (ExecutionException e) {
            		thrown = e.getCause();
            	} catch (CancellationException e) {
            		cancelled = true;
            	} catch (InterruptedException consumed) {
            	} finally {
            		onCompletion(value, thrown, cancelled);
            	}
            });
        }
    }
    protected abstract T compute() throws Exception;
    protected void setProgress(int current, int max) {
        SwingExecutor.instance().execute(()->onProgress(current, max));
    }
    protected void onCompletion(T result, Throwable exception, boolean cancelled) {
    }
    protected void onProgress(int current, int max) {
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return computation.cancel(mayInterruptIfRunning);
    }
    @Override
    public T get() throws InterruptedException, ExecutionException {
        return computation.get();
    }
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return computation.get(timeout, unit);
    }
    @Override
    public boolean isCancelled() {
        return computation.isCancelled();
    }
    @Override
    public boolean isDone() {
        return computation.isDone();
    }
    @Override
    public void run() {
        computation.run();
    }
}
