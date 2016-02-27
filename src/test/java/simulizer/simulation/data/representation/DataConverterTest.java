package simulizer.simulation.data.representation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test the DataConverter
 * @author mbway
 */
public class DataConverterTest {

    /**
     * integer power of 2
     */
    private long po2(int power) {
        return (long)Math.pow(2, power);
    }

    @Test
    public void testDecodeAsSigned() {
        byte ff = (byte) 0xFF; // =255

        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff}));
        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff, ff}));
        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff, ff, ff}));
        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff, ff, ff, ff}));

        assertEquals(10, DataConverter.decodeAsSigned(new byte[] {10}));
        assertEquals(-10, DataConverter.decodeAsSigned(new byte[] {-10}));

        assertEquals(10, DataConverter.decodeAsSigned(new byte[] {0, 10}));
        assertEquals(-10, DataConverter.decodeAsSigned(new byte[] {ff, -10}));

        assertEquals(10, DataConverter.decodeAsSigned(new byte[] {0, 0, 10}));
        assertEquals(-10, DataConverter.decodeAsSigned(new byte[] {ff, ff, -10}));

        assertEquals(po2(16) - 1, DataConverter.decodeAsSigned(new byte[] {0, ff, ff}));
        assertEquals(po2(16) - 1, DataConverter.decodeAsSigned(new byte[] {0, 0, ff, ff}));
    }

    @Test
    public void testDecodeAsUnsigned() {
        byte ff = (byte) 0xFF; // =255

        assertEquals(po2(8)  - 1, DataConverter.decodeAsUnsigned(new byte[] {ff}));
        assertEquals(po2(16) - 1, DataConverter.decodeAsUnsigned(new byte[] {ff, ff}));
        assertEquals(po2(24) - 1, DataConverter.decodeAsUnsigned(new byte[] {ff, ff, ff}));
        assertEquals(po2(32) - 1, DataConverter.decodeAsUnsigned(new byte[] {ff, ff, ff, ff}));

        assertEquals(10, DataConverter.decodeAsUnsigned(new byte[] {10}));
        assertEquals(246 , po2(8) - 10);
        assertEquals(po2(8) - 10, DataConverter.decodeAsUnsigned(new byte[] {-10}));

        assertEquals(10, DataConverter.decodeAsUnsigned(new byte[] {0, 10}));
        assertEquals(65526 , po2(16) - 10);
        assertEquals(po2(16) - 10, DataConverter.decodeAsUnsigned(new byte[] {ff, -10}));

        assertEquals(10, DataConverter.decodeAsUnsigned(new byte[] {0, 0, 10}));
        assertEquals(16777206, po2(24) - 10);
        assertEquals(po2(24) - 10, DataConverter.decodeAsUnsigned(new byte[] {ff, ff, -10}));

        assertEquals(po2(16) - 1, DataConverter.decodeAsUnsigned(new byte[] {0, ff, ff}));
        assertEquals(po2(16) - 1, DataConverter.decodeAsUnsigned(new byte[] {0, 0, ff, ff}));
    }

    @SuppressWarnings("NumericOverflow")
    @Test
    public void testHasOverflow() {
        // the boundary
        assertTrue(DataConverter.hasOverflow(po2(32)));
        assertFalse(DataConverter.hasOverflow(po2(32) - 1));
        assertTrue(DataConverter.hasOverflow(-po2(32) - 1));
        assertFalse(DataConverter.hasOverflow(-po2(32)));

        // large values way into the overflow range
        assertTrue(DataConverter.hasOverflow(po2(40) - 123));
        assertTrue(DataConverter.hasOverflow(-po2(40) - 123));

        // random good values
        assertFalse(DataConverter.hasOverflow(0));
        assertFalse(DataConverter.hasOverflow(1));
        assertFalse(DataConverter.hasOverflow(-1));
        assertFalse(DataConverter.hasOverflow(14254));
        assertFalse(DataConverter.hasOverflow(-14254));

        // this overflows the long holding the integer and so should
        // incorrectly say that the integer has not overflowed
        // because the value is back into the allowed range
        assertEquals(-2, Long.MAX_VALUE + Long.MAX_VALUE);
        assertFalse(DataConverter.hasOverflow(Long.MAX_VALUE + Long.MAX_VALUE));
    }

    @Test
    public void testEncodeAsSigned() {
        byte ff = (byte) 0xFF; // =255

        // should work exactly the same as encode as unsigned

        assertArrayEquals(new byte[] {ff, ff, ff, ff}, DataConverter.encodeAsSigned(-1));
        assertArrayEquals(new byte[] {0, 0, 0, ff}, DataConverter.encodeAsSigned(255));
        assertArrayEquals(new byte[] {ff, ff, ff, 0}, DataConverter.encodeAsSigned(-256));

        assertArrayEquals(new byte[] {0, 0, 0, 10}, DataConverter.encodeAsSigned(10));
        assertArrayEquals(new byte[] {ff, ff, ff, (byte)0xf6}, DataConverter.encodeAsSigned(-10));

        assertArrayEquals(new byte[] {0, 0, ff, ff}, DataConverter.encodeAsSigned(po2(16) - 1));
        assertArrayEquals(new byte[] {0, 0, ff, (byte)0xfe}, DataConverter.encodeAsSigned(po2(16) - 2));
    }

    @Test
    public void testEncodeAsUnsigned() {
        byte ff = (byte) 0xFF; // =255

        // should work exactly the same as encode as signed

        assertArrayEquals(new byte[] {ff, ff, ff, ff}, DataConverter.encodeAsSigned(-1));
        assertArrayEquals(new byte[] {0, 0, 0, ff}, DataConverter.encodeAsSigned(255));
        assertArrayEquals(new byte[] {ff, ff, ff, 0}, DataConverter.encodeAsSigned(-256));

        assertArrayEquals(new byte[] {0, 0, 0, 10}, DataConverter.encodeAsSigned(10));
        assertArrayEquals(new byte[] {ff, ff, ff, (byte)0xf6}, DataConverter.encodeAsSigned(-10));

        assertArrayEquals(new byte[] {0, 0, ff, ff}, DataConverter.encodeAsSigned(po2(16) - 1));
        assertArrayEquals(new byte[] {0, 0, ff, (byte)0xfe}, DataConverter.encodeAsSigned(po2(16) - 2));
    }

    @Test
    public void testBackAndFourth() {
        List<Long> vals = Arrays.asList(
            0L, 1L, -1L, 10L, -10L, 255L, 123452L, -213421345L
        );

        for(long val : vals) {
            assertEquals(val, DataConverter.decodeAsSigned(DataConverter.encodeAsSigned(val)));
            if(val >= 0) { // if negative then this won't work
                assertEquals(val, DataConverter.decodeAsUnsigned(DataConverter.encodeAsUnsigned(val)));
            }
        }

        List<List<Byte>> bytes = Arrays.asList(
            Arrays.asList((byte)0xf4, (byte)0),
            Arrays.asList((byte)0xf4, (byte)0, (byte)0x51),
            Arrays.asList((byte)0xf4, (byte)0, (byte)0x51, (byte)0xa8),
            Arrays.asList((byte)0x04, (byte)0, (byte)0x51, (byte)0xa8),
            Arrays.asList((byte)0x04, (byte)0, (byte)0x51),
            Arrays.asList((byte)0x04, (byte)0)
        );

        for(List<Byte> bs : bytes) {
            byte[] b = new byte[bs.size()];
            for(int i = 0; i < bs.size(); i++) {
                b[i] = bs.get(i);
            }

            // the output is of fixed size (4) so using
            // the known good decode as signed to test equality
            byte[] processed = DataConverter.encodeAsSigned(DataConverter.decodeAsSigned(b));
            assertEquals(DataConverter.decodeAsSigned(b), DataConverter.decodeAsSigned(processed));
            processed = DataConverter.encodeAsUnsigned(DataConverter.decodeAsUnsigned(b));
            assertEquals(DataConverter.decodeAsUnsigned(b), DataConverter.decodeAsUnsigned(processed));
        }
    }
}
