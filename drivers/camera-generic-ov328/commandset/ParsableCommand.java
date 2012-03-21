package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.IncorrectParameterException;

public abstract class ParsableCommand extends Command {
	/**
	 * Sets the first parameter
	 * 
	 * @param parameter1
	 * @throws IncorrectParameterException
	 */
	protected abstract void setParameter1(byte parameter1) throws IncorrectParameterException;

	/**
	 * Sets the second parameter
	 * 
	 * @param parameter2
	 */
	protected abstract void setParameter2(byte parameter2) throws IncorrectParameterException;

	/**
	 * Sets the third parameter
	 * 
	 * @param parameter3
	 */
	protected abstract void setParameter3(byte parameter3) throws IncorrectParameterException;

	/**
	 * Sets the fourth parameter
	 * 
	 * @param parameter4
	 */
	protected abstract void setParameter4(byte parameter4) throws IncorrectParameterException;

	/**
	 * Called when all of the parameters are set so any additional processing can be done
	 */
	protected abstract void allParametersSet();

	/**
	 * Do nothing constructor
	 */
	public ParsableCommand() {
		// Do nothing
	}

	/**
	 * Validates and parses a command and sets the related internal variables
	 * 
	 * @param bytes
	 * @throws IncorrectParameterException
	 */
	public ParsableCommand(byte[] bytes) throws IncorrectParameterException {
		// Is there any data?
		if (bytes == null) {
			// No, throw an exception
			throw new IllegalStateException("Cannot process empty data for " + getCommandName());
		}

		// Is the data the correct length?
		if (bytes.length != COMMAND_SIZE) {
			// No, throw an exception
			throw new IllegalStateException("Expected " + COMMAND_SIZE + " command bytes, received " + bytes.length);
		}

		// Is the first byte correct?
		if (bytes[0] != PREFIX_BYTE) {
			// No, throw an exception
			throw new IllegalStateException("Invalid prefix byte [expected " + formatByte(PREFIX_BYTE) + ", received " + formatByte(bytes[0]) + "]");
		}

		// Is the second byte correct?
		if (bytes[1] != getIdNumber()) {
			// No, throw an exception
			throw new IllegalStateException("Invalid ID number " + formatByte(bytes[1]) + ", expected " + formatByte(getIdNumber()));
		}

		// Copy the other bytes and let the implementing class handle the validation
		setParameter1(bytes[2]);
		setParameter2(bytes[3]);
		setParameter3(bytes[4]);
		setParameter4(bytes[5]);
	}
}
