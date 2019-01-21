
package device;

/**
 *  The DeviceFactory interface specifies operations for creating virtual
 *  devices as part of the Abstract Factory pattern--it plays the role of
 *  the Abstract Factory in the pattern.
 *
 *   @author C. Fox
 *  @version 06/06
 */

public interface DeviceFactory {

      /* methods
      /**********/

      /**
       *  Create a new virtual sensor device.
       *
       *     @pre id != null
       *    @post @result is connected to hardware or simulated device
       *   @param id  The unique sensor identifier
       *  @throws IllegalArgumentException if precondition is violated
       */

   SensorDevice createSensorDevice( String id );

      /**
       *  Create a new virtual valve device.
       *
       *     @pre id != null
       *    @post @result is connected to hardware or simulated device
       *   @param id  The unique valve identifier
       *  @throws IllegalArgumentException if precondition is violated
       */

   ValveDevice createValveDevice( String id );

      /**
       *  Create a new virtual display device.
       *
       *     @pre name != null
       *    @post @result is connected to hardware or simulated device
       */

   DisplayDevice createDisplayDevice();

      /**
       *  Create a new virtual keypad device.
       *
       *     @pre name != null
       *    @post @result is connected to hardware or simulated device
       */

   KeypadDevice createKeypadDevice();

      /**
       *  Create a new virtual screen button device.
       *
       *     @pre name != null
       *    @post @result is connected to hardware or simulated device
       */

   ScreenButtonDevice createScreenButtonDevice();

      /**
       *  Create a new virtual clock device.
       *
       *     @pre name != null
       *    @post @result is connected to hardware or simulated device
       */

   ClockDevice createClockDevice();

      /**
       *  Create a new virtual persistent storage device.
       *
       *     @pre name != null
       *    @post @result is connected to hardware or simulated device
       */

   StorageDevice createStorageDevice();

} // DeviceFactory
