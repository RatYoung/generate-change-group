
package device;

/**
 *  The SensorDevice interface specifies the operations that any virtual moisture
 *  sensor must implement.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface SensorDevice {

      /**
       *  Obtain the current moisture level as a percentage of complete
       *  saturation.
       *
       *     @pre none
       *    @post @return is in range 0..100;
       *  @throws DeviceFailureException if the device fails
       */

   int read() throws DeviceFailureException;

} // SensorDevice
