package com.timmattison.firmware.serial.peripherals.camera.c328;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;

import com.timmattison.firmware.exceptions.serial.SerialPortException;
import com.timmattison.firmware.exceptions.serial.SerialPortNotFoundException;
import com.timmattison.firmware.exceptions.serial.SerialPortReadTimeoutException;
import com.timmattison.firmware.serial.WrappedSerialPort;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.AckCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.Command;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.DataAckCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.DataCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.GetPictureCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.InitialCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.NackCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.SetPackageSizeCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.SnapshotCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.commandset.SyncCommand;
import com.timmattison.firmware.serial.peripherals.camera.c328.enums.JPEGResolution;
import com.timmattison.firmware.serial.peripherals.camera.c328.enums.PictureType;
import com.timmattison.firmware.serial.peripherals.camera.c328.enums.SnapshotType;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.IncorrectParameterException;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.MissingParameterException;
import com.timmattison.firmware.serial.peripherals.camera.c328.exceptions.UnexpectedResponseException;

public class SerialCameraC328 extends WrappedSerialPort {
	private static final byte[] SYNC_COMMAND = { (byte) 0xAA, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

	public SerialCameraC328(String portName, int baudRate) throws SerialPortNotFoundException, PortInUseException, IOException, UnsupportedCommOperationException {
		super(portName, baudRate);
	}

	private void sync() throws InterruptedException, SerialPortException {
		// Send the sync command many times (the manual says a maximum of 60 times on page 8)
		for (int loop = 0; loop < 59; loop++) {
			Thread.sleep(100);
			write(SYNC_COMMAND);
		}
	}

	/**
	 * Returns true if a C328 camera is detected.
	 * 
	 * @return
	 * @throws MissingParameterException
	 * @throws UnexpectedResponseException
	 * @throws SerialPortException
	 * @throws InterruptedException
	 */
	public boolean isCameraPresent() throws MissingParameterException, UnexpectedResponseException, SerialPortException, InterruptedException {
		// XXX - If the camera is in a bad state (ie. in the middle of returning a picture) it may make sense to
		// XXX -   try to send an immediate reset command before the SYNC to be extra sure that we're dealing with
		// XXX -   a clean camera

		// Try up to two times to connect to the camera
		int maxTries = 2;
		int currentTry = 0;

		// Create an ACK ACK command
		AckCommand ackAckCommand = null;

		// Create a variable to hold the bytes we read in the loop
		byte[] bytes = null;

		// Loop while we haven't run out of tries and we haven't received anything yet
		while ((currentTry < maxTries) && (bytes == null)) {
			try {
				// Send the SYNC command to the camera to wake it up
				sync();

				// Wait up to 1 second for a command to come back
				bytes = waitForBytes(1, Command.COMMAND_SIZE);
			} catch (SerialPortReadTimeoutException e) {
				// Increment the try counter
				currentTry++;

				// Didn't get any data, can we try again?
				if (currentTry >= maxTries) {
					// No, tried too many times.  Give up.
					return false;
				}
			}
		}

		// Start checking for a sync ACK
		AckCommand syncAckCommand = null;

		try {
			// Did we receive a sync ACK?
			syncAckCommand = new AckCommand(bytes);

			// XXX - Could check a bit more thoroughly here.  Theoretically we could be getting an ACK for something
			// XXX -   other than sync which would be invalid.
		} catch (IncorrectParameterException e) {
			try {
				// No, is it a NACK?
				NackCommand nackCommand = new NackCommand(bytes);

				// Yes, it was a NACK.  Just give up.
				return false;
			} catch (IncorrectParameterException e1) {
				// No, throw an exception
				throw new UnexpectedResponseException(bytes, "Neither an ACK nor a NACK was received when syncing the camera");
			}
		}

		// Create an acknowledgment for the sync ACK command
		ackAckCommand = new AckCommand(syncAckCommand);

		// Wait up to 1 second for the next command to come back
		bytes = waitForBytes(1, Command.COMMAND_SIZE);

		// Did we get any data?
		if (bytes == null) {
			// No, give up
			return false;
		}

		try {
			// Are these bytes a SYNC command?
			SyncCommand receivedSyncCommand = new SyncCommand(bytes);
		} catch (IncorrectParameterException e) {
			// No, throw an exception
			throw new UnexpectedResponseException(bytes, "Expected a SYNC command but didn't receive one");
		}

		// Received the SYNC, send the ACK ACK to the camera
		write(ackAckCommand.getBytes());

		// Camera looks good
		return true;
	}

	private void sendCommandAndValidateACK(Command command) throws UnexpectedResponseException, SerialPortException, MissingParameterException {
		// Send the command to the camera
		write(command.getBytes());

		// Wait up to 1 second for an ACK to come back
		byte[] bytes = waitForBytes(1, Command.COMMAND_SIZE);

		try {
			// Did we receive an ACK?
			AckCommand ackCommand = new AckCommand(bytes);

			// Yes, does the command ID match?
			if (!ackCommand.isAckFor(command)) {
				// No, throw an exception
				throw new UnexpectedResponseException(bytes, "An ACK was received that wasn't for the " + command.getCommandName() + " command");
			}
		} catch (IncorrectParameterException e) {
			// No, throw an exception
			throw new UnexpectedResponseException(bytes, "An ACK was not received after sending the " + command.getCommandName() + " command");
		}
	}

	public byte[] takePicture() throws UnexpectedResponseException, SerialPortException, MissingParameterException, IncorrectParameterException {
		// Use 512 bytes as our package size
		int packageSize = 512;

		// In each package we will receive header bytes that are a data command and then the rest are data bytes
		int dataSize = packageSize - Command.COMMAND_SIZE;

		// Send the initial command
		InitialCommand initialCommand = new InitialCommand(JPEGResolution.JPEG_IMAGE_640x480);
		sendCommandAndValidateACK(initialCommand);

		// Send the set package size command so we get packageSize byte chunks
		SetPackageSizeCommand setPackageSizeCommand = new SetPackageSizeCommand(packageSize);
		sendCommandAndValidateACK(setPackageSizeCommand);

		// Send the snapshot command
		SnapshotCommand snapshotCommand = new SnapshotCommand(SnapshotType.COMPRESSED_PICTURE);
		sendCommandAndValidateACK(snapshotCommand);

		// Send the get picture command
		GetPictureCommand getPictureCommand = new GetPictureCommand(PictureType.SNAPSHOT_PICTURE);
		sendCommandAndValidateACK(getPictureCommand);

		// Wait up to 1 second for a data command to come back
		byte[] bytes = waitForBytes(1, Command.COMMAND_SIZE);

		DataCommand dataCommand = new DataCommand(bytes);

		// Got the data command, determine how many packages we will receive (see page 14 for this formula).
		int packagesToReceive = dataCommand.getLength() / dataSize;

		// Don't forget the last few bytes due to rounding.  Do we need to add a package?
		if ((dataCommand.getLength() % dataSize) != 0) {
			// Yes, there will be one last package to get the remaining bytes
			packagesToReceive += 1;
		}

		// Allocate enough memory for the entire image
		byte[] image = new byte[dataCommand.getLength()];

		DataAckCommand dataAckCommand;

		// Loop and receive all of the packages
		for (int loop = 0; loop < packagesToReceive; loop++) {
			// Send a data ACK to tell the device to send the next package
			dataAckCommand = new DataAckCommand(loop);
			write(dataAckCommand.getBytes());

			int bytesToWaitFor = packageSize;

			// Is this the last package?
			if (loop == (packagesToReceive - 1)) {
				// Yes, it may be less than packageSize bytes
				bytesToWaitFor = (image.length % dataSize) + Command.COMMAND_SIZE;
			}

			// Wait up to 1 second for a data package to come back
			bytes = waitForBytes(1, bytesToWaitFor);

			DataPackage dataPackage = new DataPackage(bytes);

			// Where does this data chunk start in our image byte array?
			int imageDataOffset = dataSize * loop;

			// Now take the data and append it to the image bytes
			dataPackage.copyImageData(image, imageDataOffset);
		}

		// Send the final (special) data ACK command
		dataAckCommand = new DataAckCommand();
		write(dataAckCommand.getBytes());

		// Return the bytes to the caller
		return image;
	}
}