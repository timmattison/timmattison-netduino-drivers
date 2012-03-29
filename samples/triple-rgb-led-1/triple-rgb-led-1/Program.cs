using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using chainable_rgbled_grove;

namespace triple_rgb_led_1
{
    public class Program
    {
        // A list of prime numbers that we use to XOR into our color values.  This was done to try to make the colors look random.
        private static byte[] increments = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251 };

        // A counter that makes sure each time we XOR we're using a different number from the prime number list
        private static int counter = 1;

        public static void Main()
        {
            // Create three colors, all LEDs off
            RGB first = new RGB(0, 0, 0);
            RGB second = new RGB(0, 0, 0);
            RGB third = new RGB(0, 0, 0);

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

                // Randomize each color
                randomize(first);
                randomize(second);
                randomize(third);
            }
        }

        private static void randomize(RGB color)
        {
            do
            {
                // Increment the counter to get the next number and keep it within the bounds of the table
                counter++;
                counter = counter % increments.Length;

                // XOR a new prime with the red value
                color.red = (byte) (color.red ^ increments[counter]);

                // Increment the counter to get the next number and keep it within the bounds of the table
                counter++;
                counter = counter % increments.Length;

                // XOR a new prime with the green value
                color.green = (byte) (color.green ^ increments[counter]);

                // Increment the counter to get the next number and keep it within the bounds of the table
                counter++;
                counter = counter % increments.Length;

                // XOR a new prime with the blue value
                color.blue = (byte) (color.blue ^ increments[counter]);
            } while ((color.red == 0) && (color.green == 0) && (color.blue == 0));
            // Repeat this loop if all of the colors end up being zero (avoids a strobing effect)
        }
    }
}
