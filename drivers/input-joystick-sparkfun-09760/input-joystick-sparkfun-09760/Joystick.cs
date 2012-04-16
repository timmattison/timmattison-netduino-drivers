using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Microsoft.SPOT;
using Microsoft.SPOT.Hardware;
using SecretLabs.NETMF.Hardware;
using SecretLabs.NETMF.Hardware.NetduinoPlus;

namespace TimMattison
{
    public class Joystick
    {
        private bool internalInterruptMode;

        /// <summary>
        /// Indicates whether or not we are using interrupts or polling
        /// </summary>
        public bool interruptMode { get { return internalInterruptMode; } }

        private Port button3Port;
        private Port button4Port;
        private Port button5Port;
        private Port button6Port;

        private bool internalButton3State;
        private bool internalButton4State;
        private bool internalButton5State;
        private bool internalButton6State;

        public bool button3State { get { return getState(button3Port, internalButton3State); } }
        public bool button4State { get { return getState(button4Port, internalButton4State); } }
        public bool button5State { get { return getState(button5Port, internalButton5State); } }
        public bool button6State { get { return getState(button6Port, internalButton6State); } }

        private InterruptPort joystickButtonPort;
        private bool internalJoystickButtonState;

        public bool joystickButtonState { get { return getState(joystickButtonPort, internalJoystickButtonState); } }

        private AnalogInput verticalPositionInput;

        /// <summary>
        /// Returns a value between -100 and 100 indicating where the joystick is positioned in the vertical direction
        /// </summary>
        /// <returns></returns>
        public float verticalPosition { get { return getCoordinate(verticalPositionInput.Read()); } }

        private AnalogInput horizontalPositionInput;

        /// <summary>
        /// Returns a value between -100 and 100 indicating where the joystick is positioned in the horizontal direction
        /// </summary>
        /// <returns></returns>
        public float horizontalPosition { get { return getCoordinate(horizontalPositionInput.Read()); } }

        public Joystick(bool useInterrupts)
        {
            // Do they want to use interrupts to track button states?
            if (useInterrupts)
            {
                // Yes, mark that we are using interrupts
                internalInterruptMode = true;

                // Set up the interrupt ports
                button3Port = new InterruptPort(Pins.GPIO_PIN_D3, true, Port.ResistorMode.Disabled, Port.InterruptMode.InterruptEdgeBoth);
                button4Port = new InterruptPort(Pins.GPIO_PIN_D4, true, Port.ResistorMode.Disabled, Port.InterruptMode.InterruptEdgeBoth);
                button5Port = new InterruptPort(Pins.GPIO_PIN_D5, true, Port.ResistorMode.Disabled, Port.InterruptMode.InterruptEdgeBoth);
                button6Port = new InterruptPort(Pins.GPIO_PIN_D6, true, Port.ResistorMode.Disabled, Port.InterruptMode.InterruptEdgeBoth);
                joystickButtonPort = new InterruptPort(Pins.GPIO_PIN_D2, true, Port.ResistorMode.Disabled, Port.InterruptMode.InterruptEdgeBoth);

                // Set up the interrupt handlers
                button3Port.OnInterrupt += new NativeEventHandler(button3InterruptHandler);
                button4Port.OnInterrupt += new NativeEventHandler(button4InterruptHandler);
                button5Port.OnInterrupt += new NativeEventHandler(button5InterruptHandler);
                button6Port.OnInterrupt += new NativeEventHandler(button6InterruptHandler);
                joystickButtonPort.OnInterrupt += new NativeEventHandler(joystickButtonInterruptHandler);

                // Initialize the button states
                internalButton3State = convertStateToButton(button3Port.Read());
                internalButton4State = convertStateToButton(button4Port.Read());
                internalButton5State = convertStateToButton(button5Port.Read());
                internalButton6State = convertStateToButton(button6Port.Read());
                internalJoystickButtonState = convertStateToButton(joystickButtonPort.Read());
            }
            else
            {
                // No, mark that we are not using interrupts.  We will always read the buttons when their states are requested.
                internalInterruptMode = false;
            }

            // Set up the analog ports
            verticalPositionInput = new AnalogInput(Pins.GPIO_PIN_A0);
            horizontalPositionInput = new AnalogInput(Pins.GPIO_PIN_A1);
        }

        private bool convertStateToButton(bool state)
        {
            // Is the button being pressed or released?  Just convert true to 1 and false to 0.  The other
            //   function will do the real logic conversion.
            return convertStateToButton((uint) ((state == true) ? 1 : 0));
        }

        /// <summary>
        /// Converts a uint state to a sensible boolean value.  Handles inverted logic where 0 is pressed and 1 is not pressed.
        /// </summary>
        /// <param name="state"></param>
        /// <returns></returns>
        private bool convertStateToButton(uint state)
        {
            // Is the button being pressed or released?
            bool pressed = (state == 0) ? true : false;

            return pressed;
        }

        private void button3InterruptHandler(uint port, uint state, DateTime time)
        {
            internalButton3State = convertStateToButton(state);
        }

        private void button4InterruptHandler(uint port, uint state, DateTime time)
        {
            internalButton4State = convertStateToButton(state);
        }

        private void button5InterruptHandler(uint port, uint state, DateTime time)
        {
            internalButton5State = convertStateToButton(state);
        }

        private void button6InterruptHandler(uint port, uint state, DateTime time)
        {
            internalButton6State = convertStateToButton(state);
        }

        private void joystickButtonInterruptHandler(uint port, uint state, DateTime time)
        {
            internalJoystickButtonState = convertStateToButton(state);
        }

        private bool getState(Port port, bool state)
        {
            // Are we using interrupts?
            if (interruptMode)
            {
                // Yes, just return the state
                return state;
            }
            else
            {
                // No, read the port
                return port.Read();
            }
        }

        private float getCoordinate(int analogValue)
        {
            //return (float)(analogValue - 512) / (float) 512 * (float) 100;
            return (float)analogValue;
        }
    }
}
