using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;

namespace chainable_rgbled_grove
{
    public class ChainableRGBLed
    {
        OutputPort cin;
        OutputPort din;

        public ChainableRGBLed(OutputPort cin, OutputPort din)
        {
            this.cin = cin;
            this.din = din;
        }

        public void setColors(RGB[] colors)
        {
            bool first = true;
            bool last = false;

            // Loop through each color
            foreach(RGB rgb in colors) {
                // Set it
                setColor(rgb, first, last);

                // Make sure first is no longer true
                first = false;
            }

            // We're done, send the end frame
            sendEndFrame();
        }

        private void setColor(RGB rgb, bool first, bool last)
        {
            setColor(rgb.red, rgb.green, rgb.blue, first, last);
        }

        private void setColor(byte red, byte green, byte blue, bool first, bool last)
        {
            // Is this the first color?
            if (first)
            {
                // Yes, send the start frame
                sendStartFrame();
            }
            else
            {
                // No, do nothing
            }

            // Send the flag bits
            sendFlagBits();

            // Send the colors
            sendColorData(red, green, blue);

            // Is this the last color?
            if (last)
            {
                // Yes, send the end frame
                sendEndFrame();
            }
            else
            {
                // No, do nothing
            }
        }

        private void sendBit(bool bit)
        {
            // Get DIN into the proper state
            din.Write(bit);

            // Set the clock high
            cin.Write(true);

            // Set the clock low
            cin.Write(false);
        }

        private void sendByte(byte data)
        {
            // Send the bits MSB first
            sendBit((data & 0x80) == 0x80);
            sendBit((data & 0x40) == 0x40);
            sendBit((data & 0x20) == 0x20);
            sendBit((data & 0x10) == 0x10);
            sendBit((data & 0x08) == 0x08);
            sendBit((data & 0x04) == 0x04);
            sendBit((data & 0x02) == 0x02);
            sendBit((data & 0x01) == 0x01);
        }

        private void sendStartFrame()
        {
            // The start frame is 32 bits of zeroes
            sendByte(0);
            sendByte(0);
            sendByte(0);
            sendByte(0);
        }

        private void sendEndFrame()
        {
            // The end frame is the same as the start frame
            sendStartFrame();
        }

        private void sendFlagBits()
        {
            // The flag bits are two 1s
            sendBit(true);
            sendBit(true);
        }

        private void sendColorData(byte red, byte green, byte blue)
        {
            // Send the inverse bits of the B7, B6, G7, G6, R7, R6
            sendBit((blue & 0x80) != 0x80);
            sendBit((blue & 0x40) != 0x40);
            sendBit((green & 0x80) != 0x80);
            sendBit((green & 0x40) != 0x40);
            sendBit((red & 0x80) != 0x80);
            sendBit((red & 0x40) != 0x40);

            // Send the actual colors
            sendByte((byte)blue);
            sendByte((byte)green);
            sendByte((byte)red);
        }
    }
}
