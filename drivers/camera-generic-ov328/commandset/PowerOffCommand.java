package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

public class PowerOffCommand extends Command {
	private static final String COMMAND_NAME = "POWER OFF";
	private static final byte ID = 0x09;
	private static final byte[] PARAMETER_BYTES = { 0x00, 0x00, 0x00, 0x00 };

	public PowerOffCommand() {
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
}
