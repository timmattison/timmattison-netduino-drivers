package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.enums.ColorType;
import com.timmattison.firmware.serial.peripherals.camera.c328.enums.JPEGResolution;
import com.timmattison.firmware.serial.peripherals.camera.c328.enums.RawResolution;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.InvalidColorType;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class InitialCommand extends Command {
	private static final String COMMAND_NAME = "INITIAL";
	private static final byte ID = 0x01;
	private static final int COLOR_TYPE_BYTE = PARAMETER_2_BYTE;
	private static final int RAW_RESOLUTION_BYTE = PARAMETER_3_BYTE;
	private static final int JPEG_RESOLUTION_BYTE = PARAMETER_4_BYTE;

	protected ColorType colorType;
	protected RawResolution rawResolution;
	protected JPEGResolution jpegResolution;

	public InitialCommand(ColorType colorType, RawResolution rawResolution) throws InvalidColorType {
		// Did they specify a proper RAW color type?
		if (colorType == ColorType.JPEG) {
			// No, throw an exception
			throw new InvalidColorType();
		}

		this.colorType = colorType;
		this.rawResolution = rawResolution;
		this.jpegResolution = null;
	}

	public InitialCommand(JPEGResolution jpegResolution) {
		this.colorType = ColorType.JPEG;
		this.jpegResolution = jpegResolution;
		this.rawResolution = null;
	}

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() throws MissingParameterException {
		// Is the color type set?
		if (colorType == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 1, "color type");
		}

		// Create an array to hold the data we're going to return
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		// Initialize the bytes
		for (int loop = 0; loop < PARAMETER_SIZE; loop++) {
			parameterBytes[loop] = 0x00;
		}

		// Set the color type
		parameterBytes[COLOR_TYPE_BYTE] = colorType.getValue();

		// Do they want a JPEG?
		if (colorType == ColorType.JPEG) {
			// Yes, did they specify a JPEG resolution?
			if (jpegResolution == null) {
				// No, throw an exception
				throw new MissingParameterException(getCommandName(), 4, "JPEG resolution");
			} else {
				// Yes, they did.  Store it in the parameter bytes.
				parameterBytes[JPEG_RESOLUTION_BYTE] = jpegResolution.getValue();
			}
		} else {
			// No, they wanted a raw image.  Did thye specify a raw resolution?
			if (rawResolution == null) {
				// No, throw an exception
				throw new MissingParameterException(getCommandName(), 3, "RAW resolution");
			} else {
				// Yes, they did.  Store it in the parameter bytes.
				parameterBytes[RAW_RESOLUTION_BYTE] = rawResolution.getValue();
			}
		}

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
