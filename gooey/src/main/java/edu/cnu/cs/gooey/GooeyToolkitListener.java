package edu.cnu.cs.gooey;

public interface GooeyToolkitListener<T> {
	void setEnableCapture(boolean on);
	T    getTarget();
}
