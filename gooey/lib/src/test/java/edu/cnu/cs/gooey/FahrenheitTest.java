package edu.cnu.cs.gooey;

import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Dimension;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyFrame;

public class FahrenheitTest {
	private static final Class<?> CLASS = Fahrenheit.class;

	@BeforeAll
	public static void preLoad() {
		JFrame f = new Fahrenheit();
		f.setVisible( true );
		f.dispose();
	}

	@Test
	void testNonFunctional_NoStaticNoNonPrivateFieldsAllowed() {
		Consumer<Class<?>> 
		hasPrivateFields = a -> Arrays.stream( a.getDeclaredFields() ).filter( f->!f.isSynthetic() ).forEach( f->{
			var mods = f.getModifiers();
			var name = f.getName();
			Truth.assertWithMessage( String.format("field '%s' is not private", name )).that( Modifier.isPrivate( mods )).isTrue();
		});
		Consumer<Class<?>> 
		hasNoStaticFields = a -> Arrays.stream( a.getDeclaredFields() ).filter( f->!f.isSynthetic() ).forEach( f->{
			var mods = f.getModifiers();
			var name = f.getName();
			Truth.assertWithMessage( String.format("field '%s' is static",      name )).that( Modifier.isStatic ( mods )).isFalse();
		});
		Consumer<Class<?>> 
		hasNoJFrameField = a -> Arrays.stream( a.getDeclaredFields() ).filter( f->!f.isSynthetic() ).forEach( f->
			Truth.assertWithMessage( String.format("field '%s' cannot be of type JFrame", f.getName() ))
			     .that( f ).isNotInstanceOf( JFrame.class )
		);
		hasPrivateFields .accept( CLASS );
		hasNoStaticFields.accept( CLASS );
		hasNoJFrameField .accept( CLASS );
	}
	@Test
	void testFrameHasTitle() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				Fahrenheit.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				Truth.assertThat( frame.getTitle() )
				     .ignoringCase()
				     .isEqualTo( "Fahrenheit Conversion" );
			}
		});
	}
	@Test
	void testFrameWasPacked() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				Fahrenheit.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				Dimension actual   = frame.getSize();
				frame.pack();
				Dimension expected = frame.getSize();
				Truth.assertWithMessage( "Frame is not packed" )
				     .that( actual )
				     .isEqualTo( expected );
			}
		});
	}
	@Test
	void testFrameDisposesOnClose() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				Fahrenheit.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				Truth.assertWithMessage( "JFrame should get disposed on close" )
				     .that( frame.getDefaultCloseOperation() )
				     .isEqualTo( JFrame.DISPOSE_ON_CLOSE );
			}
		});
	}
	@Test
	void testHasComponents() {
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				Fahrenheit.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				List<JComponent> components = Gooey.getComponents( frame, JComponent.class, c->c instanceof JLabel || c instanceof JTextField || c instanceof JButton  || c instanceof JRadioButton );
				// check components exist
				Truth.assertThat( components )
				     .isNotEmpty();
				// check components exist in order
				for (int i = 0; i < components.size(); i++) {
					JComponent actual   = components.get( i );
					Class<?>   expected = null;
					switch (i) {
					case 0 : 
					case 5 : 
					case 6 : expected = JLabel      .class; break;
					case 1 : expected = JTextField  .class; break;
					case 2 : expected = JButton     .class; break;
					case 3 :
					case 4 : expected = JRadioButton.class; break;
					default: fail( String.format("There are more components found (%d) than expected (%d)", components.size(), 3 ));
					}
					Truth.assertWithMessage( String.format( "expected %s @ index %d but found %s", expected.getSimpleName(), i, actual.getClass().getSimpleName() ))
						 .that( actual )
						 .isInstanceOf( expected );
				}
			}
		});
	}
	@Test
	void testCelsius() {
		testHasComponents();
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				Fahrenheit.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				List<JTextField>   textfields = Gooey.getComponents( frame, JTextField.class );
				List<JButton>      buttons    = Gooey.getComponents( frame, JButton.class );
				List<JLabel>       labels     = Gooey.getComponents( frame, JLabel.class );
				List<JRadioButton> radios     = Gooey.getComponents( frame, JRadioButton.class );
				// input data
				JTextField   fahrenheit = textfields.get( 0 );
				JButton      go         = buttons   .get( 0 );
				JLabel       result     = labels    .get( 2 );
				JRadioButton celsius    = radios    .get( 0 );
				celsius.setSelected( true );
				fahrenheit.setText( "87" );
				go        .doClick();
				// check result
				Truth.assertThat( result.getText() )
				     .isEqualTo( "30.56" );
			}
		});
	}
	@Test
	void testKelvin() {
		testHasComponents();
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				Fahrenheit.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				List<JTextField>   textfields = Gooey.getComponents( frame, JTextField.class );
				List<JButton>      buttons    = Gooey.getComponents( frame, JButton.class );
				List<JLabel>       labels     = Gooey.getComponents( frame, JLabel.class );
				List<JRadioButton> radios     = Gooey.getComponents( frame, JRadioButton.class );
				// input data
				JTextField   fahrenheit = textfields.get( 0 );
				JButton      go         = buttons   .get( 0 );
				JLabel       result     = labels    .get( 2 );
				JRadioButton kelvin     = radios    .get( 1 );
				kelvin.setSelected( true );
				fahrenheit.setText( "87" );
				go        .doClick();
				// check result
				Truth.assertThat( result.getText() )
			         .isEqualTo( "303.71" );
			}
		});
	}
	@Test
	void testNoNumber() {
		testHasComponents();
		Gooey.capture( new GooeyFrame() {
			@Override
			public void invoke() {
				Fahrenheit.main( new String[]{ } );
			}
			@Override
			public void test(JFrame frame) {
				List<JTextField> textfields = Gooey.getComponents( frame, JTextField.class );
				List<JButton>    buttons    = Gooey.getComponents( frame, JButton.class );
				List<JLabel>     labels     = Gooey.getComponents( frame, JLabel.class );
				// input data
				JTextField fahrenheit = textfields.get( 0 );
				JButton    go         = buttons   .get( 0 );
				JLabel     result     = labels    .get( 2 );
				fahrenheit.setText( "a" );
				go        .doClick();
				// check result
				Truth.assertThat( result.getText() )
		             .isEqualTo( "Enter a number" );
			}
		});
	}
}
