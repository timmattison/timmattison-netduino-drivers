package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

public enum RawResolution {
	RAW_IMAGE_80x60((byte) 0x01), RAW_IMAGE_160x120((byte) 0x03);

	private final byte value;

	RawResolution(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}