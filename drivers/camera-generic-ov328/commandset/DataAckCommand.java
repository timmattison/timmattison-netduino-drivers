package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

public class DataAckCommand extends AckCommand {
	private static final String COMMAND_NAME = "DATA ACK";
	private static final byte FINAL_PACKAGE_ID_LOW_BYTE = (byte) 0xF0;
	private static final byte FINAL_PACKAGE_ID_HIGH_BYTE = (byte) 0xF0;

	public DataAckCommand(int packageNumber) {
		super((byte) 0, (byte) 0);

		// Parameters 3 and 4 are the package number, low byte in 3, high byte in 4
		setParameter3((byte) (packageNumber & 0xFF));
		setParameter4((byte) ((packageNumber >> 8) & 0xFF));
	}

	public DataAckCommand() {
		super((byte) 0, (byte) 0);

		setParameter3(FINAL_PACKAGE_ID_LOW_BYTE);
		setParameter4(FINAL_PACKAGE_ID_HIGH_BYTE);
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
