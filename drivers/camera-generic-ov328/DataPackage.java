package com.timmattison.firmware.serial.peripherals.camera.c328;

import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.Command;

public class DataPackage {
	/**
	 * The package must be at least 4 bytes to get the ID and data size
	 */
	private static final int MINIMUM_SIZE = 4;

	private static final int PACKAGE_SIZE_LOW_BYTE = 2;
	private static final int PACKAGE_SIZE_HIGH_BYTE = 3;
	private static final int PACKAGE_ID_LOW_BYTE = 0;
	private static final int PACKAGE_ID_HIGH_BYTE = 1;

	/**
	 * Where the image data starts in the package
	 */
	private static final int IMAGE_DATA_START_BYTE = 4;

	/**
	 * The ID of the package
	 */
	private int id;

	/**
	 * The total size of the package with header information (the sum of the package size bytes plus six)
	 */
	private int packageSize;

	/**
	 * The actual image data
	 */
	private byte[] imageData;

	/**
	 * A basic checksum
	 */
	private byte verifyCode = 0;

	public DataPackage(byte[] bytes) {
		// The data package is laid out like this:
		//   ID - Two (2) bytes
		//   Package size - Two (2) bytes
		//   Image data - (Package size -  6) bytes
		//   Verify code - Two (2) bytes
		//
		//   High byte of verify code is always zero
		//   Low byte of verify code is the sum of all of the preceeding (package_size - 2) bytes
		//   We return 0xAA 0x0E 0x00 0x00 LL HH
		//   LL = Low byte of ID
		//   HH = High byte of ID

		// Is there any data?
		if (bytes == null) {
			// No, throw an exception
			throw new IllegalStateException("Data package cannot be blank");
		}

		// Is the data at least the minimum size?
		if (bytes.length < MINIMUM_SIZE) {
			// No, throw an exception
			throw new IllegalStateException("Data package must be at least 4 bytes");
		}

		// Set the ID and the data size
		setId(bytes);
		setPackageSize(bytes);

		// Do we have the expected number of bytes?
		if (bytes.length != packageSize) {
			// No, throw an exception
			throw new IllegalStateException("Expected " + packageSize + " bytes, received " + bytes.length + " bytes");
		}

		// Extract the bytes into the image data array
		setImageData(bytes);

		// Is the high byte of the verify code zero?
		if (bytes[getVerifyCodeHighByte()] != 0) {
			// No, throw an exception
			throw new IllegalStateException("High byte of verify code was " + Command.formatByte(bytes[getVerifyCodeHighByte()]) + ", expected 0x00");
		}

		// Does the low byte of the verify code match what we calculated?
		if (bytes[getVerifyCodeLowByte()] != verifyCode) {
			// No, throw an exception
			throw new IllegalStateException("Low byte of verify code was " + Command.formatByte(bytes[getVerifyCodeLowByte()]) + ", expected " + Command.formatByte(verifyCode));
		}
	}

	private int getVerifyCodeLowByte() {
		return packageSize - 2;
	}

	private int getVerifyCodeHighByte() {
		return packageSize - 1;
	}

	private int getDataSize() {
		return (packageSize - Command.COMMAND_SIZE);
	}

	private void setImageData(byte[] bytes) {
		// Initialize the verify code
		verifyCode = 0;

		// Allocate space for the image data
		imageData = new byte[getDataSize()];

		// Update the verify code with the data before the image data
		for (int loop = 0; loop < IMAGE_DATA_START_BYTE; loop++) {
			verifyCode += bytes[loop];
		}

		// Copy all of the image data while updating the verify code
		for (int loop = 0; loop < getDataSize(); loop++) {
			// Get the current byte
			byte currentByte = bytes[IMAGE_DATA_START_BYTE + loop];

			// Store it in the image data array
			imageData[loop] = currentByte;

			// Add this byte into the verify code
			verifyCode += currentByte;
		}
	}

	private void setPackageSize(byte[] bytes) {
		packageSize = (bytes[PACKAGE_SIZE_LOW_BYTE] & 0xFF) | ((bytes[PACKAGE_SIZE_HIGH_BYTE] << 8) & 0xFF00);
		packageSize += 6;
	}

	private void setId(byte[] bytes) {
		id = (bytes[PACKAGE_ID_LOW_BYTE] & 0xFF) | ((bytes[PACKAGE_ID_HIGH_BYTE] << 8) & 0xFF00);
	}

	public void copyImageData(byte[] image, int outputOffset) {
		for (int loop = 0; loop < getDataSize(); loop++) {
			image[outputOffset + loop] = imageData[loop];
		}
	}
}
