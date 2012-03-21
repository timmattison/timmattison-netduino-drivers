package com.timmattison.firmware.serial.peripherals.camera.c328.exceptions;

import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.Command;

public class UnexpectedResponseException extends C328Exception {
	private byte[] bytes;
	private String message;

	public UnexpectedResponseException(byte[] bytes) {
		this.bytes = bytes;
	}

	public UnexpectedResponseException(byte[] bytes, String message) {
		this.bytes = bytes;
		this.message = message;
	}

	public String getMessage() {
		// Did we get any bytes?
		if (bytes == null) {
			// No, just say that nothing was received
			return "No bytes received";
		} else {
			// Yes, print out a message and include the hex formatted bytes
			String separator = "";

			StringBuilder stringBuilder = new StringBuilder();

			// Was there an error message specified already?
			if (message == null) {
				// No, use a generic one
				stringBuilder.append("Unexpected byte pattern received [");
			} else {
				// Yes, include that message and an indicator that we are including the received bytes.
				stringBuilder.append(message);
				stringBuilder.append(".  Received [");
			}

			for (int loop = 0; loop < bytes.length; loop++) {
				stringBuilder.append(separator);
				stringBuilder.append(Command.formatByte(bytes[loop]));
				separator = ", ";
			}

			stringBuilder.append("]");

			return stringBuilder.toString();
		}
	}

	public byte[] getBytes() {
		return bytes;
	}
}
