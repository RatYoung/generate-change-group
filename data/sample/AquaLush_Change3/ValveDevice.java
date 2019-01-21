
package device;

/**
 *  The ValveDevice interface specifies the operations that any virtual valve
 *  must implement.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface ValveDevice {

      /**
       *  Open the valve.
       *
       *     @pre none
       *    @post the valve is open
       *  @throws DeviceFailureException if the device fails
       */

   void open() throws DeviceFailureException;

      /**
       *  Close the valve.
       *
       *     @pre none
       *    @post the valve is closed
       *  @throws DeviceFailureException if the device fails
       */

   void close() throws DeviceFailureException;

} // ValveDevice
