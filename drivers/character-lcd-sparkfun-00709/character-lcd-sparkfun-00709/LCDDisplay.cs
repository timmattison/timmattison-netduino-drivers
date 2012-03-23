using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using System.IO.Ports;
using System.Text;

namespace character_lcd_sparkfun_00709
{
    public class LCDDisplay
    {
        public enum DISPLAY_WIDTH { x16_CHARACTERS = 15, x20_CHARACTERS = 19 };
        public enum DISPLAY_HEIGHT { x1_LINES = 0, x2_LINES = 1, x3_LINES = 2, x4_LINES = 3};

        static readonly byte[] CLEAR_DISPLAY_COMMAND = { 0x01 };
        static readonly byte[] MOVE_CURSOR_RIGHT_ONE_COMMAND = { 0x14 };
        static readonly byte[] MOVE_CURSOR_LEFT_ONE_COMMAND = { 0x10 };
        static readonly byte[] SCROLL_RIGHT_COMMAND = { 0x1C };
        static readonly byte[] SCROLL_LEFT_COMMAND = { 0x18 };
        static readonly byte[] TURN_VISUAL_DISPLAY_ON_COMMAND = { 0x0C };
        static readonly byte[] TURN_VISUAL_DISPLAY_OFF_COMMAND = { 0x08 };
        static readonly byte[] UNDERLINE_CURSOR_ON_COMMAND = { 0x0E };
        static readonly byte[] UNDERLINE_CURSOR_OFF_COMMAND = { 0x0C };
        static readonly byte[] BLINKING_BOX_CURSOR_ON_COMMAND = { 0x0D };
        static readonly byte[] BLINKING_BOX_CURSOR_OFF_COMMAND = { 0x0C };
        static readonly byte[] SPECIAL_COMMAND = { 0x7C };
        static readonly byte[] START_COMMAND_BYTE = { 0xFE };

        const int SET_CURSOR_POSITION_BIT = 0x80;

        const int MAX_BRIGHTNESS = 30;
        const int MIN_BRIGHTNESS = 0;

        const int MIN_INTERNAL_BRIGHTNESS = 128;
        const int MAX_INTERNAL_BRIGHTNESS = 157;

        private DISPLAY_WIDTH ourDisplayWidth;
        private DISPLAY_HEIGHT ourDisplayHeight;

        private SerialPort serial;

        public LCDDisplay(DISPLAY_WIDTH displayWidth, DISPLAY_HEIGHT displayHeight, String portName)
        {
            // Initialize the display width and height
            ourDisplayWidth = displayWidth;
            ourDisplayHeight = displayHeight;

            // Open the requested COM port at 9600 bps, no parity, 8 data bits, 1 stop bit
            serial = new SerialPort(portName, 9600, Parity.None, 8, StopBits.One);
            serial.Open();
        }

        /// <summary>
        /// Writes a string to the LCD
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        /// <param name="stringToWrite">The string to write to the LCD</param>
        public void writeString(string stringToWrite)
        {
            // Convert the string 
            byte[] bytes = Encoding.UTF8.GetBytes(stringToWrite);
            serial.Write(bytes, 0, bytes.Length);
        }

        /// <summary>
        /// Sets the LCD brightness
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        /// <param name="brightnessLevel">A value from 0 to 30 indicating the desired brightness level.  0 turns off the backlight, 30 is maximum brightness.</param>
        public void setBrightness(int brightnessLevel)
        {
            // Clamp the brightness level to values that work for this device
            if (brightnessLevel > MAX_BRIGHTNESS)
            {
                brightnessLevel = MAX_BRIGHTNESS;
            }
            else if (brightnessLevel < MIN_BRIGHTNESS)
            {
                brightnessLevel = MIN_BRIGHTNESS;
            }

            // Adjust it to the values that the display expects
            brightnessLevel += MIN_INTERNAL_BRIGHTNESS;

            serial.Write(SPECIAL_COMMAND, 0, SPECIAL_COMMAND.Length);
            writeByte((byte)brightnessLevel);
        }

        /// <summary>
        /// Clears the entire display and moves the cursor to the upper left corner (position 0, 0)
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void clearDisplay()
        {
            startCommand();
            serial.Write(CLEAR_DISPLAY_COMMAND, 0, CLEAR_DISPLAY_COMMAND.Length);
        }

        /// <summary>
        /// Moves the cursor right by one character
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void moveCursorRightOne()
        {
            startCommand();
            serial.Write(MOVE_CURSOR_RIGHT_ONE_COMMAND, 0, MOVE_CURSOR_RIGHT_ONE_COMMAND.Length);
        }

        /// <summary>
        /// Moves the cursor left by one character
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void moveCursorLeftOne()
        {
            startCommand();
            serial.Write(MOVE_CURSOR_LEFT_ONE_COMMAND, 0, MOVE_CURSOR_LEFT_ONE_COMMAND.Length);
        }

        /// <summary>
        /// Scrolls all of the text on the display to the right by one character.  NOTE: This changes the coordinate system of the device
        ///   so that the origin (0, 0) is no longer in the same place.  Be careful when using this for scrolling since this can get
        ///   confusing.  To reset the coordinate system call clearDisplay.
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void scrollRight()
        {
            startCommand();
            serial.Write(SCROLL_RIGHT_COMMAND, 0, SCROLL_RIGHT_COMMAND.Length);
        }

        /// <summary>
        /// Scrolls all of the text on the display to the left by one character.  NOTE: This changes the coordinate system of the device
        ///   so that the origin (0, 0) is no longer in the same place.  Be careful when using this for scrolling since this can get
        ///   confusing.  To reset the coordinate system call clearDisplay.
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void scrollLeft()
        {
            startCommand();
            serial.Write(SCROLL_LEFT_COMMAND, 0, SCROLL_LEFT_COMMAND.Length);
        }

        /// <summary>
        /// Turns the display on if it is currently off
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void turnVisualDisplayOn()
        {
            startCommand();
            serial.Write(TURN_VISUAL_DISPLAY_ON_COMMAND, 0, TURN_VISUAL_DISPLAY_ON_COMMAND.Length);
        }

        /// <summary>
        /// Turns the display off if it is currently on
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void turnVisualDisplayOff()
        {
            startCommand();
            serial.Write(TURN_VISUAL_DISPLAY_OFF_COMMAND, 0, TURN_VISUAL_DISPLAY_OFF_COMMAND.Length);
        }

        /// <summary>
        /// Turns on the underline cursor if it is not already on
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void underlineCursorOn()
        {
            startCommand();
            serial.Write(UNDERLINE_CURSOR_ON_COMMAND, 0, UNDERLINE_CURSOR_ON_COMMAND.Length);
        }

        /// <summary>
        /// Turns off the underline cursor if it is already on
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void underlineCursorOff()
        {
            startCommand();
            serial.Write(UNDERLINE_CURSOR_OFF_COMMAND, 0, UNDERLINE_CURSOR_OFF_COMMAND.Length);
        }

        /// <summary>
        /// Turns on the blinking box cursor if it is not already on
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void blinkingBoxCursorOn()
        {
            startCommand();
            serial.Write(BLINKING_BOX_CURSOR_ON_COMMAND, 0, BLINKING_BOX_CURSOR_ON_COMMAND.Length);
        }

        /// <summary>
        /// Turns off the blinking box cursor if it is already on
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        public void blinkingBoxCursorOff()
        {
            startCommand();
            serial.Write(BLINKING_BOX_CURSOR_OFF_COMMAND, 0, BLINKING_BOX_CURSOR_OFF_COMMAND.Length);
        }

        /// <summary>
        /// Sets the cursor position on the serial LCD
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        /// <param name="lineNumber">The line number or Y-coordinate on the LCD (0 based)</param>
        /// <param name="characterNumber">The character number or X-coordinate on the LCD (0 based)</param>
        public void setCursorPosition(int lineNumber, int characterNumber)
        {
            // Is the line number negative?
            if (lineNumber < 0)
            {
                // Yes, just make it zero
                lineNumber = 0;
            }
            else
            {
                // No, is it too high for our display?
                if (lineNumber > (int) ourDisplayHeight)
                {
                    // Yes, clamp it
                    lineNumber = (int)ourDisplayHeight;
                }
            }

            // Is our character number negative?
            if (characterNumber < 0)
            {
                // Yes, just make it zero
                characterNumber = 0;
            }
            else
            {
                // No, is it off of our display?
                if (characterNumber > (int)ourDisplayWidth)
                {
                    // Yes, clamp it
                    characterNumber = (int)ourDisplayWidth;
                }
            }

            // Now calculate the position value
            byte positionValue;

            // What kind of display are we working with?
            if (ourDisplayWidth == DISPLAY_WIDTH.x16_CHARACTERS)
            {
                // This is a 16-character display, use that lookup table to determine the line offset
                switch (lineNumber)
                {
                    case 0: positionValue = 0; break;
                    case 1: positionValue = 64; break;
                    case 2: positionValue = 16; break;
                    case 3: positionValue = 80; break;
                    default: throw new NotSupportedException("Invalid line number on 16 character display [" + lineNumber + "]");
                }
            }
            else if (ourDisplayWidth == DISPLAY_WIDTH.x20_CHARACTERS)
            {
                // This is a 20-character display, use that lookup table to determine the line offset
                switch (lineNumber)
                {
                    case 0: positionValue = 0; break;
                    case 1: positionValue = 64; break;
                    case 2: positionValue = 20; break;
                    case 3: positionValue = 84; break;
                    default: throw new NotSupportedException("Invalid line number on 20 character display [" + lineNumber + "]");
                }
            }
            else
            {
                // We don't support this
                throw new NotSupportedException("Invalid display width [" + ourDisplayWidth + "]");
            }

            // Now add the character number
            positionValue += (byte) characterNumber;

            // Set the high bit
            positionValue |= SET_CURSOR_POSITION_BIT;

            // Now write the command and the position value
            startCommand();
            writeByte(positionValue);
        }

        /// <summary>
        /// Sends one byte (START_COMMAND_BYTE) to the serial LCD module so it knows the next byte must be treated
        /// as a command
        /// </summary>
        /// <param name="serial">The serial port to which the serial LCD is connected</param>
        private void startCommand()
        {
            serial.Write(START_COMMAND_BYTE, 0, START_COMMAND_BYTE.Length);
        }

        /// <summary>
        /// A helper function to write a single byte
        /// </summary>
        /// <param name="serial">The serial port to which the byte is written</param>
        /// <param name="byteToWrite">The byte to write</param>
        private void writeByte(byte byteToWrite)
        {
            byte[] byteArray = new byte[1];
            byteArray[0] = byteToWrite;

            serial.Write(byteArray, 0, 1);
        }
    }
}
