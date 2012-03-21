package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

public enum JPEGResolution {
	JPEG_IMAGE_80x64((byte) 0x01), JPEG_IMAGE_160x128((byte) 0x03), JPEG_IMAGE_320x240((byte) 0x05), JPEG_IMAGE_640x480((byte) 0x07);

	private final byte value;

	JPEGResolution(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}