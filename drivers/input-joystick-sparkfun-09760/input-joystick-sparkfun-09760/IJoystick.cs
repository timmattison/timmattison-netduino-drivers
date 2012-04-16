using System;
using Microsoft.SPOT;

namespace TimMattison
{
    interface IJoystick
    {
        /// <summary>
        /// Called when button #3 (pin D3) is pressed
        /// </summary>
        void button3Pressed();

        /// <summary>
        /// Called when button #4 (pin D4) is pressed
        /// </summary>
        void button4Pressed();

        /// <summary>
        /// Called when button #5 (pin D5) is pressed
        /// </summary>
        void button5Pressed();

        /// <summary>
        /// Called when button #6 (pin D6) is pressed
        /// </summary>
        void button6Pressed();
    }
}
