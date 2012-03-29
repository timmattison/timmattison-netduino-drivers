using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using System.IO.Ports;

namespace camera_generic_ov328
{
    public class Program
    {
        static SerialPort serial = null;
        static byte[] SYNC_COMMAND = { (byte) 0xAA, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

        public static void Main()
        {
            // Open the requested COM port at 9600 bps, no parity, 8 data bits, 1 stop bit
            serial = new SerialPort(SerialPorts.COM1, 9600, Parity.None, 8, StopBits.One);
            serial.Open();
            serial.DataReceived += new SerialDataReceivedEventHandler(serial_DataReceived);

            sync();
        }

        private static void serial_DataReceived(Object sender, SerialDataReceivedEventArgs e)
        {
            int a = 5;
            a++;
        }

        private static void sync()
        {
            for (int loop = 0; loop < 59; loop++)
            {
                Thread.Sleep(100);
                write(SYNC_COMMAND);
            }
        }

        private static void write(byte[] bytes)
        {
            serial.Write(bytes, 0, bytes.Length);
        }
    }
}
