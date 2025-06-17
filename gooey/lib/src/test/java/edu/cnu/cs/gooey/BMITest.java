package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BMITest {
	@BeforeAll
	static void preLoad() {
		JFrame f = new BMI();
		f.setVisible( true );
		f.dispose();
	}
	@Test
	void testHasPrivateNonStaticFields() {
		Class<?> bmi    = BMI.class;
		Field[]  fields = bmi.getDeclaredFields();
		
		for (Field f : fields) {
			if (!f.isSynthetic()) {
				assertTrue ( Modifier.isPrivate( f.getModifiers() ), "Field \""+f.getName()+"\" should be private" );
				assertFalse( Modifier.isStatic ( f.getModifiers() ), "Field \""+f.getName()+"\" can't be static" );
				assertFalse( f.getType().getName().equals( "javax.swing.JFrame" ), "Field \""+f.getName()+"\" can't be of type JFrame" );
			}
		}
	}
	@Test
	void testFrameSubclassesFromJFrame() {
		Class<?> bmi    = BMI.class;
		Class<?> parent = bmi.getSuperclass();
		assertEquals( "javax.swing.JFrame", parent.getName() );
	}
	@Test
	void testSuperclassAndFields() {
		Class<?> bmi    = BMI.class;
		Field[]  fields = bmi.getDeclaredFields();

		Class<?> iParent = bmi.getSuperclass();
		assertEquals( "javax.swing.JFrame", iParent.getName(), "BMI is not a subclass of JFrame" );
		
		for (Field f : fields) {
			if (!f.isSynthetic()) {
				assertTrue ( Modifier.isPrivate( f.getModifiers() ), "Field \""+f.getName()+"\" should be private" );
				assertFalse( Modifier.isStatic ( f.getModifiers() ), "Field \""+f.getName()+"\" can't be static" );
				assertFalse( f.getType().getName().equals( "javax.swing.JFrame" ), "Field \""+f.getName()+"\" can't be of type JFrame" );
			}
		}
	}
	@Test
	void testFrameHasTitle() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				assertEquals( "Body Mass Index", frame.getTitle() );
			}
		});
	}
	@Test
	void testFrameWasPacked() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				Dimension actual   = frame.getSize();
				frame.pack();
				Dimension expected = frame.getSize();
				assertEquals( expected, actual, "Frame was not packed" );
			}
		});
	}
	@Test
	void testFrameDisposesOnClose() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				int closeOp = frame.getDefaultCloseOperation();
				assertTrue( closeOp == JFrame.DISPOSE_ON_CLOSE, "Frame should dispose on close" );
			}
		});
	}
	@Test
	void testHasGridLayout() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				LayoutManager lm = frame.getContentPane().getLayout();
				assertEquals( GridLayout.class, lm.getClass(), "Frame doesn't have a GridLayout" ); 				
			}
		});
	}
	@Test
	void testHasComponents() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				List<JComponent> components = Gooey.getComponents( frame, JComponent.class, c->c instanceof JLabel || c instanceof JTextField || c instanceof JButton );
				// check components exist
				assertTrue( components.size() > 0, "No components found" );
				// check components exist in order
				for (int i = 0; i < components.size(); i++) {
					JComponent actual   = components.get( i );
					Class<?>   expected = null;
					switch (i) {
					case 0 : 
					case 2 : 
					case 5 : 
					case 6 : expected = JLabel    .class; break;
					case 1 :
					case 3 : expected = JTextField.class; break;
					case 4 : expected = JButton   .class; break;
					default: fail( String.format("There are more components found (%d) than expected (%d)", components.size(), 3 ));
					}
					assertEquals( expected, actual.getClass(), "Incorrect component @ index "+i ); 
				}
			}
		});
	}
	@Test
	void testHasLabels() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				List<JComponent> components = Gooey.getComponents( frame, JComponent.class, c->c instanceof JLabel || c instanceof JTextField || c instanceof JButton );
				// check components have initial values
				String[] expected = { "Weight (lb)", "", "Height (ft)", "", "Go", "Index:", "" };
				for (int i = 0; i < components.size(); i++) {
					JComponent c      = components.get( i );
					String     actual = "";
					switch (i) {
					case 0 : 
					case 2 : 
					case 5 : 
					case 6 : actual = ((JLabel)    c).getText(); break;
					case 1 :
					case 3 : actual = ((JTextField)c).getText();	break;
					case 4 : actual = ((JButton)   c).getText(); break;
					default: fail( String.format("There are more components found (%d) than expected (%d)", components.size(), 3 ));
					}
					String message = String.format( "Component %d (%s) text is incorrect", i, c.getClass() );
					assertEquals( expected[i], actual, message );
				}
			}
		});
	}
	@Test
	void testCorrectInput() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				// check textfields and button exist
				List<JTextField> textfields = Gooey.getComponents( frame, JTextField.class );
				assertEquals( 2, textfields.size(), "There should be 2 JTextField: one for weight, one for height" );
				List<JButton>    buttons    = Gooey.getComponents( frame, JButton.class );
				assertEquals( 1, buttons.size(), "There should be 1 JButton labeled \"Go\"" );
				List<JLabel>     labels     = Gooey.getComponents( frame, JLabel.class );
				assertEquals( 4, labels.size(), "There should be 4 JLabels: one for weight, one for height, one for index, one for result" );
				// input data
				JTextField weight = textfields.get( 0 );
				JTextField height = textfields.get( 1 );
				JButton    go     = buttons   .get( 0 );
				JLabel     result = labels    .get( 3 );
				weight.setText( "170" );
				height.setText( "5.6" );
				go    .doClick();
				// check result
				String actual   = result.getText();
				String expected = "26.45";
				assertEquals( expected, actual );
			}
		});
	}
	@Test
	void testNegativeHeightWeightOrBoth() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				// check textfields and button exist
				List<JTextField> textfields = Gooey.getComponents( frame, JTextField.class );
				assertEquals( 2, textfields.size(), "There should be 2 JTextField: one for weight, one for height" );
				List<JButton>    buttons    = Gooey.getComponents( frame, JButton.class );
				assertEquals( 1, buttons.size(), "There should be 1 JButton labeled \"Go\"" );
				List<JLabel>     labels     = Gooey.getComponents( frame, JLabel.class );
				assertEquals( 4, labels.size(), "There should be 4 JLabels: one for weight, one for height, one for index, one for result" );
				// input data
				JTextField weight = textfields.get( 0 );
				JTextField height = textfields.get( 1 );
				JButton    go     = buttons   .get( 0 );
				JLabel     result = labels    .get( 3 );
				weight.setText( "170" );
				height.setText( "-1" );
				go    .doClick();
				// check result
				String actual   = result.getText();
				String expected = "Enter positive numbers";
				assertEquals( expected, actual );
			}
		});
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				// check textfields and button exist
				List<JTextField> textfields = Gooey.getComponents( frame, JTextField.class );
				assertEquals( 2, textfields.size(), "There should be 2 JTextField: one for weight, one for height" );
				List<JButton>    buttons    = Gooey.getComponents( frame, JButton.class );
				assertEquals( 1, buttons.size(), "There should be 1 JButton labeled \"Go\"" );
				List<JLabel>     labels     = Gooey.getComponents( frame, JLabel.class );
				assertEquals( 4, labels.size(), "There should be 4 JLabels: one for weight, one for height, one for index, one for result" );
				// input data
				JTextField weight = textfields.get( 0 );
				JTextField height = textfields.get( 1 );
				JButton    go     = buttons   .get( 0 );
				JLabel     result = labels    .get( 3 );
				weight.setText( "-1" );
				height.setText( "5.6" );
				go    .doClick();
				// check result
				String actual   = result.getText();
				String expected = "Enter positive numbers";
				assertEquals( expected, actual );
			}
		});
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				// check textfields and button exist
				List<JTextField> textfields = Gooey.getComponents( frame, JTextField.class );
				assertEquals( 2, textfields.size(), "There should be 2 JTextField: one for weight, one for height" );
				List<JButton>    buttons    = Gooey.getComponents( frame, JButton.class );
				assertEquals( 1, buttons.size(), "There should be 1 JButton labeled \"Go\"" );
				List<JLabel>     labels     = Gooey.getComponents( frame, JLabel.class );
				assertEquals( 4, labels.size(), "There should be 4 JLabels: one for weight, one for height, one for index, one for result" );
				// input data
				JTextField weight = textfields.get( 0 );
				JTextField height = textfields.get( 1 );
				JButton    go     = buttons   .get( 0 );
				JLabel     result = labels    .get( 3 );
				weight.setText( "-1" );
				height.setText( "-1" );
				go    .doClick();
				// check result
				String actual   = result.getText();
				String expected = "Enter positive numbers";
				assertEquals( expected, actual );
			}
		});
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				BMI.main( new String[]{} );
			}
			@Override
			public void test(JFrame frame) {
				// check textfields and button exist
				List<JTextField> textfields = Gooey.getComponents( frame, JTextField.class );
				assertEquals( 2, textfields.size(), "There should be 2 JTextField: one for weight, one for height" );
				List<JButton>    buttons    = Gooey.getComponents( frame, JButton.class );
				assertEquals( 1, buttons.size(), "There should be 1 JButton labeled \"Go\"" );
				List<JLabel>     labels     = Gooey.getComponents( frame, JLabel.class );
				assertEquals( 4, labels.size(), "There should be 4 JLabels: one for weight, one for height, one for index, one for result" );
				// input data
				JTextField weight = textfields.get( 0 );
				JTextField height = textfields.get( 1 );
				JButton    go     = buttons   .get( 0 );
				JLabel     result = labels    .get( 3 );
				weight.setText( "170" );
				height.setText( "abc" );
				go    .doClick();
				// check result
				String actual   = result.getText();
				String expected = "Enter positive numbers";
				assertEquals( expected, actual );
			}
		});
	}
}
