package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

public enum LightFrequency {
	LIGHT_FREQUENCY_50HZ((byte) 0x00), LIGHT_FREQUENCY_60HZ((byte) 0x01);

	private final byte value;

	LightFrequency(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}