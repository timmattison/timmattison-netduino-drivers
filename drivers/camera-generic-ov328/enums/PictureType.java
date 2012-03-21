package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

/**
 * The same as the data type
 * 
 * @author tim
 */
public enum PictureType {
	SNAPSHOT_PICTURE((byte) 0x01), PREVIEW_PICTURE((byte) 0x02), JPEG_PREVIEW_PICTURE((byte) 0x05);

	private final byte value;

	PictureType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}