

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;

public class GooeyTest {
	private static final double BUILD_VERSION = 1.8;
	
	@Test
	public void testVersion() {
		double actual   = Gooey.getVersion();
		double expected = BUILD_VERSION;
		assertTrue( "", expected <= actual );
	}
	@Test
	public void testVersionWithReflection() {
		String updateGooey = "download a newer version of gooey.jar from <https://github.com/robertoaflores/Gooey>";
		try {
			Class<?> gooey    = Class.forName("edu.cnu.cs.gooey.Gooey");
			Method   version  = gooey.getMethod( "getVersion" );
			Double   actual   = (Double) version.invoke( null );
			double   expected = BUILD_VERSION;
			assertTrue( updateGooey, expected <= actual );
		} catch (ClassNotFoundException |
				 NoSuchMethodException  | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail( updateGooey );
		}
	}
}
