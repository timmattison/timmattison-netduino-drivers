using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using TimMattison;

namespace TimMattison
{
    public class SparkfunJoystick1
    {
        public static void Main()
        {
            Joystick joystick = new Joystick(true);

            while (true)
            {
                Thread.Sleep(5000);

                Debug.Print("Button 3 state: " + joystick.button3State);
                Debug.Print("Button 4 state: " + joystick.button4State);
                Debug.Print("Button 5 state: " + joystick.button5State);
                Debug.Print("Button 6 state: " + joystick.button6State);
                Debug.Print("Joystick button state: " + joystick.joystickButtonState);
            }
        }
    }
}
