using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using chainable_rgbled_grove;

namespace triple_rgb_led_2
{
    public class Program
    {
        public static void Main()
        {
            // Create three colors, all LEDs off
            RGB first = new RGB(0, 0, 0);
            RGB second = new RGB(0, 0, 0);
            RGB third = new RGB(0, 0, 0);

            int counter = 0;
            int whichLed;

            // Put them into an array
            RGB[] colors = { first, second, third };

            // Use D6 for CIN and D7 for DIN (Grove Base Shield v1.2 header #6)
            OutputPort cin = new OutputPort(Pins.GPIO_PIN_D6, false);
            OutputPort din = new OutputPort(Pins.GPIO_PIN_D7, false);

            // Create our chainable RGB led object using the ports from above
            ChainableRGBLed leds = new ChainableRGBLed(cin, din);

            // Loop forever
            while (true)
            {
                // Set the colors
                leds.setColors(colors);

                // Increment the counter
                counter += 8;

                // Determine which LED we're cycling
                whichLed = counter >> 8;

                if (whichLed == 0)
                {
                    // Make the first LED red and turn off the second and third
                    first.red = (byte) (counter & 0xFF);
                    second.green = 0;
                    third.blue = 0;
                }
                else if (whichLed == 1)
                {
                    // Make the second LED green and turn off the first and third
                    first.red = 0;
                    second.green = (byte) (counter & 0xFF);
                    third.blue = 0;
                }
                else if (whichLed == 2)
                {
                    // Make the third LED blue and turn off the first and second
                    first.red = 0;
                    second.green = 0;
                    third.blue = (byte) (counter & 0xFF);
                }

                // Make sure the counter rolls properly after it runs through all three LEDs (256 * 3)
                if (counter >= 768)
                {
                    // We've looped through all three LEDs, start over
                    counter = 0;
                }
            }
        }
    }
}
