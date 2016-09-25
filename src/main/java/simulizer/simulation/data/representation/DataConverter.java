package simulizer.simulation.data.representation;


import java.util.Arrays;

/**
 * Convert between integer representations
 * @author mbway
 */
// suppress warning that the assertions are always true
@SuppressWarnings("ConstantConditions")
public class DataConverter {

    /**
     * OR the given bytes into the long value at the starting byte.
     * Big endian ordering so the most significant byte is the first
     * element of `data` and is inserted at the startByte.
     * The value is filled towards the least significant byte.
     *
     * @param val the value to OR the data into
     * @param startByte the byte to insert the first element of `data`
     * @param data the bytes to OR in
     * @note big endian
     * @return the new value with the specified bytes filled
     */
    private static long fillBytes(long val, int startByte, byte[] data) {
        assert data.length <= Long.BYTES;
        // two write into n'th byte, offset = n-1
        assert startByte < Long.BYTES;

        for(int i = 0; i < data.length; i++) {
            // the offset of the byte to write in bits from the least
            // significant bit. the byte is written into the 8 bits more
            // significant than the offset.
            int byteOffset = 8*(startByte-i);

            // the 0xFF mask prevents the sign extension
            long byteAsLong = data[i] & 0xFF;

            val |= byteAsLong << byteOffset;
        }
        return val;
    }

    /**
     * given a number of bytes, interpret them as a signed integer and load the
     * value into a 64 bit signed integer
     * @param data the data to interpret
     * @return the interpreted value
     */
    public static long decodeAsSigned(byte[] data) {
        assert data.length <= 4 && data.length > 0;
        // on some exotic machines this may not be true
        assert Long.BYTES == 8;
        assert Byte.BYTES == 1;

        long value = 0;

        // extract the sign bit
        boolean signBit = (data[0] & 0x80) != 0;
        // the byte to pad with (all bits == sign bit)
        byte signExtension = (byte) (signBit ? 0xFF : 0x00);

        // padded bytes
        int startByte = Long.BYTES - 1;
        byte[] padding = new byte[Long.BYTES - data.length];
        Arrays.fill(padding, signExtension);

        value = fillBytes(value, startByte, padding);

        // data bytes
        startByte = data.length - 1;
        value = fillBytes(value, startByte, data);

        return value;
    }

    /**
     * given a number of bytes, interpret them as an unsigned integer and load
     * the value into a 64 bit signed integer
     * @param data the data to interpret
     * @return the interpreted value
     */
    public static long decodeAsUnsigned(byte[] data) {
        assert data.length <= 4 && data.length > 0;
        // on some exotic machines this may not be true
        assert Long.BYTES == 8;
        assert Byte.BYTES == 1;

        // big endian
        int startByte = data.length - 1;
        long value = 0;
        value = fillBytes(value, startByte, data);
        return value;
    }

    /**
     * take the n least significant bytes of a value
     *
     * @param value the value to truncate
     * @param numBytes the number of bytes to truncate to
     * @return the n least significant bytes
     */
    private static byte[] truncate(long value, int numBytes) {
        assert numBytes > 0 && numBytes <= Long.BYTES;
        assert Long.BYTES == 8;
        assert Byte.BYTES == 1;

        byte[] res = new byte[numBytes];

        // big endian
        int startByte = numBytes-1;
        for(int i = 0; i < numBytes; i++) {
            int byteOffset = 8*(startByte-i);
            res[i] = (byte) ((value >> byteOffset) & 0xFF);
        }
        return res;
    }

    /**
     * detect whether the value stored in a 64 bit integer would overflow a word (32 bit integer)
     *
     * @param value the value to check for overflow
     * @return whether the value would overflow a word
     */
    static boolean hasOverflow(long value) {
        assert Long.BYTES == 8;
        assert Byte.BYTES == 1;

        // get 4 most significant bytes
        // >>> inserts zeroes instead of doing sign extension
        long MSBs = (value >>> 8*4);

        // if all the bits are the same then there is no overflow
        return !((MSBs == 0) || (MSBs == 0xFFFFFFFFL));
    }

    /**
     * encode an integer value in a 32 bit two's complement representation.
     * This is achieved by truncating the integer if it is too large to fit in
     * 32 bits.
     * The integer should not overflow the representation.
     * @param value the value to encode
     * @return the 32 bit two's complement representation of the value
     */
    public static byte[] encodeAsSigned(long value) {
        assert !hasOverflow(value);
        return truncate(value, 4);
    }

    /**
     * encode an integer value as 32 bits
     * This is achieved by truncating the integer if it is too large to fit in
     * 32 bits.
     * It is allowed for the integer to overflow the representation.
     * @param value the value to encode (can be negative)
     * @return the 32 bit representation of the value
     */
    public static byte[] encodeAsUnsigned(long value) {
        return truncate(value, 4);
    }

    /**
     * encode an integer value as 64 bits
     * It is allowed for the integer to overflow the representation.
     * @param value the value to encode (can be negative)
     * @return the 64 bit representation of the value
     */
    public static byte[] encodeAsUnsignedLong(long value) {
        return truncate(value, 8);
    }

}
