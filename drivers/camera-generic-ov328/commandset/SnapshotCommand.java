package com.timmattison.firmware.serial.peripherals.camera.c328.commandset;

import com.timmattison.firmware.serial.peripherals.camera.c328.enums.SnapshotType;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;

public class SnapshotCommand extends Command {
	private static final String COMMAND_NAME = "SNAPSHOT";
	private static final byte ID = 0x05;
	private static final int SNAPSHOT_TYPE_BYTE = PARAMETER_1_BYTE;
	private static final int SKIP_FRAME_LOW_BYTE = PARAMETER_2_BYTE;
	private static final int SKIP_FRAME_HIGH_BYTE = PARAMETER_3_BYTE;

	protected SnapshotType snapshotType;
	protected int skipFrameCount;

	public SnapshotCommand(SnapshotType snapshotType) {
		this.snapshotType = snapshotType;
		this.skipFrameCount = 0;
	}

	public SnapshotCommand(SnapshotType snapshotType, int skipFrameCount) {
		this.snapshotType = snapshotType;
		this.skipFrameCount = skipFrameCount;
	}

	@Override
	protected byte getIdNumber() {
		return ID;
	}

	@Override
	protected byte[] getParameterBytes() throws MissingParameterException {
		// Is the snapshot type set?
		if (snapshotType == null) {
			// No, throw an exception
			throw new MissingParameterException(getCommandName(), 1, "snapshot type");
		}

		// Create an array to hold the data we're going to return
		byte[] parameterBytes = new byte[PARAMETER_SIZE];

		// Initialize the bytes
		for (int loop = 0; loop < PARAMETER_SIZE; loop++) {
			parameterBytes[loop] = 0x00;
		}

		// Set the snapshot type
		parameterBytes[SNAPSHOT_TYPE_BYTE] = snapshotType.getValue();

		// Set the skip frame count
		parameterBytes[SKIP_FRAME_LOW_BYTE] = (byte) (skipFrameCount & 0xFF);
		parameterBytes[SKIP_FRAME_HIGH_BYTE] = (byte) ((skipFrameCount >> 8) & 0xFF);

		// Return it to the caller
		return parameterBytes;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}
}
