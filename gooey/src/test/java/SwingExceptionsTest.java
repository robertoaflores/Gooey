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
import java.awt.Window;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyFrame;
import edu.cnu.cs.gooey.GooeyWindow;

public class SwingExceptionsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();	

	@Test
	public void testCaptureGooeyWindowWithNullClass() {
		thrown.expect( IllegalArgumentException.class );
		thrown.expectMessage( "parameter cannot be null" );
		
		Gooey.capture( new GooeyWindow<Window>( null, "" ) {
			@Override
			public void invoke() {
			}
			@Override
			public void test(Window capturedWindow) {
			}
		});
	}
	
	@Test
	public void testCaptureGooeyWindowWithNullMessage() {
		thrown.expect( IllegalArgumentException.class );
		thrown.expectMessage( "parameter cannot be null" );
		
		Gooey.capture( new GooeyFrame( null ) {
			@Override
			public void invoke() {
			}
			@Override
			public void test(JFrame capturedWindow) {
			}
		});
	}

	@Test
	public void testInvokeThrowsException() {
		thrown.expect( AssertionError.class );
		thrown.expectMessage( "JFrame not detected" );
		
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					throw new RuntimeException("thrown in invoke()");
				}
				@Override
				public void test(JFrame window) {
				}
			});
	}
	@Test
	public void testTestThrowsException() {
		thrown.expect( RuntimeException.class );
		thrown.expectMessage( "thrown in test()" );
		
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
			});
	}

}
