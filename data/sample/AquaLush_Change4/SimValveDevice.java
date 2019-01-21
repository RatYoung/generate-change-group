
package device.sim;

/**
 *  A SimValveDevice provides a device interface to a simulated hardware 
 *  irrigation valve (a SimValve). The SimValveDevice converts failure 
 *  signals from the simulated hardware into DeviceFailureExceptions.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import device.ValveDevice;
import device.DeviceFailureException;
import simulation.SimValve;

class SimValveDevice implements ValveDevice {

      /* attributes
      /*************/

   private final SimValve valve;      // the simulated hardware

      /* constructors
      /***************/

      /**
       *  Create a device interface for a simulated hardware valve.
       *
       *     @pre none
       *    @post The device interface is initialized
       *   @param simValve  The simulated hardware device
       */

   public SimValveDevice( SimValve simValve ) {
      valve = simValve;
   }

      /* methods
      /**********/

      /**
       *  Open the valve. Opening an open valve has no effect.
       *
       *     @pre none
       *    @post the valve is open
       *  @throws DeviceFailureException if the device fails
       */

   public void open() throws DeviceFailureException {
      int result = valve.open();
      if ( result == -1 ) throw new DeviceFailureException();
   }

      /**
       *  Close the valve. Closing a closed valve has no effect.
       *
       *     @pre none
       *    @post the valve is closed
       *  @throws DeviceFailureException if the device fails
       */

   public void close() throws DeviceFailureException {
      int result = valve.close();
      if ( result == -1 ) throw new DeviceFailureException();
   }

} // SimValveDevice
