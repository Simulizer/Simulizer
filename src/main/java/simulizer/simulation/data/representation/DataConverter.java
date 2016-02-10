package simulizer.simulation.data.representation;

import simulizer.assembler.representation.Variable;

import java.math.BigInteger;

public class DataConverter {

    public static BigInteger toSignedByte(byte i) {
        return BigInteger.valueOf(i);
    }
    public static BigInteger toUnsignedByte(byte i) {
        return BigInteger.valueOf(Integer.toUnsignedLong(i));
    }

    public static BigInteger toSignedByte(byte[] i) {
        assert i.length == 1;
        return BigInteger.valueOf(i[0]);
    }
    public static BigInteger toUnsignedByte(byte[] i) {
        assert i.length == 1;
        return BigInteger.valueOf(Integer.toUnsignedLong(i[0]));
    }

    public static BigInteger toSignedHalf(byte[] i) {
        assert i.length == 2;
        return BigInteger.valueOf(0);
    }
    public static BigInteger toUnsignedHalf(byte[] i) {
        assert i.length == 2;
        return BigInteger.valueOf(0);
    }

    public static BigInteger toSignedWord(byte[] i) {
        assert i.length == 4;
        return BigInteger.valueOf(0);
    }
    public static BigInteger toUnsignedWord(byte[] i) {
        assert i.length == 4;
        return BigInteger.valueOf(0);
    }

    /**
     * convert a sequence of bytes with a defined length to a String
     * @param b
     * @return
     */
    public static String toStringNotZ(byte[] b) {
        return "";
    }
    /**
     * convert a null terminated sequence of bytes to a String
     * can be given more bytes than the length of the string. Will stop
     * once null terminator is reached
     * @param b
     * @return
     */
    public static String toStringZ(byte[] b) {
        return "";
    }




    public static byte[] fromUnsignedByte(BigInteger i) {
        return new byte[] {};
    }
    public static byte[] fromSignedByte(BigInteger i) {
        return new byte[] {};
    }

    public static byte[] fromSignedHalf(BigInteger i) {
        return new byte[] {};
    }
    public static byte[] fromUnsignedHalf(BigInteger i) {
        return new byte[] {};
    }

    public static byte[] fromSignedWord(BigInteger i) {
        return new byte[] {};
    }
    public static byte[] fromUnsignedWord(BigInteger i) {
        return new byte[] {};
    }

    public static byte[] fromStringNotZ(String s) {
        return new byte[] {};
    }
    public static byte[] fromStringZ(String s) {
        return new byte[] {};
    }

    public static byte[] fromVariable(Variable v) {
        // soooo many assumptions made here, if something is wrong this could
        // crash any number of ways

        switch(v.getType()) {
            case Byte:
            case Half:
            case Word:
                return fromSignedWord(
                    BigInteger.valueOf(v.getInitialValue().get().asIntegerOp().value));
            case ASCII:
                return fromStringNotZ(v.getInitialValue().get().asStringOp().value);
            case ASCIIZ:
                return fromStringZ(v.getInitialValue().get().asStringOp().value);
            case Space:
            default:
                return new byte[v.getSize()];
        }
    }
}
