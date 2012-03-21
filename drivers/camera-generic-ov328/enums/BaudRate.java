package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

public enum BaudRate {
	BAUD_RATE_7200((byte) 0xFF, (byte) 0x01), BAUD_RATE_9600((byte) 0xBF, (byte) 0x01), BAUD_RATE_14400((byte) 0x7F, (byte) 0x01), BAUD_RATE_19200((byte) 0x5F, (byte) 0x01), BAUD_RATE_28800((byte) 0x3F, (byte) 0x01), BAUD_RATE_38400((byte) 0x2F, (byte) 0x01), BAUD_RATE_57600((byte) 0x1F, (byte) 0x01), BAUD_RATE_115200((byte) 0x0F, (byte) 0x01);

	private final byte firstDivider;
	private final byte secondDivider;

	BaudRate(byte firstDivider, byte secondDivider) {
		this.firstDivider = firstDivider;
		this.secondDivider = secondDivider;
	}

	public byte getFirstDivider() {
		return firstDivider;
	}

	public byte getSecondDivider() {
		return secondDivider;
	}
}