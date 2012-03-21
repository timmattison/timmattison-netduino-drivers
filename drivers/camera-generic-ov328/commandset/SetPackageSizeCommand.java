package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class SetPackageSizeCommand extends Command {
	private static final String COMMAND_NAME = "SET PACKAGE SIZE";
	private static final byte ID = 0x06;
	private static final int PACKAGE_SIZE_LOW_BYTE = PARAMETER_2_BYTE;
	private static final int PACKAGE_SIZE_HIGH_BYTE = PARAMETER_3_BYTE;
	private static final int PARAMETER_1_FIXED_VALUE = 0x08;

	protected int packageSize;

	public SetPackageSizeCommand(int packageSize) {
		this.packageSize = packageSize;
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

		// Set the fixed value for parameter 1
		parameterBytes[PARAMETER_1_BYTE] = PARAMETER_1_FIXED_VALUE;

		// Set the package size
		parameterBytes[PACKAGE_SIZE_LOW_BYTE] = (byte) (packageSize & 0xFF);
		parameterBytes[PACKAGE_SIZE_HIGH_BYTE] = (byte) ((packageSize >> 8) & 0xFF);

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
