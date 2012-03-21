package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.IncorrectParameterException;

public class NackCommand extends ParsableCommand {
	private static final String COMMAND_NAME = "NACK";
	private static final byte ID = 0x0F;
	private static final byte PARAMETER_1_REQUIRED_VALUE = 0x00;
	private static final byte PARAMETER_4_REQUIRED_VALUE = 0x00;

	public NackCommand(byte commandId, byte nackCounter) throws IncorrectParameterException {
		setParameter1(commandId);
		setParameter2(nackCounter);
	}

	public NackCommand(byte[] bytes) throws IncorrectParameterException {
		super(bytes);
	}

	/**
	 * For debugging only, not utilized in the protocol
	 */
	private Byte nackCounter;

	/**
	 * The error number
	 */
	private Byte errorNumber;

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() {
		// Is the NACK counter set?
		if (nackCounter == null) {
			// No, throw an exception
			throw new IllegalStateException("NACK counter byte not set for a " + COMMAND_NAME);
		}

		// Is the error number set?
		if (errorNumber == null) {
			// No, it isn't.  Set it to zero.
			throw new IllegalStateException("Error number byte not set for a " + COMMAND_NAME);
		}

		// Everything is set up, build up the byte array
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		parameterBytes[PARAMETER_1_BYTE] = 0x00;
		parameterBytes[PARAMETER_2_BYTE] = nackCounter;
		parameterBytes[PARAMETER_3_BYTE] = errorNumber;
		parameterBytes[PARAMETER_4_BYTE] = 0x00;

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	protected void setParameter1(byte parameter1) throws IncorrectParameterException {
		// Is the first parameter zero?
		if (parameter1 != PARAMETER_1_REQUIRED_VALUE) {
			// No, throw an exception
			throw new IncorrectParameterException(getCommandName(), 1, PARAMETER_1_REQUIRED_VALUE, parameter1);
		}
	}

	@Override
	protected void setParameter2(byte parameter2) {
		nackCounter = parameter2;
	}

	@Override
	protected void setParameter3(byte parameter3) {
		errorNumber = parameter3;
	}

	@Override
	protected void setParameter4(byte parameter4) throws IncorrectParameterException {
		// Is the fourth parameter zero?
		if (parameter4 != PARAMETER_4_REQUIRED_VALUE) {
			// No, throw an exception
			throw new IncorrectParameterException(getCommandName(), 4, PARAMETER_4_REQUIRED_VALUE, parameter4);
		}
	}

	@Override
	protected void allParametersSet() {
	}

	protected Byte getNackCounter() {
		return nackCounter;
	}

	protected Byte getErrorNumber() {
		return errorNumber;
	}
}
