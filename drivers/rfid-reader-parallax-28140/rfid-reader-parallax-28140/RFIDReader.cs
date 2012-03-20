using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using System.IO.Ports;

namespace TimMattison
{
    public class RFIDReader
    {
        //
        // Static variables
        //

        // The RFID reader can only be in one of three states, waiting for a card ID to start,
        //   waiting for data (already started reading a card), and waiting for the end byte
        //   of a card so verify that the read is finished
        enum RFID_READER_STATE { WAITING_FOR_START, WAITING_FOR_DATA, WAITING_FOR_END };

        // All of our RFID cards have 10 byte IDs
        static int RFID_ID_LENGTH = 10;

        // This is a special value that we use to determine if our read position is invalid
        static int RFID_POSITION_INVALID = -1;

        // The first byte sent by the reader when an RFID tag is read is 10 (decimal)
        static byte RFID_START_BYTE = 10;

        // The last byte sent by the reader when an RFID tag is read is 13 (decimal)
        static byte RFID_END_BYTE = 13;

        // Start out indexing our array at our invalid position since we're not ready to read yet
        static int rfidIdPosition = RFID_POSITION_INVALID;

        // The COM ports that we can use for RFID readers
        static readonly String[] supportedComPorts = { SerialPorts.COM1, SerialPorts.COM2 };

        // The pins that COM port 1 uses
        static readonly Microsoft.SPOT.Hardware.Cpu.Pin[] com1Pins = { Pins.GPIO_PIN_D0, Pins.GPIO_PIN_D1 };

        // The pins that COM port 2 uses
        static readonly Microsoft.SPOT.Hardware.Cpu.Pin[] com2Pins = { Pins.GPIO_PIN_D2, Pins.GPIO_PIN_D3 };

        // An array we can use to determine what pins our port uses when we're scanning supportedComPorts for a match.  NOTE: These
        //   values must be in the same order as supportedComPorts
        static readonly Microsoft.SPOT.Hardware.Cpu.Pin[][] comPortPins = { com1Pins, com2Pins };

        // All of the pins we can use for the RFID reader's enable line
        static readonly Microsoft.SPOT.Hardware.Cpu.Pin[] allSupportedEnablePins = { Pins.GPIO_PIN_D0, Pins.GPIO_PIN_D1, Pins.GPIO_PIN_D2, Pins.GPIO_PIN_D3, Pins.GPIO_PIN_D4, Pins.GPIO_PIN_D5, Pins.GPIO_PIN_D6, Pins.GPIO_PIN_D7, Pins.GPIO_PIN_D0, Pins.GPIO_PIN_D0, Pins.GPIO_PIN_D8, Pins.GPIO_PIN_D9, Pins.GPIO_PIN_D10, Pins.GPIO_PIN_D11, Pins.GPIO_PIN_D12, Pins.GPIO_PIN_D13 };

        //
        // Instance variables
        //

        // All of the pins we can use with our current configuration for the RFID reader's enable line
        Microsoft.SPOT.Hardware.Cpu.Pin[] currentlySupportedEnablePins = null;

        // The serial port that we're using for this device
        SerialPort serial;

        // Create an array for a current byte so we don't have to keep converting from byte to array and back again
        byte[] currentByte = new byte[1];

        // Initialize our RFID reader state
        RFID_READER_STATE rfidReaderState = RFID_READER_STATE.WAITING_FOR_START;

        // Create an array to hold one RFID tag's ID
        byte[] rfidId = new byte[RFID_ID_LENGTH];

        // The RFID receiver that will receive our RFID messages
        IRFIDReceiver rfidReceiver;

        public RFIDReader(String portName, Microsoft.SPOT.Hardware.Cpu.Pin enablePin, IRFIDReceiver theirRfidReceiver)
        {
            // Verify that the port name makes sense

            // Is the port name NULL?
            if (portName == null)
            {
                // Yes, the port name can never be NULL.  Throw an exception.
                throw new NotSupportedException("The serial port name cannot be NULL");
            }

            bool validComPort = false;

            // Is the port name one of the supported COM ports?
            for (int comPortLoop = 0; (!validComPort) && (comPortLoop < supportedComPorts.Length); comPortLoop++)
            {
                // Is this the COM port they wanted to use?
                if (supportedComPorts[comPortLoop].Equals(portName))
                {
                    // Yes, remove its pins from the supported enable pins list
                    buildSupportedEnablePinList(comPortPins[comPortLoop]);

                    // Indicate that we found our COM port
                    validComPort = true;
                }
            }

            // Was the COM port valid?
            if (!validComPort)
            {
                // No, the port name must be either COM1 or COM2.  Throw an exception.
                throw new NotSupportedException("The serial port name must be the SerialPort.COM1 or SerialPort.COM2 constant.  " + portName + " is not supported.");
            }

            bool validEnablePin = false;

            // Is the enable pin one of the currently supported pins?
            for (int enablePinLoop = 0; (!validEnablePin) && (enablePinLoop < currentlySupportedEnablePins.Length); enablePinLoop++)
            {
                // Get the current pin
                Microsoft.SPOT.Hardware.Cpu.Pin currentPin = currentlySupportedEnablePins[enablePinLoop];

                // Is this a valid pin?
                if (currentPin.Equals(Microsoft.SPOT.Hardware.Cpu.Pin.GPIO_NONE))
                {
                    // No, do nothing
                }
                else
                {
                    // Yes, does it match?
                    if (currentPin.Equals(enablePin))
                    {
                        // Yes, this will work
                        validEnablePin = true;
                    }
                }
            }

            // Is the enable pin valid?
            if (!validEnablePin)
            {
                // No, throw an exception.
                throw new NotSupportedException("The enable pin isn't valid.  " + enablePin + " is not supported.");
            }

            // Set up the RFID receiver
            rfidReceiver = theirRfidReceiver;

            // Open the serial port at 2400 bps, no parity, 8 data bits, 1 stop bit
            serial = new SerialPort(portName, 2400, Parity.None, 8, StopBits.One);
            serial.Open();

            // Set up the event handler
            serial.DataReceived += new SerialDataReceivedEventHandler(serial_DataReceived);

            // Set up the enable pin, set it to off since the device is low active
            OutputPort enablePinPort = new OutputPort(enablePin, false);
        }

        void serial_DataReceived(object sender, SerialDataReceivedEventArgs e)
        {
            // Are there any bytes to read?
            if (serial.BytesToRead > 0)
            {
                // Yes, read in one byte
                serial.Read(currentByte, 0, 1);

                // Act on this byte depending on our state
                switch (rfidReaderState)
                {
                    case RFID_READER_STATE.WAITING_FOR_START:
                        // We are waiting for the start byte, is this the start byte?
                        if (currentByte[0] == RFID_START_BYTE)
                        {
                            // Yes, this is the start byte.  Initialize everything.

                            // Set the reader state to waiting for data
                            rfidReaderState = RFID_READER_STATE.WAITING_FOR_DATA;

                            // Move to the first position
                            rfidIdPosition = 0;

                            // Clear the existing data out
                            for (int loop = 0; loop < RFID_ID_LENGTH; loop++)
                            {
                                rfidId[loop] = 0x00;
                            }
                        }
                        else
                        {
                            // No start byte, do nothing
                        }

                        break;
                    case RFID_READER_STATE.WAITING_FOR_DATA:
                        // We are waiting for data

                        // Just put this into our array and move onto the next byte
                        rfidId[rfidIdPosition++] = currentByte[0];

                        // Is this as much data as we should expect?
                        if (rfidIdPosition == RFID_ID_LENGTH)
                        {
                            // Yes, move to the last state
                            rfidReaderState = RFID_READER_STATE.WAITING_FOR_END;
                        }
                        else
                        {
                            // No, do nothing
                        }

                        break;
                    case RFID_READER_STATE.WAITING_FOR_END:
                        // We are waiting for the end byte

                        // Is this the end byte?
                        if (currentByte[0] == RFID_END_BYTE)
                        {
                            // Yes, looks like we have a successful read.  Send an event to anyone listening.
                            rfidReceiver.idRead(rfidId);
                        }
                        else
                        {
                            // No, this isn't the end byte.  We have a failed read.  Send a failed read event to anyone listening.
                            rfidReceiver.readFailed();
                        }

                        // Move back into the waiting for start byte state
                        rfidReaderState = RFID_READER_STATE.WAITING_FOR_START;

                        break;
                    default:
                        // This should never happen
                        throw new NotSupportedException("Reader is in an invalid state");
                }
            }
            else
            {
                // No date, do nothing

                // Sleep for a bit so we don't just spin and waste energy
                Thread.Sleep(500);
            }
        }
    
        /// <summary>
        /// Builds a list of pins that can be used with the current configuration, removing those that cannot be used
        /// </summary>
        /// <param name="unavailablePins">A list of pins that are allocated to the COM port</param>
        private void buildSupportedEnablePinList(Cpu.Pin[] unavailablePins)
        {
            // Create our array and the array position
            currentlySupportedEnablePins = new Microsoft.SPOT.Hardware.Cpu.Pin[allSupportedEnablePins.Length];

            int position = 0;

            // Initialize the array
            for (int loop = 0; loop < currentlySupportedEnablePins.Length; loop++)
            {
                currentlySupportedEnablePins[loop] = Microsoft.SPOT.Hardware.Cpu.Pin.GPIO_NONE;
            }

            // Loop through all of the pins
            for (int outerLoop = 0; outerLoop < allSupportedEnablePins.Length; outerLoop++)
            {
                bool usable = true;
                Microsoft.SPOT.Hardware.Cpu.Pin currentPin = allSupportedEnablePins[outerLoop];

                // Loop through the unavailable pins
                for (int innerLoop = 0; (usable) && (innerLoop < unavailablePins.Length); innerLoop++)
                {
                    // Is this pin unusable?
                    if (unavailablePins[innerLoop].Equals(currentPin))
                    {
                        // Yes, mark it as unusable
                        usable = false;
                    }
                }

                // Was the pin usable?
                if (usable)
                {
                    // Yes, add it to the list
                    currentlySupportedEnablePins[position++] = currentPin;
                }
            }
        }

        /*
        private static void printId()
        {
            for (int loop = 0; loop < RFID_ID_LENGTH; loop++)
            {
                Debug.Print("Byte #" + loop + ": " + rfidId[loop]);
            }
        }
         */

        private static void printBytes(byte[] buffer)
        {
            // Is there any data?
            if (buffer == null)
            {
                // No, just return
                return;
            }

            String output = "";

            // Loop through the bytes and add each byte onto the end of the output buffer
            for (int loop = 0; loop < buffer.Length; loop++)
            {
                output += buffer[loop] + " ";
            }

            Debug.Print("CARD DATA: " + output);
        }
    }
}
