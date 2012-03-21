package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

// This code refers to page numbers in the camera module's documentation.  Those page numbers are from the document
//   "C328-7640 JPEG Compression Module" [2005-08-19] available from http://www.kosmodrom.com.ua/pdf/C328-7640.pdf
public abstract class Command {
	public static final int COMMAND_SIZE = 6;
	protected static final int PARAMETER_SIZE = 4;
	protected static final byte PREFIX_BYTE = (byte) 0xAA;

	protected static final int PARAMETER_1_BYTE = 0;
	protected static final int PARAMETER_2_BYTE = 1;
	protected static final int PARAMETER_3_BYTE = 2;
	protected static final int PARAMETER_4_BYTE = 3;

	/**
	 * Returns the ID number from the C328's ID number column on page 4 without the leading 0xAA
	 * 
	 * @return
	 */
	protected abstract byte getIdNumber();

	/**
	 * Returns the four bytes corresponding to the parameter columns on page 4
	 * 
	 * @return
	 * @throws MissingParameterException
	 */
	protected abstract byte[] getParameterBytes() throws MissingParameterException;

	/**
	 * Returns the human-readable name of the command. This is used for printing error messages.
	 * 
	 * @return
	 */
	public abstract String getCommandName();

	public byte[] getBytes() throws MissingParameterException {
		// Build the bytes for the command
		byte[] bytes = new byte[COMMAND_SIZE];

		// The first byte is the prefix
		bytes[0] = PREFIX_BYTE;

		// The second byte is the command ID
		bytes[1] = getIdNumber();

		// Now get the parameter bytes
		byte[] parameterBytes = getParameterBytes();

		// Are they the right size?
		if (parameterBytes.length != PARAMETER_SIZE) {
			// No, throw an exception
			throw new IllegalStateException("Expected " + PARAMETER_SIZE + " parameter bytes, received " + parameterBytes.length);
		}

		// Copy the parameter bytes
		bytes[2] = parameterBytes[0];
		bytes[3] = parameterBytes[1];
		bytes[4] = parameterBytes[2];
		bytes[5] = parameterBytes[3];

		// Return them to the caller
		return bytes;
	}

	/**
	 * Formats a byte for printing as a two character, zero padded hex string
	 * 
	 * @param input
	 * @return
	 */
	public static String formatByte(byte input) {
		return String.format("0x%02x", input);
	}
}
