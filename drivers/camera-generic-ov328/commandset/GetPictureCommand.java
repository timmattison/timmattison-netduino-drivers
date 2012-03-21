package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.enums.PictureType;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class GetPictureCommand extends Command {
	private static final String COMMAND_NAME = "GET PICTURE";
	private static final byte ID = 0x04;
	private static final int PICTURE_TYPE_BYTE = PARAMETER_1_BYTE;

	protected PictureType pictureType;

	public GetPictureCommand(PictureType pictureType) {
		this.pictureType = pictureType;
	}

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() throws MissingParameterException {
		// Is the picture type set?
		if (pictureType == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 1, "picture type");
		}

		// Create an array to hold the data we're going to return
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		// Initialize the bytes
		for (int loop = 0; loop < PARAMETER_SIZE; loop++) {
			parameterBytes[loop] = 0x00;
		}

		// Set the picture type
		parameterBytes[PICTURE_TYPE_BYTE] = pictureType.getValue();

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
