using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using System.IO.Ports;
using TimMattison;

namespace rfid_reader_parallax_28140
{
    public class Program : IRFIDReceiver
    {
        public static void Main()
        {
            Program program = new Program();
            RFIDReader rfidReader = new RFIDReader("COM1", Pins.GPIO_PIN_D4, program);

            // Sleep forever
            Thread.Sleep(Timeout.Infinite);
        }

        void IRFIDReceiver.idRead(byte[] id)
        {
            Debug.Print("Successful read");
        }

        void IRFIDReceiver.readFailed()
        {
            Debug.Print("Read failed");
        }
    }
}
