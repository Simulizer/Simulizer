package simulizer.ui.windows.help;

/**
 * Syscall Reference
 * 
 * @author Michael
 *
 */
public class SyscallReference extends SimpleTablePairWindow {
	//@formatter:off
	public SyscallReference() {
		super("Syscall", "Action", 0.2);
		setData(new String[][]{ 
			{"1", "print int (in a0)"},
			{"4", "print string (start address of null terminated string in a0)"},
			{"5", "read int (stores in v0)"},
			{"8", "read string (a0 address of input buffer, a1 max number of bytes to be read)"},
			{"9", "sbrk (no. bytes to allocate in a0) (address of start of block in v0)"},
			{"10", "stops execution (only way to exit cleanly)"},
			{"11", "print char (in a0)"},
			{"12", "read char (stores in v0)"}
		});
	}
	//@formatter:on
}
