package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

public enum ColorType {
	RAW_2_BIT_GRAY_SCALE((byte) 0x01), RAW_4_BIT_GRAY_SCALE((byte) 0x02), RAW_8_BIT_GRAY_SCALE((byte) 0x03), RAW_12_BIT_COLOR((byte) 0x05), RAW_16_BIT_COLOR((byte) 0x06), JPEG((byte) 0x07);

	private final byte value;

	ColorType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}