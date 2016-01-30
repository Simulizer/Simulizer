package javafxprototype;

import junit.framework.*;

public class TestSuite extends TestCase {
	protected void setUp() {

	}

	public void testThing() {
    	int x = 5;
        assertTrue(x * x == 25);
    }
}
