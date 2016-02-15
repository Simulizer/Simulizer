package simulizer.assembler.representation.operand;

import java.util.Arrays;

/**
 * specifies the type of operands that should be accepted for a particular instruction
 * @author mbway
 */
public class OperandFormat {
    public enum OperandType {
        // TYPE (parent) where TYPE should allow the parent
        // eg if any immediate is required, accept unsigned immediate values
        // eg if destination register is required, accept any register

        REGISTER    (null),
            DEST_REGISTER   (REGISTER),       // register written to
            SRC_REGISTER    (REGISTER),       // register value read
            TARGET_REGISTER (REGISTER),       // register value jumped to exactly (not base/offset) or used as branch condition
        UNSIGNED_IMMEDIATE (null),            // unsigned immediate integer
            IMMEDIATE   (UNSIGNED_IMMEDIATE), // positive or negative immediate integer
        LABEL       (null),                   // label
        BASE_OFFSET (null);                   // of the form (regex) `label? (+|-)? offset? ( register )`

        private OperandType shouldAlsoAccept;

        OperandType(OperandType shouldAlsoAccept) {
            this.shouldAlsoAccept = shouldAlsoAccept;
        }

        public boolean accepts(OperandType t) {
            return equals(t) || (shouldAlsoAccept != null && shouldAlsoAccept.accepts(t));
        }
    }


    public static OperandFormat noArguments = new OperandFormat();

    public static OperandFormat register = new OperandFormat()
        .allowed1(OperandType.REGISTER);

    public static OperandFormat label = new OperandFormat()
        .allowed1(OperandType.LABEL);

    public static OperandFormat cmpLabel = new OperandFormat()
        .allowed1(OperandType.REGISTER)
        .allowed2(OperandType.LABEL);

    public static OperandFormat srcAddr = new OperandFormat()
        .allowed1(OperandType.DEST_REGISTER)
        .allowed2(OperandType.LABEL, OperandType.BASE_OFFSET);

    public static OperandFormat destAddr = new OperandFormat()
        .allowed1(OperandType.DEST_REGISTER)
        .allowed2(OperandType.LABEL, OperandType.BASE_OFFSET);

    public static OperandFormat destImm = new OperandFormat()
        .allowed1(OperandType.DEST_REGISTER)
        .allowed2(OperandType.IMMEDIATE);

    public static OperandFormat destSrc = new OperandFormat()
        .allowed1(OperandType.DEST_REGISTER)
        .allowed2(OperandType.SRC_REGISTER);

    public static OperandFormat destSrcSrc = new OperandFormat()
        .allowed1(OperandType.DEST_REGISTER)
        .allowed2(OperandType.SRC_REGISTER)
        .allowed3(OperandType.SRC_REGISTER);

    public static OperandFormat destSrcImm = new OperandFormat()
        .allowed1(OperandType.DEST_REGISTER)
        .allowed2(OperandType.SRC_REGISTER)
        .allowed3(OperandType.IMMEDIATE);

    public static OperandFormat destSrcImmU = new OperandFormat()
        .allowed1(OperandType.DEST_REGISTER)
        .allowed2(OperandType.SRC_REGISTER)
        .allowed3(OperandType.UNSIGNED_IMMEDIATE);

    public static OperandFormat cmpCmpLabel = new OperandFormat()
        .allowed1(OperandType.REGISTER)
        .allowed2(OperandType.REGISTER)
        .allowed3(OperandType.LABEL);



    public OperandType[] allowedPos1;
    public OperandType[] allowedPos2;
    public OperandType[] allowedPos3;

    public OperandFormat() {
        allowedPos1 = new OperandType[] {};
        allowedPos2 = new OperandType[] {};
        allowedPos3 = new OperandType[] {};
    }

    @Override
    public String toString() {
        return Arrays.toString(allowedPos1) +
            Arrays.toString(allowedPos2) +
            Arrays.toString(allowedPos3);
    }

    public int getNumArgs() {
        if(allowedPos1.length == 0) {
            return 0;
        } else if(allowedPos2.length == 0) {
            return 1;
        } else if(allowedPos3.length == 0) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Test whether given arguments are valid for the given format
     * pass null for any arguments that are not used
     *
     * @param arg1 the type of argument 1
     * @param arg2 the type of argument 2
     * @param arg3 the type of argument 3
     * @return whether the arguments matched the format
     */
    public boolean valid(OperandType arg1, OperandType arg2, OperandType arg3) {
        boolean isGood;

        OperandType allowed[][] = new OperandType[][] {allowedPos1, allowedPos2, allowedPos3};
        OperandType args[] = new OperandType[] {arg1, arg2, arg3};

        for(int i = 0; i < 3; i++) {
            isGood = false;

            if(allowed[i].length == 0) {
                // no arguments allowed in this slot
                isGood = args[i] == null;
            } else {
                for(OperandType t : allowed[i]) {
                    if(t.accepts(args[i])) {
                        isGood = true;
                        break;
                    }
                }
            }

            if(!isGood) {
                return false;
            }
        }

        return true;
    }

    public OperandFormat allowed1(OperandType... allowedTypes) {
        allowedPos1 = allowedTypes;
        return this;
    }
    public OperandFormat allowed2(OperandType... allowedTypes) {
        allowedPos2 = allowedTypes;
        return this;
    }
    public OperandFormat allowed3(OperandType... allowedTypes) {
        allowedPos3 = allowedTypes;
        return this;
    }
}
