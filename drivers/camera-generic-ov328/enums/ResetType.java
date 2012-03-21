package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

public enum ResetType {
	WHOLE_SYSTEM_RESET((byte) 0x00), STATE_MACHINE_RESET((byte) 0x01);

	private final byte value;

	ResetType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}