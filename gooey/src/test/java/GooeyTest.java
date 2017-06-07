

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;

public class GooeyTest {
	private static final String BUILD_VERSION = "1.8.0";
	
	@Test
	public void testVersion() {
		String actual   = Gooey.getVersion();
		String expected = BUILD_VERSION;
		assertTrue( "", actual.startsWith( expected ));
	}
	@Test
	public void testVersionWithReflection() {
		String updateGooey = "download a newer version of gooey.jar from <https://github.com/robertoaflores/Gooey>";
		try {
			Class<?> gooey    = Class.forName("edu.cnu.cs.gooey.Gooey");
			Method   version  = gooey.getMethod( "getVersion" );
			String   actual   = (String) version.invoke( null );
			String   expected = BUILD_VERSION;
			assertTrue( "", actual.startsWith( expected ));
		} catch (ClassNotFoundException |
				 NoSuchMethodException  | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail( updateGooey );
		}
	}
}
