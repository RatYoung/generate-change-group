
package device.sim;

/**
 *  A SimDeviceFactory creates instances of virtual device that use simulated
 *  devices. The simulated devices are all to be found in the simulation
 *  package, and mainly consist of Java Swing components.
 *
 *  The SimDeviceFactory is a concrete factory that realizes an abstract
 *  factory (DeviceFactory) in the Abstract Factory pattern.
 *
 *   @author C. Fox
 *  @version 07/06
 */

import device.*;
import simulation.Simulation;

public class SimDeviceFactory implements DeviceFactory {

      /* attributes
      /*************/

   private final Simulation simulation;   // the simulated environment

      /* constructors
      /***************/

      /**
       *  Create a new simulation virtual device factory.
       *
       *     @pre  sim is not null
       *    @post  device factory is initialized
       *   @param  sim AquaLush simulated environment
       *  @throws  IllegalArgumentException if precondition is violated
       */

   public SimDeviceFactory( Simulation sim ) {

      if ( sim == null ) throw new IllegalArgumentException();
      simulation = sim;
   }

      /* methods
      /**********/

      /**
       *  Create a SensorDevice connected to a particular SimSensor
       *  by way of the id (the unique SimSensor identifier)
       *
       *     @pre  none
       *    @post  a new SimSensorDevice is created that is connected to
       *           the designated SimSensor
       *   @param  id  the SimSensor identifier
       */

   public SensorDevice createSensorDevice( String id ) {
      return new SimSensorDevice( simulation.getSensor(id) );
   }

      /**
       *  Create a ValveDevice connected to a particular SimValve
       *  by way of the id (the unique SimValve identifier)
       *
       *     @pre  none
       *    @post  a new SimValveDevice is created that is connected to
       *           the designated SimValve
       *   @param  id  the SimValve identifier
       */

   public ValveDevice createValveDevice( String id ) {
      return new SimValveDevice( simulation.getValve(id) );
   }

      /**
       *  Return a virtual display device that can be used to manipulate a
       *  simulated display.
       *
       *     @pre none
       *    @post @return is a virtual device connected to a simulated device
       */

   public DisplayDevice createDisplayDevice() {
      return new SimDisplayDevice( simulation.getDisplay() );
   }

      /**
       *  Return a virtual keypad device that captures control panel
       *  keypresses on the simulated keypad.
       *
       *     @pre none
       *    @post @return is a virtual device connected to a simulated device
       */

   public KeypadDevice createKeypadDevice() {
      SimKeypadDevice result = new SimKeypadDevice();
      simulation.setKeypadListener( result );
      return result;
   }

      /**
       *  Return a virtual screen button device that captures control panel
       *  screen button presses of the simulated screen buttons.
       *
       *     @pre none
       *    @post @return is a virtual device connected to a simulated device
       */

   public ScreenButtonDevice createScreenButtonDevice() {
      SimScreenButtonDevice result = new SimScreenButtonDevice();
      simulation.setScreenButtonListener( result );
      return result;
   }

      /**
       *  Return a clock device that a clock can use to be notified of the
       *  passage of time.
       *
       *     @pre none
       *    @post @return is a virtual device connected to a simulated device
       */

   public ClockDevice createClockDevice() {
      return new SimClockDevice();
   }

      /**
       *  Return a simulated persistent storage device that really does nothing.
       *
       *     @pre none
       *    @post @return is a virtual device connected to a simulated device
       */

   public StorageDevice createStorageDevice() {
      return new SimStorageDevice();
   }

} // SimDeviceFactory
