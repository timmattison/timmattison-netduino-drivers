package com.timmattison.firmware.serial.peripherals.camera.c328.exceptions;

import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.Command;

public class IncorrectParameterException extends C328Exception {
	private String commandName;
	private int parameterNumber;
	private byte expectedValue;
	private byte receivedValue;

	public IncorrectParameterException(String commandName, int parameterNumber, byte expectedValue, byte receivedValue) {
		this.commandName = commandName;
		this.parameterNumber = parameterNumber;
		this.expectedValue = expectedValue;
		this.receivedValue = receivedValue;
	}

	public String getMessage() {
		return "Parameter " + parameterNumber + " must be " + Command.formatByte(expectedValue) + " for the " + commandName + " command but " + Command.formatByte(receivedValue) + " was received";
	}

	public String getCommandName() {
		return commandName;
	}

	public int getParameterNumber() {
		return parameterNumber;
	}

	public int getExpectedValue() {
		return expectedValue;
	}

	public int getReceivedValue() {
		return receivedValue;
	}
}
