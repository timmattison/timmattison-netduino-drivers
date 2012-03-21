package com.timmattison.firmware.serial.peripherals.camera.c328.exceptions;

public class MissingParameterException extends C328Exception {
	private String commandName;
	private int parameterNumber;
	private String parameterName;

	public MissingParameterException(String commandName, int parameterNumber) {
		this.commandName = commandName;
		this.parameterNumber = parameterNumber;
	}

	public MissingParameterException(String commandName, int parameterNumber, String parameterName) {
		this.commandName = commandName;
		this.parameterNumber = parameterNumber;
		this.parameterName = parameterName;
	}

	public String getMessage() {
		String parameterInfo = "Parameter #" + parameterNumber;

		if (parameterName != null) {
			parameterInfo += " [" + parameterName + "]";
		}

		return parameterInfo + " must be specified for the " + commandName + " command";
	}

	public String getCommandName() {
		return commandName;
	}

	public int getParameterNumber() {
		return parameterNumber;
	}
}
