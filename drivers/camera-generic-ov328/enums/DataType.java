package com.timmattison.firmware.serial.peripherals.camera.c328.enums;

import java.util.HashMap;

/**
 * The same as the picture type
 * 
 * @author tim
 */
public enum DataType {
	SNAPSHOT_PICTURE((byte) 0x01), PREVIEW_PICTURE((byte) 0x02), JPEG_PREVIEW_PICTURE((byte) 0x05);

	private static HashMap<Byte, DataType> fromByteMap = null;

	private final byte value;

	DataType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public static DataType fromByte(byte byteValue) {
		// Has the reverse map been built yet?
		if (fromByteMap == null) {
			// No, build it
			fromByteMap = new HashMap<Byte, DataType>();

			// Loop through all of the values
			for (DataType current : DataType.values()) {
				// Map this value's character representation to it
				fromByteMap.put(current.getValue(), current);
			}
		}

		// Return the match, if one exists
		return fromByteMap.get(byteValue);
	}
}