package simulizer.simulation.listeners;

import simulizer.assembler.representation.Address;

/**
 * A message which is sent every time an instruction is executed
 * sent after the fetch, before the decode
 * @author mbway
 */
public class ExecuteStatementMessage extends Message {
	public Address statementAddress;

	public ExecuteStatementMessage(Address statementAddress) {
		this.statementAddress = statementAddress;
	}
}
