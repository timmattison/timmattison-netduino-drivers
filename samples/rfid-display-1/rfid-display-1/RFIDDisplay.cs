using System;
using Microsoft.SPOT;
using TimMattison;
using character_lcd_sparkfun_00709;

namespace rfid_display_1
{
    class RFIDDisplay : IRFIDReceiver
    {
        LCDDisplay ourLcdDisplay;
        byte[] lastId = null;
        int idRepeatCount = 0;

        public RFIDDisplay(LCDDisplay lcdDisplay)
        {
            // Get a copy of the LCD they set up
            ourLcdDisplay = lcdDisplay;

            // Is the LCD set up?
            if (ourLcdDisplay == null)
            {
                // No, throw an exception
                throw new NotSupportedException("The LCD object may not be NULL.");
            }

            // Clear the display when we first start
            ourLcdDisplay.clearDisplay();
        }

        public void idRead(byte[] id)
        {
            // Convert the ID into octets
            String octet0 = byteToHexString(id[0]);
            String octet1 = byteToHexString(id[1]);
            String octet2 = byteToHexString(id[2]);
            String octet3 = byteToHexString(id[3]);
            String octet4 = byteToHexString(id[4]);
            String octet5 = byteToHexString(id[5]);
            String octet6 = byteToHexString(id[6]);
            String octet7 = byteToHexString(id[7]);
            String octet8 = byteToHexString(id[8]);
            String octet9 = byteToHexString(id[9]);

            // Clear the LCD display
            ourLcdDisplay.clearDisplay();

            // Write the first five octets on the first line separated by colons
            ourLcdDisplay.writeString(octet0);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet1);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet2);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet3);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet4);

            // Move to the next line
            ourLcdDisplay.setCursorPosition(1, 0);

            // Write the last five octets on the first line separated by colons
            ourLcdDisplay.writeString(octet5);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet6);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet7);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet8);
            ourLcdDisplay.writeString(":");
            ourLcdDisplay.writeString(octet9);

            // Is this the same ID that we last saw?
            if (!byteArraysEqual(lastId, id))
            {
                // No, clear the counter and update the last ID
                idRepeatCount = 0;
                copyId(id);
            }
            else
            {
                // Yes, increment the counter
                idRepeatCount++;
            }

            // Display the repeat count on the right side of the display
            string idRepeatCountString = byteToHexString((byte) idRepeatCount);
            ourLcdDisplay.setCursorPosition(0, 15);
            ourLcdDisplay.writeString(idRepeatCountString[0] + "");
            ourLcdDisplay.setCursorPosition(1, 15);
            ourLcdDisplay.writeString(idRepeatCountString[1] + "");
        }

        private bool byteArraysEqual(byte[] lastId, byte[] id)
        {
            // Is either array NULL?
            if ((lastId == null) || (id == null))
            {
                // Yes, never consider NULL equal to anything else
                return false;
            }

            // Are their lengths the same?
            if (lastId.Length != id.Length)
            {
                // No, they cannot be equal
                return false;
            }

            // Compare byte by byte
            for (int loop = 0; loop < lastId.Length; loop++)
            {
                // Are these bytes equal?
                if (lastId[loop] != id[loop])
                {
                    // No, early out
                    return false;
                }
            }

            // Both are non-NULL, the same length, with all bytes equal
            return true;
        }

        void copyId(byte[] id)
        {
            // Has the last ID been initialized?
            if (lastId == null)
            {
                // No, allocate space for it
                lastId = new byte[id.Length];
            }

            // Copy byte by byte
            for (int loop = 0; loop < id.Length; loop++)
            {
                lastId[loop] = id[loop];
            }
        }

        private string byteToHexString(byte input)
        {
            char first;
            char second;

            // Get the upper nibble and turn it into a character
            int inputDiv16 = input / 16;
            first = nibbleToHexChar(inputDiv16);

            // Get the lower nibble and turn it into a character
            int inputMod16 = input % 16;
            second = nibbleToHexChar(inputMod16);

            // Convert the two values to a string.  The "" in the middle forces the compiler
            //   to output a string instead of adding the int values of first and second.
            string output = first + "" + second;

            return output;
        }

        private char nibbleToHexChar(int nibble)
        {
            // Is the value less than 10?
            if (nibble < 10)
            {
                // Yes, it is a digit
                return (char)(nibble + '0');
            }
            else
            {
                // No, it is a hex character.  Adjust accordingly.
                return (char)(nibble - 10 + 'A');
            }
        }

        public void readFailed()
        {
            Debug.Print("Bad read");
        }
    }
}
