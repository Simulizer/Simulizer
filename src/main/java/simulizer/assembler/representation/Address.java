package simulizer.assembler.representation;

/**
 * immutable memory address
 * @author mbway
 */
public class Address {
    private final int value;

    public static final Address NULL = new Address(0);

    public Address(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Address(0x" + Integer.toHexString(value) + ")";
    }

    @Override
    public boolean equals(Object other) {
        return this == other ||
            (other instanceof Address &&
                value == ((Address) other).value);
    }

    @Override
    public int hashCode() {
        return value; // Integer class does this
    }
}
