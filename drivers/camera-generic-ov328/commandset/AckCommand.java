package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.IncorrectParameterException;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class AckCommand extends ParsableCommand {
	private static final String COMMAND_NAME = "ACK";
	private static final byte ID = 0x0E;

	public AckCommand(byte commandId, byte ackCounter) {
		setParameter1(commandId);
		setParameter2(ackCounter);
	}

	public AckCommand(byte[] bytes) throws IncorrectParameterException {
		super(bytes);
	}

	/**
	 * ACKs an ACK (acknowledges reception of an acknowledgment command from the camera). This is equivalent to sending the ACK command directly back to the
	 * camera but has been broken out here for completeness.
	 * 
	 * @param receivedAckCommand
	 */
	public AckCommand(AckCommand receivedAckCommand) {
		setParameter1(receivedAckCommand.getCommandId());
		setParameter2(receivedAckCommand.getAckCounter());
	}

	/**
	 * The command that is being acknowledged
	 */
	private Byte commandId;

	/**
	 * For debugging only, not utilized in the protocol
	 */
	private Byte ackCounter;

	/**
	 * Used for acknowledging data commands otherwise it is set to zero
	 */
	private Byte packageIdByte0;

	/**
	 * Used for acknowledging data commands otherwise it is set to zero
	 */
	private Byte packageIdByte1;

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() throws MissingParameterException {
		// Is the command ID set?
		if (commandId == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 1, "command ID");
		}

		// Is the ACK counter set?
		if (ackCounter == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 2, "ACK counter");
		}

		// Is the package ID byte 0 set?
		if (packageIdByte0 == null) {
			// No, it isn't.  Set it to zero.
			packageIdByte0 = 0x00;
		}

		// Is the package ID byte 1 set?
		if (packageIdByte1 == null) {
			// No, it isn't.  Set it to zero.
			packageIdByte1 = 0x00;
		}

		// Everything is set up, build up the byte array
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		parameterBytes[PARAMETER_1_BYTE] = commandId;
		parameterBytes[PARAMETER_2_BYTE] = ackCounter;
		parameterBytes[PARAMETER_3_BYTE] = packageIdByte0;
		parameterBytes[PARAMETER_4_BYTE] = packageIdByte1;

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	protected void setParameter1(byte parameter1) {
		commandId = parameter1;
	}

	@Override
	protected void setParameter2(byte parameter2) {
		ackCounter = parameter2;
	}

	@Override
	protected void setParameter3(byte parameter3) {
		packageIdByte0 = parameter3;
	}

	@Override
	protected void setParameter4(byte parameter4) {
		packageIdByte1 = parameter4;
	}

	@Override
	protected void allParametersSet() {
	}

	protected Byte getCommandId() {
		return commandId;
	}

	protected Byte getAckCounter() {
		return ackCounter;
	}

	public boolean isAckFor(Command command) {
		// Does the command ID match the ID number of the command passed in?
		if (getCommandId() == command.getIdNumber()) {
			// Yes, success
			return true;
		} else {
			// No, failure
			return false;
		}
	}
}
