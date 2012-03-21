package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.enums.DataType;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.IncorrectParameterException;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class DataCommand extends ParsableCommand {
	private static final String COMMAND_NAME = "DATA";
	private static final byte ID = 0x0A;

	public DataCommand() {
	}

	public DataCommand(byte[] bytes) throws IncorrectParameterException {
		super(bytes);
	}

	/**
	 * The type of data that is coming after this command
	 */
	private DataType dataType;

	/**
	 * The length of the data coming after this command
	 */
	private Integer length;

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() throws MissingParameterException {
		// Is the data type set?
		if (dataType == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 1, "data type");
		}

		// Is the length set?
		if (length == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 2, "length");
		}

		// Everything is set up, build up the byte array
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		parameterBytes[PARAMETER_1_BYTE] = dataType.getValue();
		parameterBytes[PARAMETER_2_BYTE] = (byte) (length & 0xFF);
		parameterBytes[PARAMETER_3_BYTE] = (byte) ((length >> 8) & 0xFF);
		parameterBytes[PARAMETER_4_BYTE] = (byte) ((length >> 16) & 0xFF);

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	protected void setParameter1(byte parameter1) {
		dataType = DataType.fromByte(parameter1);
	}

	private void initializeLengthIfNecessary() {
		if (length == null) {
			length = 0;
		}
	}

	@Override
	protected void setParameter2(byte parameter2) {
		initializeLengthIfNecessary();
		length |= (parameter2 & 0xFF);
	}

	@Override
	protected void setParameter3(byte parameter3) {
		initializeLengthIfNecessary();
		length |= ((parameter3 << 8) & 0xFF00);
	}

	@Override
	protected void setParameter4(byte parameter4) {
		initializeLengthIfNecessary();
		length |= ((parameter4 << 16) & 0xFF0000);
	}

	@Override
	protected void allParametersSet() {
	}

	public Integer getLength() {
		return length;
	}
}
