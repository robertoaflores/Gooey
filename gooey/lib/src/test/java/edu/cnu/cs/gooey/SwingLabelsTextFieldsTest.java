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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.junit.jupiter.api.Test;

public class SwingLabelsTextFieldsTest {
	private static final String FIRST_INPUT  = "name.first .input";
	private static final String FIRST_LABEL  = "name.first .label";
	private static final String MIDDLE_INPUT = "name.middle.input";
	private static final String MIDDLE_LABEL = "name.middle.label";
	private static final String LAST_INPUT   = "name.last  .input";
	private static final String LAST_LABEL   = "name.last  .label";
	
	@SuppressWarnings("serial")
	private static class JFrameWithLabelsTextFields extends JFrame {
		public JFrameWithLabelsTextFields() {
			super("FlowLayout Example");
			
			setLayout( new FlowLayout());
			
			JTextField jtfFirst, jtfMiddle, jtfLast;
			JLabel     jlbFirst, jlbMiddle, jlbLast;
			
			jlbFirst = new JLabel( "First name" );
			jlbFirst.setName( FIRST_LABEL );
			add( jlbFirst );
			
			jtfFirst = new JTextField( 10 );
			jtfFirst.setName( FIRST_INPUT );
			add( jtfFirst );
			
			jlbMiddle = new JLabel( "MI" );
			jlbMiddle.setName( MIDDLE_LABEL );
			add( jlbMiddle );

			jtfMiddle = new JTextField( 1 );
			jtfMiddle.setName( MIDDLE_INPUT );
			add( jtfMiddle );
			
			jlbLast = new JLabel( "Last name" );
			jlbLast.setName( LAST_LABEL );
			add( jlbLast );
			
			jtfLast = new JTextField( 10 );
			jtfLast.setName( LAST_INPUT );
			add( jtfLast );
			
			pack();
		}
		public static void main(String[] args) {
			JFrame frame = new JFrameWithLabelsTextFields();
			frame.setVisible( true );
		}
	}
	@Test
	public void testHasLabels() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					JFrameWithLabelsTextFields.main( new String[]{} );
				}
				@Override
				public void test(JFrame frame) {
					JLabel fLabelByText = Gooey.getLabel( frame, "First name" );
					JLabel mLabelByText = Gooey.getLabel( frame, "MI" );
					JLabel lLabelByText = Gooey.getLabel( frame, "Last name" );
					
					JLabel fLabelByName = Gooey.getLabel( frame,  FIRST_LABEL, Gooey.Match.BY_NAME );
					JLabel mLabelByName = Gooey.getLabel( frame, MIDDLE_LABEL, Gooey.Match.BY_NAME );
					JLabel lLabelByName = Gooey.getLabel( frame,   LAST_LABEL, Gooey.Match.BY_NAME );
					
					assertTrue( fLabelByText == fLabelByName );
					assertTrue( mLabelByText == mLabelByName );
					assertTrue( lLabelByText == lLabelByName );
				}
			});
	}
	@Test
	public void testHasTextFields() {
		Gooey.capture(
			new GooeyFrame() {
				@Override
				public void invoke() {
					JFrameWithLabelsTextFields.main( new String[]{} );
				}
				@Override
				public void test(JFrame frame) {
					Gooey.getComponent( frame, JTextField.class,  FIRST_INPUT );
					Gooey.getComponent( frame, JTextField.class, MIDDLE_INPUT );
					Gooey.getComponent( frame, JTextField.class,   LAST_INPUT );					
				}
			});
	}
}
