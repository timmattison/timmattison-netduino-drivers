package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.IncorrectParameterException;

public class SyncCommand extends ParsableCommand {
	private static final String COMMAND_NAME = "SYNC";
	private static final byte ID = 0x0D;
	private static final byte[] PARAMETER_BYTES = { 0x00, 0x00, 0x00, 0x00 };

	public SyncCommand(byte[] bytes) throws IncorrectParameterException {
		super(bytes);
	}

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() {
		return PARAMETER_BYTES;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	protected void setParameter1(byte parameter1) {
		validateParameter(parameter1, 1);
	}

	@Override
	protected void setParameter2(byte parameter2) {
		validateParameter(parameter2, 2);
	}

	@Override
	protected void setParameter3(byte parameter3) {
		validateParameter(parameter3, 3);
	}

	@Override
	protected void setParameter4(byte parameter4) {
		validateParameter(parameter4, 4);
	}

	/**
	 * Validates that all of the parameters are zero
	 * 
	 * @param parameter
	 * @param parameterNumber
	 */
	private void validateParameter(byte parameter, int parameterNumber) {
		// Is this a zero?
		if (parameter != 0x00) {
			// No, this must be zero.  Throw an exception.
			throw new IllegalStateException("Non-zero parameter received for parameter " + parameterNumber + " in " + getCommandName() + " command");
		}
	}

	@Override
	protected void allParametersSet() {
		// Nothing to do here
	}
}
