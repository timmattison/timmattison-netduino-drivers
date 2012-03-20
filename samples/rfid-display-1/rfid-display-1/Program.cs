using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using TimMattison;
using character_lcd_sparkfun_00709;

namespace rfid_display_1
{
    public class Program
    {
        public static void Main()
        {
            // Set up the LCD display on COM2
            LCDDisplay lcdDisplay = new LCDDisplay(LCDDisplay.DISPLAY_WIDTH.x16_CHARACTERS, LCDDisplay.DISPLAY_HEIGHT.x2_LINES, SerialPorts.COM2);

            // Instantiate the RFID display controller
            RFIDDisplay rfidDisplay = new RFIDDisplay(lcdDisplay);

            // Set up the RFID reader using the RFID display controller to receive ID events
            RFIDReader rfidReader = new RFIDReader(SerialPorts.COM1, Pins.GPIO_PIN_D4, rfidDisplay);

            // Sleep forever, the entire program is event driven
            Thread.Sleep(Timeout.Infinite);
        }
    }
}
