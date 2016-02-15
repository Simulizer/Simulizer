package simulizer.simulation.data.representation;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the DataConverter
 * @author mbway
 */
public class DataConverterTest {

    @Test
    public void testDecodeAsSigned() {
        byte ff = (byte) 0xFF;

        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff}));
        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff, ff}));
        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff, ff, ff}));
        assertEquals(-1, DataConverter.decodeAsSigned(new byte[] {ff, ff, ff, ff}));
    }
}
