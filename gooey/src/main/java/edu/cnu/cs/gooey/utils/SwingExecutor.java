package edu.cnu.cs.gooey.utils;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * GuiExecutor
 * <p/>
 * Executor built atop SwingUtilities
 *
 * @author Brian Goetz and Tim Peierls
 */
public class SwingExecutor extends AbstractExecutorService {
    private static          final SwingExecutor            instance = new SwingExecutor();
	private static          final ExecutorService          executor = Executors.newSingleThreadExecutor(new SwingThreadFactory());
	private static volatile       Thread                   my;

	private static class SwingThreadFactory implements ThreadFactory {
	    @Override
		public Thread newThread(Runnable r) {
	    	my = new Thread(r);
	    	Debug.it("new thread:"+my.getName());
			return my;
		}
	}
    private SwingExecutor() { }

    public static SwingExecutor instance() {
        return instance;
    }

	private boolean isMyThread() {
		return Thread.currentThread() == my;
	}

    @Override
    public void execute(Runnable r) {
        if (isMyThread()) {
	    	Debug.it("running my thread");
        	r.run();
        }
        else {
	    	Debug.it("running in executor");
        	executor.execute( r );
        }
    }
    @Override
    public void shutdown() {
		if (my != null) {
			my.interrupt();
		}
		executor.shutdown();
    }
    @Override
    public List<Runnable> shutdownNow() {
    	return executor.shutdownNow();
    }
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    	return executor.awaitTermination(timeout, unit);
    }
    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }
    @Override
    public boolean isTerminated() {
        return executor.isTerminated();
    }
}
