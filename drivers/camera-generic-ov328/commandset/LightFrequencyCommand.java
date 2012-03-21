package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.enums.LightFrequency;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class LightFrequencyCommand extends Command {
	private static final String COMMAND_NAME = "LIGHT FREQUENCY";
	private static final byte ID = 0x13;
	private LightFrequency lightFrequency;

	public LightFrequencyCommand(LightFrequency lightFrequency) {
		this.lightFrequency = lightFrequency;
	}

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() throws MissingParameterException {
		// Is the light frequency set?
		if (lightFrequency == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 1, "light frequency");
		}

		// Everything is set up, build up the byte array
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		parameterBytes[PARAMETER_1_BYTE] = lightFrequency.getValue();
		parameterBytes[PARAMETER_2_BYTE] = 0x00;
		parameterBytes[PARAMETER_3_BYTE] = 0x00;
		parameterBytes[PARAMETER_4_BYTE] = 0x00;

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
