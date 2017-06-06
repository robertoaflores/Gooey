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


import javax.swing.JFrame;

public abstract class GooeyFrame extends GooeyWindow<JFrame> {
	static {
//		Debug.it("new JFrame");
		new JFrame().dispose();
	}
	public GooeyFrame() {
		this( "JFrame not detected" );
	}
	public GooeyFrame(String noWindowMessage) {
		super( JFrame.class, noWindowMessage );
	}
}
