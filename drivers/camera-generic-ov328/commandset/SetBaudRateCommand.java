package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.enums.BaudRate;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class SetBaudRateCommand extends Command {
	private static final String COMMAND_NAME = "SET BAUD RATE";
	private static final byte ID = 0x07;
	private static final int FIRST_DIVIDER_BYTE = PARAMETER_1_BYTE;
	private static final int SECOND_DIVIDER_BYTE = PARAMETER_2_BYTE;

	protected BaudRate baudRate;

	public SetBaudRateCommand(BaudRate baudRate) {
		this.baudRate = baudRate;
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

		// Set the baud rate dividers
		parameterBytes[FIRST_DIVIDER_BYTE] = (byte) (baudRate.getFirstDivider() & 0xFF);
		parameterBytes[SECOND_DIVIDER_BYTE] = (byte) ((baudRate.getSecondDivider() >> 8) & 0xFF);

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
