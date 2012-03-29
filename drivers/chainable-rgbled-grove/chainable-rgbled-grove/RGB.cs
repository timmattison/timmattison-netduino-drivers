using System;
using Microsoft.SPOT;

namespace chainable_rgbled_grove
{
    public class RGB
    {
        public int red;
        public int green;
        public int blue;

        public RGB(int red, int green, int blue)
        {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
