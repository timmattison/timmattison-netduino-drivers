package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.enums.ResetType;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class ResetCommand extends Command {
	private static final String COMMAND_NAME = "RESET";
	private static final byte ID = 0x08;
	private static final int RESET_TYPE_BYTE = PARAMETER_1_BYTE;
	private static final int IMMEDIATE_TYPE_BYTE = PARAMETER_4_BYTE;
	private static final byte IMMEDIATE_FLAG = (byte) 0xFF;

	protected ResetType resetType;
	protected boolean immediate;

	public ResetCommand(ResetType resetType, boolean immediate) {
		this.resetType = resetType;
		this.immediate = immediate;
	}

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() throws MissingParameterException {
		// Create an array to hold the data we're going to return
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		// Initialize the bytes
		for (int loop = 0; loop < PARAMETER_SIZE; loop++) {
			parameterBytes[loop] = 0x00;
		}

		// Set the reset type
		parameterBytes[RESET_TYPE_BYTE] = resetType.getValue();

		// Is this an immediate request?
		if (immediate) {
			// Yes, set the immediate flag
			parameterBytes[IMMEDIATE_TYPE_BYTE] = IMMEDIATE_FLAG;
		}

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
