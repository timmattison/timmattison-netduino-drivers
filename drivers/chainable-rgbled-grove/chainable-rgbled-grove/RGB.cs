using System;
using Microsoft.SPOT;

namespace chainable_rgbled_grove
{
    public class RGB
    {
        public byte red;
        public byte green;
        public byte blue;

        public RGB(byte red, byte green, byte blue)
        {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
