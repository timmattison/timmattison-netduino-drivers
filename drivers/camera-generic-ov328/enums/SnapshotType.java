package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

public enum SnapshotType {
	COMPRESSED_PICTURE((byte) 0x00), UNCOMPRESSED_PICTURE((byte) 0x01);

	private final byte value;

	SnapshotType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}