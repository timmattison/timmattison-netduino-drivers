using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;
using System.IO.Ports;

namespace TimMattison
{
    public interface IRFIDReceiver
    {
        /// <summary>
        /// Called when an RFID reader finishes reading a card successfully
        /// </summary>
        /// <param name="id">The bytes that represent a particular card's unique ID</param>
        void idRead(byte[] id);

        /// <summary>
        /// Called when an RFID reader fails to read a card
        /// </summary>
        void readFailed();
    }
}