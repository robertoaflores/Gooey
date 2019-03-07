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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Window;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyFrame;
import edu.cnu.cs.gooey.GooeyWindow;

public class SwingExceptionsTest {

	@Test
	public void testCaptureGooeyWindowWithNullClass() {
		Throwable e = assertThrows( IllegalArgumentException.class, () ->
			Gooey.capture( new GooeyWindow<Window>( null, "" ) {
				@Override
				public void invoke() {
				}
				@Override
				public void test(Window capturedWindow) {
				}
			})
		);
		assertEquals( "parameter cannot be null", e.getMessage());		
	}
	
	@Test
	public void testCaptureGooeyWindowWithNullMessage() {
		Throwable e = assertThrows( IllegalArgumentException.class, () ->
			Gooey.capture( new GooeyFrame( null ) {
				@Override
				public void invoke() {
				}
				@Override
				public void test(JFrame capturedWindow) {
				}
			})
		);
		assertEquals( "parameter cannot be null", e.getMessage());		
	}

	@Test
	public void testInvokeThrowsException() {
		Throwable e = assertThrows( RuntimeException.class, () ->
			Gooey.capture( new GooeyFrame() {
				@Override
				public void invoke() {
					throw new RuntimeException("thrown in invoke()");
				}
				@Override
				public void test(JFrame window) {
				}
			})
		);
		assertEquals( "thrown in invoke()", e.getMessage());		
	}
	@Test
	public void testTestThrowsException() {
		Throwable e = assertThrows( RuntimeException.class, () ->
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					new JFrame().setVisible( true );
				}
				@Override
				public void test(JFrame window) {
					throw new RuntimeException( "thrown in test()" );
				}
			})
		);
		assertEquals( "thrown in test()", e.getMessage());		
	}
}
