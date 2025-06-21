package edu.cnu.cs.gooey;

/**
 * Listener interface used by Gooey to monitor window events.
 * Implementations are responsible for enabling or disabling
 * event capture and providing access to the captured window.
 *
 * @param <T> type of window being captured
 */
public interface GooeyToolkitListener<T> {
        /**
         * Enables or disables event capture for the implementing listener.
         *
         * @param on {@code true} to start capturing events, {@code false} to stop
         */
        void setEnableCapture(boolean on);

        /**
         * Returns the window instance that has been captured by the listener.
         * This call blocks until a window matching the criteria is available.
         *
         * @return captured window instance
         */
        T getTarget();
}
