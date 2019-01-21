
package device.sim;

/**
 *  A SimSensorDevice provides a device interface to a simulated hardware 
 *  moisture sensor (a SimSensor). The SimSensorDevice converts failure 
 *  signals from the simulated hardware into DeviceFailureExceptions.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import device.SensorDevice;
import device.DeviceFailureException;
import simulation.SimSensor;

class SimSensorDevice implements SensorDevice {

      /* attributes
      /*************/

   private final SimSensor sensor;   // the simulated hardware device

      /* constructors
      /***************/

      /**
       *  Create a device interface for a simulated hardware moisture sensor.
       *
       *     @pre none
       *    @post The device interface is initialized
       *   @param simSensor  The simulated hardware device
       */

   public SimSensorDevice( SimSensor simSensor ) {
      sensor = simSensor;
   }

      /* methods
      /**********/

      /**
       *  Obtain the current moisture level.
       *
       *     @pre none
       *    @post @return is the moisture level in range 0..100
       *  @throws DeviceFailureException if the device fails
       */

   public int read() throws DeviceFailureException {
      int result = sensor.getLevel();
      if ( result == -1 ) throw new DeviceFailureException();
      return result;
   }

} // SimSensorDevice
